package com.esteel.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.esteel.framework.entity.BaseEntity;
import com.esteel.framework.mybatis.Sort;
import com.esteel.framework.mybatis.complexQuery.CustomQueryParam;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface BaseService<T extends BaseEntity> {
	
	public List<T> getAll();
	
	public T getById(String id);

	public int count(T params);

	public int countLike(T findParams);

    public int countQuery(List<CustomQueryParam> customQueryParams);

    public List<T> query(List<CustomQueryParam> customQueryParams);

    public List<T> query(List<CustomQueryParam> customQueryParams, Integer start, Integer limit, List<Sort> sortList);

	public List<T> find(T findParams, Integer start, Integer limit);
	
	public List<T> findByObj(T findParams);

	public void insert(T t) throws RuntimeException;
	
	public void insert(List<T> list) throws RuntimeException;

	void insertBatch(List<T> list) throws RuntimeException;

	public void deleteById(String id) throws RuntimeException;

	public void deleteById(List<String> list) throws RuntimeException;

	public void delete(T t) throws RuntimeException;

	public void delete(List<T> list) throws RuntimeException;

    public void deleteAll() throws RuntimeException;

	public void update(T t) throws RuntimeException;
	
	public void update(List<T> list) throws RuntimeException;

//    public void export(OutputStream outputStream, String sheetName, JSONArray columns,JSONObject queryFilter) throws IOException, WriteException, InvocationTargetException,
//            IllegalAccessException,
//            NoSuchMethodException;

    public List<T> findForExport(JSONObject jsonParams) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;

    public int countForExport(JSONObject queryParams) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
    
    public List<T> getByObj(T findParams) ;
    
    public T getOne(T t);


}
