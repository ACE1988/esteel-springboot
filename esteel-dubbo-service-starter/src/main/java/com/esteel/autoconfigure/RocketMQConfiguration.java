package com.esteel.autoconfigure;

import com.alibaba.fastjson.JSONObject;
import com.esteel.annotation.rocketmq.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Method;
import java.util.*;


@Configuration
@Aspect
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMQConfiguration implements InitializingBean {

  private Logger logger = LoggerFactory.getLogger(RocketMQConfiguration.class);

  @Autowired
  private RocketMqProperties properties;

  @Autowired
  private ConfigurableApplicationContext applicationContext;
  @Autowired
  private ConfigurableEnvironment env;

  private DefaultMQProducer producer;
  private DefaultMQPushConsumer consumer;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.setupMq();
  }

  private void setupMq() throws MQClientException {
    //创建消息消费者
    createConsumer();
    //创建消息生产者
    createProduce();
  }

  private void createProduce() throws MQClientException {
    producer = new DefaultMQProducer(properties.getProducer().getId());
    producer.setNamesrvAddr(properties.getProducer().getServerAddress());
    producer.setVipChannelEnabled(false);
    producer.start();
    logger.info("MQ Producer: id {}, server {}", properties.getProducer().getId(), properties.getProducer().getServerAddress());
  }


  private void createConsumer() throws MQClientException {
    consumer = new DefaultMQPushConsumer(properties.getConsumer().getId());
    consumer.setNamesrvAddr(properties.getConsumer().getServerAddress());
    consumer.setMessageModel(MessageModel.CLUSTERING); //集群模式

    consumer.setConsumeThreadMin(properties.getConsumer().getMinThread());
    consumer.setConsumeThreadMax(properties.getConsumer().getMaxThread());

    //一次只消费1条消息
    consumer.setConsumeMessageBatchMaxSize(1);
    //设置消费者端消息拉取策略，表示从哪里开始消费
    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

    logger.info("MQ Consumer: id {}, server {}", properties.getConsumer().getId(), properties.getConsumer().getServerAddress());

    // 注册监听
    ConsumerListener consumerListener = new ConsumerListener();
    consumer.registerMessageListener(consumerListener);
    Map<String, MessageListener> topicListeners = new HashMap<>();

    Map<String, MqSubscription> subscriptions = new HashMap<>();

    for (String beanName : applicationContext.getBeansWithAnnotation(MQ.class).keySet()) {
      Object bean = applicationContext.getBean(beanName);
      Method[] methods = AopUtils.getTargetClass(bean).getDeclaredMethods();
      for (Method m : methods) {
        if (m.isAnnotationPresent(Consume.class)) {
          Consume subscription = m.getAnnotation(Consume.class);
          String topic = env.resolvePlaceholders(subscription.topic());
          String tag = env.resolvePlaceholders(subscription.tags());

          if (!subscriptions.containsKey(topic)) {
            subscriptions.put(topic, new MqSubscription());
            subscriptions.get(topic).setTopic(topic);
          }
          if (!subscriptions.get(topic).getTags().containsKey(tag)) {
            subscriptions.get(topic).getTags().put(tag, new MqSubscriptionTag());
            subscriptions.get(topic).getTags().get(tag).setTag(tag);
          }

          MqInvocation inv = new MqInvocation();
          inv.setBean(bean);
          inv.setMethod(m);
          inv.setBodyType(m.getParameters()[0].getType());
          subscriptions.get(topic).getTags().get(tag).getInvocations().add(inv);

          logger.info("Register MQ consumer: topic {}, tags {}, method {}", topic, tag, m);
        }
      }
    }

    for (String topic : subscriptions.keySet()) {
      String tags = StringUtils.join(subscriptions.get(topic).getTags().keySet(), "||");
      consumer.subscribe(topic, tags);
      topicListeners.put(topic, new MethodMessageListener(subscriptions.get(topic)));
    }

    consumerListener.setTopicListeners(topicListeners);
    consumer.start();
  }


  @Around("@annotation(com.esteel.annotation.rocketmq.Produce)")
  public Object produceProxy(ProceedingJoinPoint pjp)
      throws Throwable {

    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    Produce produce = method.getAnnotation(Produce.class);

    Object result = pjp.proceed();

    String json = JSONObject.toJSONString(result);

    String topic = env.resolvePlaceholders(produce.topic());
    String tags = env.resolvePlaceholders(produce.tags());

    org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message(topic, tags, json.getBytes("UTF-8"));
    msg.setKeys(UUID.randomUUID().toString());
    msg.setWaitStoreMsgOK(false);

    producer.send(msg, new SendCallback() {
      @Override
      public void onSuccess(SendResult sendResult) {
        logger.info("Send MQ message: {} {} {}, result: {}", topic, tags, json, sendResult);
      }

      @Override
      public void onException(Throwable throwable) {
    	throwable.printStackTrace();
        logger.error("Send MQ Exception", throwable);
      }
    });

    return result;
  }
}


class MethodMessageListener implements MessageListener {

  private static final ObjectMapper OM;

  static {
    OM = new ObjectMapper();
    OM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  private final MqSubscription subscription;
  private Logger logger = LoggerFactory.getLogger(MethodMessageListener.class);

  public MethodMessageListener(MqSubscription mqSubscription) {
    this.subscription = mqSubscription;
  }

  @Override
  public Action consume(Message message, ConsumeContext context) {
    try {
    	logger.info("Receive MQ message: {}-{}:{}",message.getTopic(), message.getTag(), new String(message.getBody()));
      Object body = new String(message.getBody());
      for (String tag : subscription.getTags().keySet()) {
        if (tag.equals(message.getTag())) {
          processInvocation(subscription.getTags().get(tag), body);
        }
      }
    } catch (Exception e) {
      logger.error("Error in consume message", e);
      return Action.CommitMessage;
    }
    return Action.CommitMessage;
  }

  private void processInvocation(MqSubscriptionTag subs, Object body) {
    for (MqInvocation inv : subs.getInvocations()) {
      try {
        if (inv.getBodyType() != String.class && body instanceof String) {
          body = JSONObject.parseObject((String) body, inv.getBodyType());
        }
        inv.getMethod().invoke(inv.getBean(), body);
      } catch (Exception e) {
        logger.error("Error in process message consume, " + inv.getBean().getClass() + " " + inv.getMethod(), e);
      }
    }
  }
}

@Data
class MqSubscription {

  private String topic;
  // tag name, invocations
  private Map<String, MqSubscriptionTag> tags = new HashMap<>();
}

@Data
class MqSubscriptionTag {

  private String tag;
  private List<MqInvocation> invocations = new ArrayList<>();
}

@Data
class MqInvocation {

  private Object bean;
  private Method method;
  private Class bodyType;
}
