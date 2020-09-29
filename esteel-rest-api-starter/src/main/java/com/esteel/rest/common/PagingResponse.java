package com.esteel.rest.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.esteel.common.core.Page;
import com.esteel.common.core.Pager;
import com.esteel.common.core.ProcessBizException;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @version 1.0.0
 * @ClassName PagingResponse.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class PagingResponse<T> extends RestResponse<Page<T>> {

    public static <T> PagingResponse<T> paging(BiFunction<Integer, Integer, Page<T>> function, int currentPage, int currentSize) {
        return paging(new Pager<>(100, function), currentPage, currentSize, null);
    }

    public static <T> PagingResponse<T> paging(Pager<T> pages, int currentPage, int currentSize) {
        return paging(pages, currentPage, currentSize, null);
    }

    public static <T> PagingResponse<T> paging(BiFunction<Integer, Integer, Page<T>> function, int currentPage, int currentSize, PagingView view) {
        return paging(new Pager<>(100, function), currentPage, currentSize, view);
    }

    public static <T> PagingResponse<T> paging(Pager<T> pages, int currentPage, int currentSize, PagingView view) {
        return new PagingResponse<>(pages, currentPage, currentSize, view);
    }

    @JsonIgnore
    private Pager<T> pages;

    @JsonIgnore
    private int currentPage;

    @JsonIgnore
    private int currentSize;

    @JsonIgnore
    private boolean initialized;

    @JsonIgnore
    private PagingView view;

    @JsonIgnore
    private Map<String, Object> attributes;

    private PagingResponse(Pager<T> pages, int currentPage, int currentSize, PagingView view) {
        super(SUCCESS_CODE, null);
        this.pages = pages;
        this.currentPage = currentPage;
        this.currentSize = currentSize;
        this.initialized = false;
        this.view = view;
        this.attributes = Maps.newHashMap();
    }

    public PagingView getView() {
        return this.view;
    }

    public PagingResponse<T> putAttribute(String key, Object value) {
        this.attributes.put(key, value);
        return this;
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    @JsonProperty("code")
    public String getCode() {
        initialize();
        return super.getCode();
    }

    @Override
    @JsonProperty("content")
    public Page<T> getContent() {
        initialize();
        return super.getContent();
    }

    @Override
    @JsonProperty("error")
    public RestResponseError getError() {
        initialize();
        return super.getError();
    }

    public Pager<T> getPages() {
        return pages;
    }

    private void initialize() {
        if (initialized) {
            return;
        }
        try {
            Page<T> page = pages.specified(currentPage, currentSize);
            super.setContent(page);
        } catch (ProcessBizException e) {
            super.setCode(e.getCode().getCode());
            super.setError(new RestResponseError(e.getCode().getMessage(), e.getCode().getMessage(), e.getErrors()));
        } finally {
            initialized = true;
        }
    }
}
