package com.m.common.utils;

import com.m.common.setting.SettingUtility;

public class CommSettings {

	public static void setAppTheme(int theme) {
		if (theme > 0)
			SettingUtility.setPermanentSetting("theme", theme);
	}
	
	public static int getAppTheme() {
		return SettingUtility.getPermanentSettingAsInt("theme");
	}
	
}
