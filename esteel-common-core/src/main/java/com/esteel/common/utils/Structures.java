package com.esteel.common.utils;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @version 1.0.0
 * @ClassName Structures.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class Structures {

    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        return list == null ?
                Lists.newArrayList() :
                list.stream().map(function).collect(Collectors.toList());
    }
}
