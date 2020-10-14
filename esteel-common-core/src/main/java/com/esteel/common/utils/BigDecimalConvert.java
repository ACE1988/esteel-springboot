package com.esteel.common.utils;

import org.apache.commons.beanutils.Converter;

/**
 * @version 1.0.0
 * @ClassName BigDecimalConvert.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-10月-13日  10:43
 */
public class BigDecimalConvert  implements Converter {


    public BigDecimalConvert() {
    }

    @Override
    public Object convert(Class type, Object value) {
        return value == null ? null : value;
    }
}
