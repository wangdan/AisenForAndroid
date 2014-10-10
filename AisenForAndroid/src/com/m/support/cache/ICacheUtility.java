package com.m.support.cache;

import com.m.common.params.Params;
import com.m.common.settings.Setting;

/**
 * 缓存接口
 * 
 * @author wangdan
 * 
 */
public interface ICacheUtility {

	public <T> Cache<T> findCacheData(Setting action, Params params, Class<T> responseCls);

	public void addCacheData(Setting action, Params params, Object responseObj);

	public static class Cache<T> {
		
		private T t;

		// true-缓存到期
		private boolean expired;

		public Cache() {

		}

		public Cache(T t, boolean expired) {
			this.t = t;
			this.expired = expired;
		}

		public T getT() {
			return t;
		}

		public void setT(T t) {
			this.t = t;
		}

		public boolean expired() {
			return expired;
		}

		public void setExpired(boolean expired) {
			this.expired = expired;
		}

	}
	
}
