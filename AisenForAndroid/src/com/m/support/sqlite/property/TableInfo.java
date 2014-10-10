package com.m.support.sqlite.property;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import com.m.support.sqlite.util.ClassUtils;
import com.m.support.sqlite.util.FieldUtils;

public class TableInfo {

	private Class<?> clazz;
	private String tableName;

	private Id id;

	private boolean checkDatabese;// 在对实体进行数据库操作的时候查询是否已经有表了，只需查询一遍，用此标示

	public final HashMap<String, Property> propertyMap = new HashMap<String, Property>();

	private final HashMap<String, TableInfo> tableInfoMap = new HashMap<String, TableInfo>();

	public TableInfo get(Class<?> clazz) {
		TableInfo tableInfo = tableInfoMap.get(clazz.getName());

		if (tableInfo == null) {
			tableInfo = new TableInfo();

			tableInfo.setTableName(ClassUtils.getTableName(clazz));
			tableInfo.setClazz(clazz);

			Field idField = ClassUtils.getPrimaryKeyField(clazz);
			if (idField != null) {
				Id id = new Id();
				id.setColumn(FieldUtils.getColumnByField(idField));
				id.setDataType(idField.getType());
				id.setField(idField);
				tableInfo.setId(id);
			} else {
				throw new RuntimeException("the class[" + clazz
						+ "]'s idField is null , \n you can define _id,id property or use annotation @id to solution this exception");
			}

			List<Property> properties = ClassUtils.getPropertyList(clazz);
			if (properties != null) {
				for (Property p : properties)
					if (p != null)
						tableInfo.propertyMap.put(p.getColumn(), p);
			}

			tableInfoMap.put(clazz.getName(), tableInfo);
		}

		return tableInfo;
	}

	public TableInfo get(String className) {
		try {
			return get(Class.forName(className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Property getProperty(String columnName) {
		return propertyMap.get(columnName);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public boolean isCheckDatabese() {
		return checkDatabese;
	}

	public void setCheckDatabese(boolean checkDatabese) {
		this.checkDatabese = checkDatabese;
	}

}
