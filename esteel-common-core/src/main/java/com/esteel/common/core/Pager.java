package com.esteel.common.core;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @version 1.0.0
 * @ClassName Pager.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:50
 */
public class Pager<T> implements Iterable<T>, Iterator<T> {


    private int currentPage = 0;
    private List<T> records = new LinkedList<>();
    private int pageSize;
    private boolean hasMorePage = true;

    private int currentIndex;

    private BiFunction<Integer, Integer, Page<T>> function;

    public Pager(int pageSize, BiFunction<Integer, Integer, Page<T>> function) {
        this.pageSize = pageSize;
        this.function = function;

        this.currentIndex = 0;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Page<T> specified(int pageNo, int pageSize) throws ProcessBizException {
        return specifiedPage(pageNo, pageSize);
    }

    public int forEachPage(BiConsumer<List<T>, Subscriber> consumer) {
        return forEachPage(-1, consumer);
    }

    public int forEachPage(int maxPage, BiConsumer<List<T>, Subscriber> consumer) {
        int totalCount = 0;
        int currentPage = 0;
        Subscriber subscriber = new Subscriber();
        while (hasMorePage) {
            if (maxPage > 0 && currentPage == maxPage) {
                break;
            }
            Page<T> page = nextPage();
            List<T> results = page == null ? null : page.getItems();
            hasMorePage = results != null && results.size() > pageSize;
            if (results != null && results.size() > 0) {
                consumer.accept(results, subscriber);
                totalCount += results.size();
            }
            currentPage ++;
        }
        if (!subscriber.suppliers.isEmpty()) {
            for (Supplier<Boolean> supplier : subscriber.suppliers) {
                supplier.get();
            }
        }
        return totalCount;
    }

    @Override
    public boolean hasNext() {
        boolean emptyStack = records.size() == 0;
        if (emptyStack && hasMorePage) {
            Page<T> page = nextPage();
            Collection<T> results = page == null ? null : page.getItems();
            hasMorePage = results != null && results.size() > pageSize;
            if (results != null && results.size() > 0) {
                records.addAll(results);
            }
        }
        return records.size() > 0 || hasMorePage;
    }

    @Override
    public T next() {
        T t = records.remove(0);
        this.currentIndex ++;
        return t;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    private Page<T> nextPage() {
        return function.apply(++ currentPage, pageSize + 1);
    }

    private Page<T> specifiedPage(int pageNo, int pageSize) throws ProcessBizException {
        return function.apply(pageNo, pageSize);
    }

    public static class Subscriber {

        private List<Supplier<Boolean>> suppliers = Lists.newArrayList();

        public void onComplete(Supplier<Boolean> supplier) {
            suppliers.add(supplier);
        }
    }

}
