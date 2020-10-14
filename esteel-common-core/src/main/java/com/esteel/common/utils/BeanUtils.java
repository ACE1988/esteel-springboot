package com.esteel.common.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0.0
 * @ClassName BeanUtils.java
 * @author: liu Jie
 * @Description: 属性转换工具类
 * @createTime: 2020年-10月-13日  10:40
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

    public BeanUtils(){

    }

    public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
        org.apache.commons.beanutils.BeanUtils.copyProperties(dest, orig);
    }

    public static void copyOnPropertyUtils(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtils.copyProperties(dest, orig);
    }

    public static Map<String, Object> beanToMap(Object entity) {
        Map<String, Object> parameter = new HashMap();
        Field[] fields = entity.getClass().getDeclaredFields();

        for(int i = 0; i < fields.length; ++i) {
            String fieldName = fields[i].getName();
            Object o = null;
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + fieldName.substring(1);

            try {
                Method getMethod = entity.getClass().getMethod(getMethodName);
                o = getMethod.invoke(entity);
            } catch (Exception var10) {
                var10.printStackTrace();
            }

            if (o != null) {
                parameter.put(fieldName, o);
            }
        }

        return parameter;
    }

    static {
        ConvertUtils.register(new DateConvert(), Date.class);
        ConvertUtils.register(new DateConvert(), java.sql.Date.class);
        ConvertUtils.register(new DateConvert(), Timestamp.class);
        ConvertUtils.register(new BigDecimalConvert(), BigDecimal.class);
    }
}
