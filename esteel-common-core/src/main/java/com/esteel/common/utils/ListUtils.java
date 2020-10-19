package com.esteel.common.utils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author liujie
 * @version 1.0.0
 * @ClassName StringListToIntegerListUtils.java
 * @Description TODO
 * @createTime 2020年5月20日 14:07
 */
public class ListUtils {

    public static <T, U> List<U> convertStringListToIntList(List<T> listOfString, Function<T, U> function)
    {
        return listOfString.stream()
                .map(function)
                .collect(Collectors.toList());
    }

    /**
     *  去重
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
