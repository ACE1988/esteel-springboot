package com.esteel.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0.0
 * @ClassName RocketMqProperties.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Configuration
@ConfigurationProperties(prefix = "mq")
@Data
public class RocketMqProperties {

    @Data
    public static class Consumer {
        private String id;
        private String serverAddress;
        private int minThread = 5;
        private int maxThread = 5;
        private int messageBatchMaxSize = 1;
        private String fromWhere;
    }

    @Data
    public static class Producer {
        private String id;
        private String serverAddress;
    }

    private Consumer consumer;
    private Producer producer;
}

