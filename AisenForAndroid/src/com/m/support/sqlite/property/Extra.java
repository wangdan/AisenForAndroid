package com.m.support.sqlite.property;

import java.io.Serializable;

public class Extra implements Serializable {

	private static final long serialVersionUID = 564688497149644850L;

	private String id;// id

	private String owner;// 数据拥有者

	private String key;// 数据的key

	private int validTime;// 数据的有效时间，为ms时间

	public Extra() {

	}

	public Extra(String id) {
		this.id = id;
	}

	public Extra(String owner, String key) {
		this.owner = owner;
		this.key = key;
	}

	public Extra(String id, String owner, String key) {
		this(owner, key);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	int getValidTime() {
		return validTime;
	}

	void setValidTime(int validTime) {
		this.validTime = validTime;
	}

}
