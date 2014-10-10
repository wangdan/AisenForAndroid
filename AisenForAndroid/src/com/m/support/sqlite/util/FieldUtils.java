package com.m.support.sqlite.util;

import java.lang.reflect.Field;

import com.alibaba.fastjson.JSON;
import com.m.common.utils.Logger;
import com.m.support.sqlite.annotation.Id;

public class FieldUtils {

//	private static JSON gson = new JSON();

	public static final String OWNER = "com_m_common_owner";

	public static final String KEY = "com_m_common_key";

	public static final String CREATEAT = "com_m_common_createat";

	public static String getColumnByField(Field field) {

		return String.format(" %s %s ", getColumnNameByField(field), getColumnType(field));
	}

	/**
	 * 获取这个字段定义的表字段名称
	 * 
	 * @param field
	 * @return
	 */
	public static String getColumnNameByField(Field field) {
		// 是否是主键
		Id id = field.getAnnotation(Id.class);
		if (id != null)
			return id.column();

		return field.getName();
	}

	/**
	 * 属性是否是基础类型java.lang.*
	 * 
	 * @param field
	 * @return
	 */
//	public static boolean isBaseDateType(Field field) {
//		Class<?> clazz = field.getType();
//		return field.getType().isPrimitive() || clazz.equals(Integer.class) || clazz.equals(Byte.class)
//				|| clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class)
//				|| clazz.equals(Short.class) || clazz.equals(Boolean.class);
//	}
	
	/**
	 * 获取属性的表字段类型，当前的所有字段都定义为TEXT
	 * 
	 * @param field
	 * @return
	 */
	public static String getColumnType(Field field) {
		return " TEXT ";
	}

	/**
	 * 数据转换成json字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String value2Json(Object value) {
		return JSON.toJSONString(value);
	}

	/**
	 * json字符串转换成数据
	 * 
	 * @param json
	 * @param field
	 * @return
	 */
	public static <T> T json2Value(String json, Field field) {
		Logger.v(String.format("json=%s,clazz=%s", json, field.getClass().getSimpleName()));
		try {
			return JSON.parseObject(json, field.getGenericType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) json;
	}

}
