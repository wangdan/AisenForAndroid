package org.aisen.wen.component.network.http;

import org.aisen.wen.component.network.setting.Setting;
import org.aisen.wen.component.network.task.TaskException;

import java.io.File;

/**
 * 网络通讯切面
 *
 */
public interface IHttpUtility {

	<T> T doGet(HttpConfig config, Setting action, Params urlParams, Class<T> responseCls) throws TaskException;

	<T> T doPost(HttpConfig config, Setting action, Params urlParams, Params bodyParams, Object requestObj, Class<T> responseCls) throws TaskException;

	<T> T doPostFiles(HttpConfig config, Setting action, Params urlParams, Params bodyParams, MultipartFile[] files, Class<T> responseCls) throws TaskException;

	class MultipartFile {

		private final String contentType;// "application/octet-stream"

		private final File file;

		private final String key;

		private final byte[] bytes;

		private OnMultiFileProgress callback;

		public MultipartFile(String contentType, String key, File file) {
			this.key = key;
			this.contentType = contentType;
			this.file = file;
			this.bytes = null;
		}

		public MultipartFile(String contentType, String key, byte[] bytes) {
			this.key = key;
			this.contentType = contentType;
			this.bytes = bytes;
			this.file = null;
		}

		public String getContentType() {
			return contentType;
		}

		public File getFile() {
			return file;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public String getKey() {
			return key;
		}

		public void setOnProgress(OnMultiFileProgress callback) {
			this.callback = callback;
		}

		public OnMultiFileProgress getOnProgress() {
			return callback;
		}

	}

}
