package com.esteel.framework.util;


import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtils {

	private static ConcurrentHashMap<Class,PropertyDescriptor[]> map = new ConcurrentHashMap();

	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> type) {
		PropertyDescriptor[] propertyDescriptors = map.get(type);
		if(propertyDescriptors==null){
			propertyDescriptors = org.springframework.cglib.core.ReflectUtils.getBeanSetters(type);
			map.putIfAbsent(type,propertyDescriptors);
		}
		return map.get(type);
	}
	
	public static Method getBeanGetter(Class<?> type, String property) throws SecurityException, NoSuchMethodException {
		String methodName = null;
		if (property.length() == 1) {
			methodName = property.substring(0, 1).toUpperCase();
		} else {
			methodName = property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
		}
		methodName = "get" + methodName;
		return type.getMethod(methodName);
	}

    public static Field getFieldByGetter(Class<?> modelClass, String getterName) throws NoSuchFieldException {
        String propName = StringUtils.uncapitalize(getterName.substring(3));
        return modelClass.getDeclaredField(propName);
    }
}