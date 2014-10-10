package com.m.support.sqlite.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.text.TextUtils;

import com.m.common.settings.Setting;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.Logger;
import com.m.support.sqlite.annotation.Id;
import com.m.support.sqlite.annotation.Table;
import com.m.support.sqlite.property.Extra;
import com.m.support.sqlite.property.Property;
import com.m.support.sqlite.property.TableInfo;

public class ClassUtils {

	private static final String TAG = ClassUtils.class.getSimpleName();

	/**
	 * 获取Class的数据库定义的表名称
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table == null || table.name().trim().length() == 0) {
			// 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
			return clazz.getName().replace('.', '_');
//			return clazz.getSimpleName();
		}
		return table.name();
	}

	/**
	 * 获取Class的主键字段<br/>
	 * 可以注解标注字段，也可以在xml配置
	 * 
	 * @param clazz
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<?> clazz) {
		Field primaryKeyField = null;
		Field[] fields = clazz.getDeclaredFields();

		Setting setting = SettingUtility.getSetting(clazz.getName());
		String primaryKeyFieldName = null;
		if (setting != null)
			primaryKeyFieldName = setting.getExtras().get("id").getValue();

		Logger.v(TAG, String.format("primaryKey setting --->%s", primaryKeyFieldName + ""));

		if (fields != null) {

			for (Field field : fields) { // 获取ID注解
				Logger.v(TAG, String.format("getPrimaryKey field'name = %s", field.getName()));

				if (field.getName().equals(primaryKeyFieldName) || field.getAnnotation(Id.class) != null) {
					primaryKeyField = field;
					break;
				}
			}

		} else {
			throw new RuntimeException("this model[" + clazz + "] has no field");
		}

		if (primaryKeyField == null && clazz.getName().indexOf("Object") == -1)
			return getPrimaryKeyField(clazz.getSuperclass());

		if (primaryKeyField == null)
			throw new RuntimeException("this model[" + clazz + "] has no primaryKey");

		return primaryKeyField;
	}

	/**
	 * 获取Class的所有属性
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Property> getPropertyList(Class<?> clazz) {

		List<Property> plist = new ArrayList<Property>();
		try {
			Field[] fs = getFields(clazz, null);
			Field primaryKeyField = getPrimaryKeyField(clazz);
			for (Field f : fs) {
				// 过滤主键
				if (f.getName().equals(primaryKeyField.getName()))
					continue;

				if ("serialVersionUID".equals(f.getName()))
					continue;

//				if (FieldUtils.isBaseDateType(f)) {
				Property property = new Property();

				property.setColumn(FieldUtils.getColumnByField(f));
				property.setField(f);
				property.setDataType(f.getType());
				property.setField(f);

				plist.add(property);
//				}
			}
			return plist;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Field[] getFields(Class c, Field[] fields) {
		if (fields == null)
			fields = new Field[0];
		if (c == null)
			return fields;
		Field f[] = c.getDeclaredFields();
		Field nFs[];
		if (fields.length == 0) {
			nFs = f;
		} else if (f.length == 0) {
			nFs = fields;
		} else {
			int oLen = fields.length;
			int nLen = f.length;
			nFs = Arrays.copyOf(fields, oLen + nLen);
			System.arraycopy(f, 0, nFs, oLen, nLen);
		}
		return getFields(c.getSuperclass(), nFs);
	}

	/**
	 * 将Object转换成ContentValues插入获取更新数据
	 * 
	 * @param extra
	 * @param entity
	 * @return
	 */
	public static ContentValues getContentValues(TableInfo tableInfoUtils, Extra extra, Object entity) {
		TableInfo tableInfo = tableInfoUtils.get(entity.getClass());
		Map<String, Property> pList = tableInfo.propertyMap;

		ContentValues values = new ContentValues();

		try {
			// 主键
			tableInfo.getId().getField().setAccessible(true);
			if (tableInfo.getId().getField().getType().equals(String.class) || tableInfo.getId().getField().getType().equals(Enum.class)
					|| tableInfo.getId().getField().getType().isPrimitive()) {
				values.put(FieldUtils.getColumnNameByField(tableInfo.getId().getField()), tableInfo.getId().getField().get(entity).toString());
			} else {
				values.put(FieldUtils.getColumnNameByField(tableInfo.getId().getField()),
						FieldUtils.value2Json(tableInfo.getId().getField().get(entity)));
			}

			Set<String> keySet = pList.keySet();
			for (String key : keySet) {
				Property property = pList.get(key);
				Field field = property.getField();
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value != null) {
					if (field.getType().equals(String.class) || field.getType().equals(Enum.class) || field.getType().isPrimitive())
						values.put(FieldUtils.getColumnNameByField(field), value.toString());
					else
						values.put(FieldUtils.getColumnNameByField(field), FieldUtils.value2Json(value));
				}
			}

			if (extra != null) {
				if (!TextUtils.isEmpty(extra.getOwner()))
					values.put(FieldUtils.OWNER, extra.getOwner());

				if (!TextUtils.isEmpty(extra.getKey()))
					values.put(FieldUtils.KEY, extra.getKey());
			}

			values.put(FieldUtils.CREATEAT, System.currentTimeMillis() / 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}

	public static ContentValues getUpdateContentValues(TableInfo tableInfoUtils, Object entity) {
		TableInfo tableInfo = tableInfoUtils.get(entity.getClass());
		Map<String, Property> pList = tableInfo.propertyMap;

		ContentValues values = new ContentValues();

		try {
			Set<String> keySet = pList.keySet();
			for (String key : keySet) {
				Property property = pList.get(key);
				Field field = property.getField();
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value != null) {
					if (field.getType().equals(String.class) || field.getType().equals(Enum.class) || field.getType().isPrimitive())
						values.put(FieldUtils.getColumnNameByField(field), value.toString());
					else
						values.put(FieldUtils.getColumnNameByField(field), FieldUtils.value2Json(value));
				}
			}
			
			values.put(FieldUtils.CREATEAT, System.currentTimeMillis() / 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}
}
