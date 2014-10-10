package com.m.support.cache;

import java.util.Hashtable;

import com.alibaba.fastjson.JSON;
import com.m.common.params.Params;
import com.m.common.settings.Setting;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.Consts;
import com.m.common.utils.DateUtils;
import com.m.common.utils.KeyGenerator;
import com.m.common.utils.Logger;

public class MemoryCacheUtility implements ICacheUtility {

	private static final String TAG = ICacheUtility.class.getSimpleName();

	private static Hashtable<String, String> data;
	private static Hashtable<String, Long> dataTime;

	static {
		if (data == null)
			data = new Hashtable<String, String>();
		if (dataTime == null)
			dataTime = new Hashtable<String, Long>();
	}

	@Override
	public <T> Cache<T> findCacheData(Setting actionSetting, Params params, Class<T> responseCls) {
		String action = actionSetting.getValue();
		String key = KeyGenerator.generateMD5(action, params);

		if (data.containsKey(key)) {

			long currentTime = System.currentTimeMillis();

			long savedTime = dataTime.get(key);

			// 取缓存有效时间
			String validTime = null;
			if (actionSetting.getExtras().containsKey(Consts.Setting.CACHE_VALIDTIME))
				validTime = actionSetting.getExtras().get(Consts.Setting.CACHE_VALIDTIME).toString();
			else
				validTime = SettingUtility.getSetting(Consts.Setting.MEMORY_CACHE_VALIDITY).getValue();
			if (Consts.Value.DATA_NEVER_CRASH.equals(validTime))
				savedTime = Integer.MAX_VALUE;
			else
				savedTime += (Integer.parseInt(validTime) * 1000);

			Logger.i(
					TAG,
					String.format("--->缓存保存时间为%s，当前时间为%s, 缓存有效时间为%s秒", DateUtils.formatDate(dataTime.get(key), DateUtils.TYPE_03),
							DateUtils.formatDate(currentTime, DateUtils.TYPE_03), SettingUtility.getIntSetting(Consts.Setting.MEMORY_CACHE_VALIDITY)));

			T result = JSON.parseObject(data.get(key), responseCls);
			if (currentTime <= savedTime) {
				Logger.i(TAG, "--->内存缓存有效");
				return new Cache<T>(result, false);
			} else {
				data.remove(key);
				Logger.i(TAG, "--->内存缓存无效");
				return new Cache<T>(result, true);
			}
		}

		return null;
	}

	@Override
	public void addCacheData(Setting actionSetting, Params params, Object responseObj) {
		String action = actionSetting.getValue();
		String key = KeyGenerator.generateMD5(action, params);

		dataTime.put(key, System.currentTimeMillis());
		data.put(key, JSON.toJSONString(responseObj));
	}

}
