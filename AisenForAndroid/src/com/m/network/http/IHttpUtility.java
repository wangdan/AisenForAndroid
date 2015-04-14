package com.m.network.http;

import java.io.File;

import com.m.common.setting.Setting;
import com.m.network.task.TaskException;

public interface IHttpUtility {

	public <T> T doGet(HttpConfig config, Setting action, Params params, Class<T> responseCls) throws TaskException;

	public <T> T doPost(HttpConfig config, Setting action, Params params, Class<T> responseCls, Object requestObj) throws TaskException;

	public <T> T uploadFile(HttpConfig config, Setting action, Params params, File file, Params headers, Class<T> responseClass) throws TaskException;

}
