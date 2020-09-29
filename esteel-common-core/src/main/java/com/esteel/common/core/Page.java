package com.esteel.common.core;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @version 1.0.0
 * @ClassName Page.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:43
 */
public class Page<T> implements Serializable {

    private List<T> items ;

    //页码
    private int pageNo  ;

    //每页数
    private int pageSize ;

    //总条数
    private int totalCount ;


    public Page(List<T> items,int pageNo,int pageSize){
        this.items = items ;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }


    public Page(List<T> items,int pageNo,int pageSize,int totalCount){
        this.items = items ;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount= totalCount ;
    }

    public <R> Page<R> map(Function<T,R> function){
        List<R> values = items == null ? null: items.stream().map(function).collect(Collectors.toList());
        return new Page<R>(values,pageNo,pageSize,totalCount);
    }


    public Integer getHeaderOffset() {
        return (pageNo - 1) * pageSize;
    }

    public int getFooterOffset() {
        return pageNo * pageSize - 1;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

}
