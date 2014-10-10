package com.m.support.cache;

import java.io.File;
import java.io.Serializable;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.m.common.context.GlobalContext;
import com.m.common.params.Params;
import com.m.common.settings.Setting;
import com.m.common.settings.SettingUtil;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.DateUtils;
import com.m.common.utils.FileUtility;
import com.m.common.utils.KeyGenerator;
import com.m.common.utils.Logger;
import com.m.common.utils.ObjectUtil;
import com.m.common.utils.SystemUtility;
import com.m.support.sqlite.SqliteUtility;
import com.m.support.sqlite.annotation.Id;
import com.m.support.sqlite.property.Extra;

/**
 * 2013-12-24--->在不影响中行上线版本的基础上，将文件有效期写入db
 * 
 * @author wangdan
 * 
 */
public class FileCacheUtility implements ICacheUtility {

	private static final String TAG = FileCacheUtility.class.getSimpleName();

	private final String cachePath;// sdcard可用，就使用配置路径，否则使用程序缓存目录
	private static FileCacheManager cacheManager;

	public FileCacheUtility() {
		if (SystemUtility.hasSdcardAndCanWrite()) {
			cachePath = SystemUtility.getSdcardPath() + File.separator + SettingUtility.getStringSetting("root_path") + File.separator
					+ SettingUtility.getPermanentSettingAsStr("com_m_common_json", "json");
		} else {
			cachePath = GlobalContext.getInstance().getCacheDir().getAbsolutePath() + File.separator
					+ SettingUtility.getPermanentSettingAsStr("com_m_common_json", "json");
		}

		if (cacheManager == null)
			cacheManager = new FileCacheManager(cachePath);
	}

	@Override
	public <T> Cache<T> findCacheData(Setting actionSetting, Params params, Class<T> responseCls) {
		// 重置参数
		if (params != null) {
			params = ObjectUtil.cloneObject(params);
			params.remove("mac");
		}

		String action = actionSetting.getValue();
		String key = KeyGenerator.generateMD5(action, params);

		T data = null;
		File file = cacheManager.get(key);
		if (file != null && file.exists()) {
			String readContent = FileUtility.readFileToString(file);
			if (!TextUtils.isEmpty(readContent))
				data = JSON.parseObject(readContent, responseCls);
		} else {
			Logger.v(TAG, String.format("key=%s has not cache file", key));
		}

		if (data != null) {
			// 默认缓存未过期
			boolean isDue = false;

			CacheTime cacheTime = SqliteUtility.getInstance().selectById(new Extra(key), CacheTime.class);
			Logger.w(TAG, cacheTime);
			if (cacheTime != null) {
				long currentTime = System.currentTimeMillis();
				long saveTime = cacheTime.getTime();

				long validTime = SettingUtil.getValidTime(actionSetting);
				if (validTime != Integer.MAX_VALUE)
					validTime = validTime * 1000;

				Logger.d(
						TAG,
						String.format("缓存保存时间为%s，当前时间为%s, 缓存有效时间为%s秒", DateUtils.formatDate(saveTime, DateUtils.TYPE_01),
								DateUtils.formatDate(currentTime, DateUtils.TYPE_01), validTime / 1000));

				isDue = currentTime < saveTime + validTime ? false : true;
			} else {
				SqliteUtility.getInstance().insert(null, new CacheTime(key, System.currentTimeMillis()));
			}

			return new Cache<T>(data, isDue);
		}

		return null;
	}

	@Override
	public void addCacheData(Setting actionSetting, Params params, Object responseObj) {
		if (params != null) {
			params = ObjectUtil.cloneObject(params);
			params.remove("mac");
		}

		String action = actionSetting.getValue();
		String key = KeyGenerator.generateMD5(action, params);

		if (!cacheManager.add(key, responseObj)) {
			cacheManager.delete(actionSetting, params);
		} else {
			// 更新缓存保存时间
			CacheTime cacheTime = new CacheTime();
			cacheTime.setKey(key);
			cacheTime.setTime(System.currentTimeMillis());
			SqliteUtility.getInstance().insert(null, cacheTime);
		}
	}

	private static class FileCacheManager {

		private final String cacheDir;
		private final String fileHz = "";

		FileCacheManager(String cacheDir) {
			this.cacheDir = cacheDir;
			Logger.v(TAG, String.format("FileCacheDir's path = %s", cacheDir));

			File rootFile = new File(cacheDir);
			if (!rootFile.exists())
				rootFile.mkdirs();
//			calculateCacheSize();
		}

//		private void calculateCacheSize() {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					File[] cachedFiles = cacheDir.listFiles();
//					if (cachedFiles != null) {
//						for (File cachedFile : cachedFiles) {
//							if (cachedFile.getName().indexOf(fileHz) == -1 || cachedFile.isDirectory())
//								continue;
//
//							Logger.i(TAG, cachedFile.getAbsolutePath());
//
//							cacheFiles.put(getFileRealName(cachedFile), cachedFile);
//						}
//					}
//				}
//			}).start();
//		}

		private File newFile(String key) {
			String fileName = key + fileHz;
			Logger.d(TAG, String.format("add file to cache, key=%s, fileName=%s", key, fileName));
			File file = new File(cacheDir + File.separator + fileName);
			return file;
		}

		private File getFile(String key) {
			String fileName = key + fileHz;
			File file = new File(cacheDir + File.separator + fileName);
			if (file.exists()) {
				Logger.d(TAG, String.format("load file, key=%s, fileName=%s", key, fileName));
				return file;
			}

			return null;
		}

		public File get(String key) {
			return getFile(key);
		}

		/**
		 * 
		 * @param key
		 * @param data
		 * @return false-保存失败
		 */
		public boolean add(String key, Object data) {
			File file = newFile(key);
			return FileUtility.writeFile(file, JSON.toJSONString(data));
		}

		public void delete(Setting setting, Params params) {
			// 重置参数
			params = ObjectUtil.cloneObject(params);
			params.remove("mac");

			String action = setting.getValue();
			String key = KeyGenerator.generateMD5(action, params);

			File file = getFile(key);
			try {
				file.delete();
				SqliteUtility.getInstance().delete(null, CacheTime.class, key);
			} catch (Exception e) {
			}
		}

	}

	public static class CacheTime implements Serializable {

		private static final long serialVersionUID = 5651724383844501761L;

		@Id(column = "id")
		private String key;

		private long time;

		public CacheTime() {

		}

		public CacheTime(String key, long time) {
			this.key = key;
			this.time = time;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}
	}

}
