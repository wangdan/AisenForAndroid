package org.aisen.wen.component.network.setting;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Setting extends BaseSetting implements Serializable {

	private static final long serialVersionUID = 4801654811733634325L;

	private Map<String, SettingExtra> extras;

	public Setting() {
		extras = new HashMap<>();
	}

	public Map<String, SettingExtra> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, SettingExtra> extras) {
		this.extras = extras;
	}

	public Setting copy() {
		return JSON.parseObject(JSON.toJSONString(this), Setting.class);
	}

}
