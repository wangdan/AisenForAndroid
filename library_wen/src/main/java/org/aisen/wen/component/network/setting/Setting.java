package org.aisen.wen.component.network.setting;

import java.util.HashMap;
import java.util.Map;

public class Setting extends BaseSetting {

	private static final long serialVersionUID = 4801654811733634325L;

	private final Map<String, SettingExtra> extras;

	public Setting() {
		extras = new HashMap<>();
	}

	public Map<String, SettingExtra> getExtras() {
		return extras;
	}

}
