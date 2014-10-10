package com.m.support.sqlite.property;

import java.lang.reflect.Field;

public class Property {

	private Class<?> dataType;
	
	private Field field;
	
	private String column;
	

	public Class<?> getDataType() {
		return dataType;
	}

	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

}
