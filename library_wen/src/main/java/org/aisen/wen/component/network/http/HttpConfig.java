package org.aisen.wen.component.network.http;

import java.util.HashMap;
import java.util.Map;

public class HttpConfig {

	public String cookie;

	public String baseUrl;// 服务器地址

	public Map<String, String> headerMap = new HashMap<>();

	public void addHeader(String key, String value) {
		headerMap.put(key, value);
	}

}
