package com.m.support.cache;

import com.m.common.params.Params;
import com.m.common.settings.Setting;

public class DBCacheUtility implements ICacheUtility{

	@Override
	public <T> Cache<T> findCacheData(Setting action, Params params, Class<T> responseCls) {
		return null;
	}

	@Override
	public void addCacheData(Setting action, Params params, Object responseObj) {
	}

}
