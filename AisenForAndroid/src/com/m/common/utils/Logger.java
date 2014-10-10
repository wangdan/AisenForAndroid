package com.m.common.utils;

import com.alibaba.fastjson.JSON;

import android.util.Log;

public class Logger {

	private final static String TAG = "Logger";

	public static boolean DEBUG = true;

	public static void e(Object o) {
		if (DEBUG)
			Log.e(TAG, toJson(o));
	}

	public static void d(Object o) {
		if (DEBUG)
			Log.d(TAG, toJson(o));
	}

	public static void i(Object o) {
		if (DEBUG)
			Log.i(TAG, toJson(o));
	}

	public static void v(Object o) {
		if (DEBUG)
			Log.v(TAG, toJson(o));
	}

	public static void w(Object o) {
		if (DEBUG)
			Log.w(TAG, toJson(o));
	}

	public static void e(String o) {
		if (DEBUG)
			Log.e(TAG, o);
	}

	public static void d(String o) {
		if (DEBUG)
			Log.d(TAG, o);
	}

	public static void i(String o) {
		if (DEBUG)
			Log.i(TAG, o);
	}

	public static void v(String o) {
		if (DEBUG)
			Log.v(TAG, o);
	}

	public static void w(String o) {
		if (DEBUG)
			Log.w(TAG, o);
	}

	public static void v(String tag, String msg) {
		if (DEBUG)
			Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (DEBUG)
			Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (DEBUG)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (DEBUG)
			Log.e(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (DEBUG)
			Log.w(tag, msg);
	}

	public static void v(String tag, Object msg) {
		if (DEBUG)
			Log.v(tag, toJson(msg));
	}

	public static void d(String tag, Object msg) {
		if (DEBUG)
			Log.d(tag, toJson(msg));
	}

	public static void i(String tag, Object msg) {
		if (DEBUG)
			Log.i(tag, toJson(msg));
	}

	public static void e(String tag, Object msg) {
		if (DEBUG)
			Log.e(tag, toJson(msg));
	}

	public static void w(String tag, Object msg) {
		if (DEBUG)
			Log.w(tag, toJson(msg));
	}
	
	public static String toJson(Object msg) {
		String json = JSON.toJSONString(msg);
		if(json.length() > 500)
			json = json.substring(0, 500);
		
		return json;
	}

}
