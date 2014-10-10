package com.m.common.utils;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.m.R;

public class ActivityHelper {
	private Context _context;
	private static ActivityHelper _helper;

	private ActivityHelper(Context mContext) {
		this._context = mContext;
	}

	public static void initInstance(Context context) {
		if (_helper == null) {
			_helper = new ActivityHelper(context);
		}
	}

	public static ActivityHelper getInstance() {
		return _helper;
	}

	/**
	 * 获取string，默认值为""
	 * 
	 * @param key
	 * @return
	 */
	public String getShareData(String key) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	/**
	 * 获取string
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getShareData(String key, String defValue) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}

	/**
	 * 获取int
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getIntShareData(String key, int defValue) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}

	public int getIntShareData(String key) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}

	public boolean getBooleanShareData(String key) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	public boolean getBooleanShareData(String key, boolean defValue) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}

	public void putShareData(String key, String value) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putString(key, value);
		et.commit();
	}

	public void putIntShareData(String key, int value) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putInt(key, value);
		et.commit();
	}

	public void putBooleanShareData(String key, boolean value) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putBoolean(key, value);
		et.commit();
	}

	public void putSetShareData(String key, Set<String> value) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		Editor et = sp.edit();
		et.putStringSet(key, value);
		et.commit();
	}

	public Set<String> getSetShareData(String key) {
		SharedPreferences sp = _context.getSharedPreferences(_context.getString(R.string.sharedata), Context.MODE_PRIVATE);
		return sp.getStringSet(key, new HashSet<String>());
	}

}
