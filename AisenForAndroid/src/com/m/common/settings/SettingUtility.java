package com.m.common.settings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;

import com.m.common.context.GlobalContext;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Consts;
import com.m.common.utils.SystemUtility;

public class SettingUtility {

	private static Map<String, Setting> settingMap;

	static {
		settingMap = new HashMap<String, Setting>();
	}

	private SettingUtility() {

	}

	public static void setSettingUtility() {
		addSettings(Consts.File.CONFIG_ACTIONS);
		addSettings(Consts.File.CONFIG_SETTINGS);
		addSettings(Consts.File.CONFIG_SQLITE);

		// 初始化sdcard配置路径
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (SystemUtility.hasSdcardAndCanWrite()) {
					String sdcardPath = SystemUtility.getSdcardPath();
					File rootFile = new File(sdcardPath + File.separator + getStringSetting("root_path"));
					if (!rootFile.exists())
						rootFile.mkdirs();

					// 数据缓存目录设置
					File jsonFile = new File(rootFile.getAbsolutePath() + File.separator + getPermanentSettingAsStr("com_m_common_json", "json"));
					if (!jsonFile.exists())
						jsonFile.mkdirs();

					// 缓存目录设置
					File imageFile = new File(rootFile.getAbsolutePath() + File.separator + getPermanentSettingAsStr("com_m_common_image", "image"));
					if (!imageFile.exists())
						imageFile.mkdirs();
				}
				return null;
			}

		}.execute();
	}

	/**
	 * 添加设置配置数据
	 * 
	 * @param settingsXmlName
	 */
	public static void addSettings(String settingsXmlName) {
		Map<String, Setting> newSettingMap = SettingsXmlParser.parseSettings(GlobalContext.getInstance(), settingsXmlName);

		Set<String> keySet = newSettingMap.keySet();
		for (String key : keySet)
			settingMap.put(key, newSettingMap.get(key));
	}

	public static boolean getBooleanSetting(String type) {
		if (settingMap.containsKey(type))
			return Boolean.parseBoolean(settingMap.get(type).getValue());

		return false;
	}

	public static int getIntSetting(String type) {
		if (settingMap.containsKey(type))
			return Integer.parseInt(settingMap.get(type).getValue());

		return -1;
	}

	public static String getStringSetting(String type) {
		if (settingMap.containsKey(type))
			return settingMap.get(type).getValue();

		return null;
	}

	public static Setting getSetting(String type) {
		if (settingMap.containsKey(type))
			return settingMap.get(type);

		return null;
	}

	public static void setPermanentSetting(String type, boolean value) {
		ActivityHelper.getInstance().putBooleanShareData(type, value);
	}

	public static boolean getPermanentSettingAsBool(String type, boolean def) {
		return ActivityHelper.getInstance().getBooleanShareData(type,
				settingMap.containsKey(type) ? Boolean.parseBoolean(settingMap.get(type).getValue()) : def);
	}

	public static void setPermanentSetting(String type, int value) {
		ActivityHelper.getInstance().putIntShareData(type, value);
	}

	public static int getPermanentSettingAsInt(String type) {
		return ActivityHelper.getInstance().getIntShareData(type,
				settingMap.containsKey(type) ? Integer.parseInt(settingMap.get(type).getValue()) : -1);
	}

	public static void setPermanentSetting(String type, String value) {
		ActivityHelper.getInstance().putShareData(type, value);
	}

	public static String getPermanentSettingAsStr(String type, String def) {
		return ActivityHelper.getInstance().getShareData(type, settingMap.containsKey(type) ? settingMap.get(type).getValue() : def);
	}

}
