package org.aisen.android.network.http;

public class HttpConfig {

	public String cookie;

	public String baseUrl;// 服务器地址
	
	@Override
	public HttpConfig clone() throws CloneNotSupportedException {
		HttpConfig httpConfig = new HttpConfig();
		httpConfig.cookie = cookie;
		httpConfig.baseUrl = baseUrl;
		return httpConfig;
	}
	
}
