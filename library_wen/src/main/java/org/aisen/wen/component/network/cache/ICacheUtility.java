package org.aisen.wen.component.network.cache;


import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.http.Params;
import org.aisen.wen.component.network.setting.Setting;

/**
 * 缓存切面
 * 
 * @author wangdan
 * 
 */
public interface ICacheUtility {

	IResult findCacheData(Setting action, Params params);

	void addCacheData(Setting action, Params params, IResult result);

}
