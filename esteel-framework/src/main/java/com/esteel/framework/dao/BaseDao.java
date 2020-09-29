package com.esteel.framework.dao;

import com.esteel.framework.entity.BaseEntity;
import com.esteel.framework.mybatis.BaseProvider;
import com.esteel.framework.mybatis.Sort;
import com.esteel.framework.mybatis.complexQuery.CustomQueryParam;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;

import java.util.List;

/**
 * @version 1.0.0
 * @ClassName BaseDao.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public interface BaseDao<T extends BaseEntity> {
	
	@SelectProvider(type = BaseProvider.class, method = "getAll")
	@Options(flushCache = FlushCachePolicy.DEFAULT,useCache = true)
	@ResultMap("getMap")
	public List<T> getAll();

	@SelectProvider(type = BaseProvider.class, method = "getById")
	@Options(flushCache = FlushCachePolicy.DEFAULT,useCache = true)
	@ResultMap("getMap")
	public T getById(String id);

	@SelectProvider(type = BaseProvider.class, method = "count")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	public int count(T params);

	@SelectProvider(type = BaseProvider.class, method = "countLike")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	public int countLike(T findParams);

	@SelectProvider(type = BaseProvider.class, method = "countQuery")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	public int countQuery(@Param("queryParams") List<CustomQueryParam> customQueryParams);
	
	@SelectProvider(type = BaseProvider.class, method = "get")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	@ResultMap("getMap")
	public T getOne(T findParams);
	
	@SelectProvider(type = BaseProvider.class, method = "query")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	@ResultMap("getMap")
	public List<T> query(@Param("queryParams") List<CustomQueryParam> customQueryParams, @Param("sortList") List<Sort> sortList);

	@SelectProvider(type = BaseProvider.class, method = "get")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	@ResultMap("getMap")
	public List<T> get(T findParams);

	@SelectProvider(type = BaseProvider.class, method = "find")
	@Options(flushCache = FlushCachePolicy.FALSE,useCache = true)
	@ResultMap("getMap")
	public List<T> find(T findParams);

	@InsertProvider(type = BaseProvider.class, method = "insert")
	@Options(keyProperty = "id",flushCache = FlushCachePolicy.TRUE)
	public int insert(T t);

	@InsertProvider(type = BaseProvider.class, method = "insertBatch")
	@Options(keyProperty = "id",flushCache = FlushCachePolicy.TRUE)
	public int insertBatch(@Param("list") List<T> list);
	
	@DeleteProvider(type = BaseProvider.class, method = "delete")
	@Options(flushCache = FlushCachePolicy.TRUE)
	public int delete(String id);

	@DeleteProvider(type = BaseProvider.class, method = "deleteByPrimaryKey")
	@Options(flushCache = FlushCachePolicy.TRUE)
	public int deleteByPrimaryKey(T t);

	@UpdateProvider(type = BaseProvider.class, method = "update")
	@Options(flushCache = FlushCachePolicy.TRUE)
	public int update(T t);

    @DeleteProvider(type = BaseProvider.class,method = "deleteAll")
	@Options(flushCache = FlushCachePolicy.TRUE)
    public int deleteAll();

}