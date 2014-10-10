package com.m.support.network;

import java.io.File;

import com.m.common.params.Params;
import com.m.common.settings.Setting;
import com.m.support.task.TaskException;

public interface HttpUtility {

	public <T> T doGet(HttpConfig config, Setting action, Params params, Class<T> responseCls) throws TaskException;

	public <T> T doPost(HttpConfig config, Setting action, Params params, Class<T> responseCls, Object requestObj) throws TaskException;

	public <T> T uploadFile(HttpConfig config, Setting action, Params params, File file, Params headers, Class<T> responseClass) throws TaskException;

}
