package com.esteel.framework.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.esteel.framework.dao.BaseDao;
import com.esteel.framework.entity.BaseEntity;
import com.esteel.framework.mybatis.SqlInterceptor;
import com.esteel.framework.service.BaseService;
import com.esteel.framework.mybatis.Sort;
import com.esteel.framework.mybatis.complexQuery.CustomQueryParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @version 1.0.0
 * @ClassName BaseServiceImpl.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
@Transactional
@SuppressWarnings("all")
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private BaseDao<T> baseDao;

	@Override
	public List<T> getAll() {
		return baseDao.getAll();
	}

	@Override
	public T getById(String id) {
		return baseDao.getById(id);
	}
	
	@Override
	public T getOne(T t) {
		return baseDao.getOne(t);
	}
	@Override
	public int count(T params) {
		return baseDao.count(params);
	}

	@Override
	public int countLike(T findParams) {
		return baseDao.countLike(findParams);
	}

    @Override
    public int countQuery(List<CustomQueryParam> customQueryParams) {
        return baseDao.countQuery(customQueryParams);
    }

    @Override
    public List<T> query(List<CustomQueryParam> customQueryParams) {
        return baseDao.query(customQueryParams, null);
    }

    @Override
    public List<T> query(List<CustomQueryParam> customQueryParams, Integer start, Integer limit, List<Sort> sortList) {
        if (start != null && limit != null) {
		    SqlInterceptor.setRowBounds(new RowBounds(start, limit));
        }
        return baseDao.query(customQueryParams, sortList);
    }

    @Override
	public List<T> find(T findParams, Integer start, Integer limit) {
        if (start != null && limit != null) {
            SqlInterceptor.setRowBounds(new RowBounds(start, limit));
        }
		return baseDao.find(findParams);
	}
	
	@Override
	public List<T> findByObj(T findParams) {
		return baseDao.find(findParams);
	}
	
	@Override
	public List<T> getByObj(T findParams) {
		return baseDao.get(findParams);
	}

	@Override
	@Transactional(readOnly = false)
	public void insert(T t) throws RuntimeException {
		if (baseDao.insert(t) != 1) {
			throw new RuntimeException();
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void insert(List<T> list) throws RuntimeException {
//		baseDao.insertBatch(list);
		for (T t : list) {
            insert(t);
		}
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void insertBatch(List<T> list) throws RuntimeException {
		baseDao.insertBatch(list);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteById(String id) throws RuntimeException {
		if (baseDao.delete(id) != 1) {
			throw new RuntimeException();
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteById(List<String> list) throws RuntimeException {
		for (String id : list) {
            deleteById(id);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(T t) throws RuntimeException {
		if (baseDao.deleteByPrimaryKey(t) != 1) {
			throw new RuntimeException();
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(List<T> list) throws RuntimeException {
		for (T t : list) {
			delete(t);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void update(T t) throws RuntimeException {
		if (baseDao.update(t) != 1) {
			throw new RuntimeException();
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void update(List<T> list) throws RuntimeException {
		for (T t : list) {
			update(t);
		}
	}

    @Override
    public List<T> findForExport(JSONObject queryParams) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException{
        return baseDao.getAll();
    }

    @Override
    public int countForExport(JSONObject queryParams) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException{
        return 0;
    }

    @Override
    public void deleteAll() throws RuntimeException {
        baseDao.deleteAll();
    }

    /**
     * 对查询的对象，如果类型是String 加上%value%
     * @param findParams
     * @param cls
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public void addLike(T findParams, Class cls) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String methodName = "";
        Method[] methods = cls.getMethods();
        for(Method method:methods){
            methodName = method.getName();
            if(StringUtils.isNotBlank(methodName)){
                if(methodName.indexOf("get") < 0){
                    continue;
                }
                if(method.getReturnType() != String.class){
                    continue;
                }
                Object value  = method.invoke(findParams,null);
                if(value == null || StringUtils.isBlank(String.valueOf(value))){
                    continue;
                }

                Method setMethod = cls.getMethod(methodName.replace("get","set"),String.class);
                if(setMethod == null){
                    continue;
                }
                setMethod.invoke(findParams,"%"+value+"%");
            }
        }
    }

    public void addLike(JSONObject findParamsMap){
        if(findParamsMap == null)
        {
            return;
        }
        Iterator<String> it = findParamsMap.keySet().iterator();
        String key = "";
        while(it.hasNext()){
            key = it.next();
            if(findParamsMap.get(key) != null){
                Object value = findParamsMap.get(key);
                if("java.lang.String".equals(value.getClass().getName()) &&
                        StringUtils.isNotBlank(findParamsMap.getString(key))){
                    findParamsMap.put(key,"%"+findParamsMap.getString(key)+"%");
                }
            }
        }
    }
}
