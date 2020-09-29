package com.esteel.common.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0.0
 * @ClassName IDGeneratorWithMilis.java
 * @author: liu Jie
 * @Description: 返回的ID的时间戳信息支持到毫秒级别
 * @createTime: 2020年-05月-19日  13:29
 */
public class IDGeneratorWithMilis extends IDGeneratorWithTimestamp {
    private Logger log = LoggerFactory.getLogger(IDGeneratorWithMilis.class);

    public IDGeneratorWithMilis() {
        super("", IDDatePattern.PATTERN_MILLIS, 2);
    }

    public IDGeneratorWithMilis(String prefix) {
        super(prefix, IDDatePattern.PATTERN_MILLIS, 2);
    }

    public IDGeneratorWithMilis(String prefix, int bits) {
        super(prefix, IDDatePattern.PATTERN_MILLIS, bits);
    }

}
