package com.m.support.sqlite.util;

import java.util.Collection;

import com.m.support.sqlite.property.Id;
import com.m.support.sqlite.property.Property;
import com.m.support.sqlite.property.TableInfo;

public class SqlBuilder {

	public static String getCreatTableSQL(TableInfo tableInfoUtils, Class<?> clazz) {
		TableInfo table = tableInfoUtils.get(clazz);

		Id id = table.getId();
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("CREATE TABLE IF NOT EXISTS ");
		strSQL.append(table.getTableName());
		strSQL.append(" ( ");

//		Class<?> primaryClazz = id.getDataType();
//		if (primaryClazz == int.class || primaryClazz == Integer.class)
//			strSQL.append("\"").append(id.getField().getName()).append("\"    ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
//		else
		strSQL.append(" ").append(" _id ").append("    ").append(" INTEGER PRIMARY KEY AUTOINCREMENT ,");

		strSQL.append(" ").append(FieldUtils.getColumnNameByField(id.getField())).append("    ").append("TEXT NOT NULL ,");

		Collection<Property> propertys = table.propertyMap.values();
		for (Property property : propertys) {

			strSQL.append(" ").append(property.getColumn());
			strSQL.append(" ,");
		}

		strSQL.append(" ").append(FieldUtils.OWNER).append(" text ").append(" ,");

		strSQL.append(" ").append(FieldUtils.KEY).append(" text ").append(" ,");

		strSQL.append(" ").append(FieldUtils.CREATEAT).append(" INTEGER ").append(" ,");

		strSQL.deleteCharAt(strSQL.length() - 1);
		strSQL.append(" )");
		return strSQL.toString();
	}

}
