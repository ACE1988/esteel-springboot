package com.esteel.annotation.rocketmq;


import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConsumerListener implements MessageListenerOrderly {
    private Logger logger = LoggerFactory.getLogger(ConsumerListener.class);
    /** Key is topic name. */
    private Map<String, MessageListener> topicListeners;

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
        //一次只消费1条消息
        if (list.size() > 1) {
            logger.error("消费消息异常，消息数量大于1:", list.size());
            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }

        MessageExt msg = list.get(0);
        MessageListener listener = topicListeners.get(msg.getTopic());
        if (listener != null) {
            Action action = listener.consume(new Message(msg), new ConsumeContext());
            switch (action) {
                case CommitMessage:
                    return ConsumeOrderlyStatus.SUCCESS;
                case ReconsumeLater:
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                default:
                    return ConsumeOrderlyStatus.SUCCESS;
            }
        } else {
            logger.error("无匹配的消息监听，topic={}", msg.getTopic());
        }

        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
    }

    public Map<String, MessageListener> getTopicListeners() {
        return topicListeners;
    }

    public void setTopicListeners(Map<String, MessageListener> topicListeners) {
        this.topicListeners = topicListeners;
    }
}
