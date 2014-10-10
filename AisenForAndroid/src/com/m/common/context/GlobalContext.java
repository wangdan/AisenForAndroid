package com.m.common.context;

import java.io.File;

import org.android.loader.BitmapLoader;

import android.app.Application;
import android.os.Handler;

import com.m.common.settings.SettingUtility;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.common.utils.SystemUtility;
import com.m.common.utils.UEHandler;

public class GlobalContext extends Application {

	private static GlobalContext _context;

	@Override
	public void onCreate() {
		super.onCreate();
		_context = this;
		
		if (SettingUtility.getBooleanSetting("crashlogenable"))
			Thread.setDefaultUncaughtExceptionHandler(new UEHandler());
		
		// 初始化ActivityHelper
		ActivityHelper.initInstance(this);

		// 初始化设置
		SettingUtility.setSettingUtility();

		// 初始化BitmapLoader
		BitmapLoader.newInstanceAndInit(GlobalContext.getInstance(), getImagePath());

		Logger.DEBUG = SettingUtility.getBooleanSetting("debug");
	}

	public static GlobalContext getInstance() {
		return _context;
	}
	
	public Handler getHandler() {
		return mHandler;
	}
	
	Handler mHandler = new Handler() {
		
	};
	
	/**
	 * 程序Sdcard目录
	 * 
	 * @return
	 */
	public String getAppPath() {
		return SystemUtility.getSdcardPath() + File.separator + SettingUtility.getStringSetting("root_path") + File.separator;
	}
	
	/**
	 * 程序缓存文件目录
	 * 
	 * @return
	 */
	public String getDataPath() {
		return getAppPath() + SettingUtility.getPermanentSettingAsStr("com_m_common_json", "data") + File.separator;
	}
	
	/**
	 * 图片图片缓存目录
	 * 
	 * @return
	 */
	public String getImagePath() {
		return getAppPath() + SettingUtility.getPermanentSettingAsStr("com_m_common_image", "image") + File.separator;
	}

}