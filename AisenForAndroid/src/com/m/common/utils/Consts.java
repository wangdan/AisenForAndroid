package com.m.common.utils;

public class Consts {

	private Consts() {

	}

	public static class Setting {

		// 服务地址
		public static final String BASE_URL = "base_url";

		// 缓存开关
		public static final String CACHE_ENABLE = "cache_enable";

		// 缓存类型
		public static final String CACHE_UTILITY = "cache_utility";
		
		// 内存缓存
		public static final String MEMORY_CACHE_UTILITY = "memory_cache_utility";

		// 内存缓存有效时间
		public static final String MEMORY_CACHE_VALIDITY = "memory_cache_validity";

		/**
		 * 客户化定制ICacheUtility时的有效时间，值为{@link Value#DATA_NEVER_CRASH}时，数据持久有效
		 */
		public static final String CACHE_VALIDTIME = "cache_validtime";

		// 设置-DEBUG
		public static final String DEBUG = "debug";

		// 分页大小
		public static final String PAGE_COUNT = "page_count";

	}

	public static class File {

		// action、url的配置文件名
		public static final String CONFIG_ACTIONS = "actions";

		// 程序设置的配置文件名
		public static final String CONFIG_SETTINGS = "settings";
		
		public static final String CONFIG_SQLITE = "sqlite";

	}

	public static class Value {

		public static final String DATA_NEVER_CRASH = "max_date";

	}

}