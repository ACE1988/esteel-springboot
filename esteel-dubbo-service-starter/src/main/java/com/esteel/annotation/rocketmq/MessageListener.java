package com.esteel.annotation.rocketmq;

/**
 * @version 1.0.0
 * @ClassName MessageListener.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public interface MessageListener {
    Action consume(Message message, ConsumeContext context);
}
