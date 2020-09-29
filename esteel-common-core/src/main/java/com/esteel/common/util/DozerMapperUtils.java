package com.esteel.common.util;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0.0
 * @ClassName DozerMapperUtils.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class DozerMapperUtils {

    private static Mapper mapper = new DozerBeanMapper();

    public static <T> List<T> transforList(List<?> sources, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (sources == null) {
            return list;
        }
        for (Object o : sources) {
            T t = transfor(o, clazz);
            list.add(t);
        }
        return list;
    }

    public static void transfor(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        mapper.map(source, target);
    }

    public static <T> T transfor(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        return mapper.map(source, target);
    }
}
