package org.aisen.wen.component.network.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import org.aisen.wen.base.GlobalContext;
import org.aisen.wen.component.network.biz.ABizLogic;
import org.aisen.wen.component.network.setting.Setting;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.support.utils.SystemUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class DefHttpUtility implements IHttpUtility {

	static String getTag(Setting action, String append) {
		return ABizLogic.getTag(action, append);
	}

	public final static int CONN_TIMEOUT = 30000;
	public final static int READ_TIMEOUT = 30000;

	private final static OkHttpClient mOkHttpClient = new OkHttpClient();

	static {
		// 初始化OkHttpClient
		configOkHttpClient(CONN_TIMEOUT, READ_TIMEOUT);
	}

	public static void configOkHttpClient(int connTimeout, int socketTimeout) {
		if (mOkHttpClient != null) {
			mOkHttpClient.setConnectTimeout(connTimeout, TimeUnit.MILLISECONDS);
			mOkHttpClient.setReadTimeout(socketTimeout, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public <T> T doGet(HttpConfig config, Setting action, Params urlParams, Class<T> responseCls) throws TaskException {
		Request.Builder builder = createRequestBuilder(config, action, urlParams, "Get");

		Request request = builder.build();

		return executeRequest(request, responseCls, action, "Get");
	}

	@Override
	public <T> T doPost(HttpConfig config, Setting action, Params urlParams, Params bodyParams, Object requestObj, Class<T> responseCls) throws TaskException {
		Request.Builder builder = createRequestBuilder(config, action, urlParams, "Post");

		if (bodyParams != null) {
			String requestBodyStr = bodyParams.toURLQuery();

			Logger.d(getTag(action, "Post"), requestBodyStr);

			builder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), requestBodyStr));
		}
		else if (requestObj != null) {
			String requestBodyStr = JSON.toJSONString(requestObj);

			Logger.d(getTag(action, "Post"), requestBodyStr);

			builder.post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), requestBodyStr));
		}

		return executeRequest(builder.build(), responseCls, action, "Post");
	}

	@Override
	public <T> T doPostFiles(HttpConfig config, Setting action, Params urlParams, Params bodyParams, MultipartFile[] files, Class<T> responseCls) throws TaskException {
		String method = "doPostFiles";

		Request.Builder builder = createRequestBuilder(config, action, urlParams, method);

		MultipartBuilder multipartBuilder = new MultipartBuilder();
		multipartBuilder.type(MultipartBuilder.FORM);

		// 处理Body参数
		if (bodyParams != null && bodyParams.getKeys().size() > 0) {
			for (String key : bodyParams.getKeys()) {
				String value = bodyParams.get(key);
				multipartBuilder.addFormDataPart(key, value);

				Logger.d(getTag(action, method), "BodyParam[%s, %s]", key, value);
			}
		}

		// 处理文件数据
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				// 普通字节流
				if (file.getBytes() != null) {

					multipartBuilder.addFormDataPart(file.getKey(), file.getKey(), createRequestBody(file));

					Logger.d(getTag(action, method), "Multipart bytes, length = " + file.getBytes().length);
				}
				// 文件
				else if (file.getFile() != null) {
					multipartBuilder.addFormDataPart(file.getKey(), file.getFile().getName(), createRequestBody(file));

					Logger.d(getTag(action, method), "Multipart file, name = %s, path = %s", file.getFile().getName(), file.getFile().getAbsolutePath());
				}
			}

		}

		RequestBody requestBody = multipartBuilder.build();
		builder.post(requestBody);
		return executeRequest(builder.build(), responseCls, action, method);
	}

	private Request.Builder createRequestBuilder(HttpConfig config, Setting action, Params urlParams, String method) throws TaskException {
		// 是否有网络连接
		if (GlobalContext.getInstance() != null && SystemUtils.getNetworkType(GlobalContext.getInstance()) == SystemUtils.NetWorkType.none) {
			Logger.w(getTag(action, method), "没有网络连接");

			throw new TaskException(TaskException.TaskError.noneNetwork.toString());
		}

		String url = (config.baseUrl + action.getValue() + (urlParams == null ? "" : "?" + urlParams.toURLQuery())).replaceAll(" ", "");
		Logger.d(getTag(action, method), url);

		Request.Builder builder = new Request.Builder();
		builder.url(url);

		// add Cookie
		if (!TextUtils.isEmpty(config.cookie)) {
			builder.header("Cookie", config.cookie);

			Logger.d(getTag(action, method), "Cookie = " + config.cookie);
		}
		// add header
		if (config.headerMap.size() > 0) {
			Set<String> keySet = config.headerMap.keySet();
			for (String key : keySet) {
				builder.addHeader(key, config.headerMap.get(key));

				Logger.d(getTag(action, method), "Header[%s, %s]", key, config.headerMap.get(key));
			}
		}

		return builder;
	}

	private <T> T executeRequest(Request request, Class<T> responseCls, Setting action, String method) throws TaskException {
		try {
			OkHttpClient okHttpClient = getOkHttpClient();
			if (okHttpClient == null) {
				okHttpClient = mOkHttpClient;
			}
			Response response = okHttpClient.newCall(request).execute();

			Logger.w(getTag(action, method), "Http-code = %d", response.code());
			if (!(response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_PARTIAL)) {
				String responseStr = response.body().string();

				if (Logger.DEBUG) {
					Logger.w(getTag(action, method), responseStr);
				}

				TaskException.checkResponse(responseStr);

				throw new TaskException(TaskException.TaskError.timeout.toString());
			} else {
				String responseStr = response.body().string();

				Logger.v(getTag(action, method), "Response = %s", responseStr);

				return parseResponse(responseStr, responseCls);
			}
		} catch (SocketTimeoutException e) {
			Logger.printExc(DefHttpUtility.class, e);
			Logger.w(getTag(action, method), e + "");

			throw new TaskException(TaskException.TaskError.timeout.toString());
		} catch (IOException e) {
			Logger.printExc(DefHttpUtility.class, e);
			Logger.w(getTag(action, method), e + "");

			throw new TaskException(TaskException.TaskError.timeout.toString());
		} catch (TaskException e) {
			Logger.printExc(DefHttpUtility.class, e);
			Logger.w(getTag(action, method), e + "");

			throw e;
		} catch (Exception e) {
			Logger.printExc(DefHttpUtility.class, e);
			Logger.w(getTag(action, method), e + "");

			throw new TaskException(TaskException.TaskError.resultIllegal.toString());
		}
	}

	protected <T> T parseResponse(String resultStr, Class<T> responseCls) throws TaskException  {
		if (responseCls.getSimpleName().equals("String"))
			return (T) resultStr;

		T result = JSON.parseObject(resultStr, responseCls);
		return result;
	}

	protected OkHttpClient getOkHttpClient() {
		return null;
	}

	private static RequestBody createRequestBody(final MultipartFile file) {
		return new RequestBody() {

			@Override
			public MediaType contentType() {
				return MediaType.parse(file.getContentType());
			}

			@Override
			public long contentLength() throws IOException {
				return file.getBytes() != null ? file.getBytes().length : file.getFile().length();
			}

			@Override
			public void writeTo(BufferedSink sink) throws IOException {
				Source source;
				if (file.getFile() != null) {
					source = Okio.source(file.getFile());
				}
				else {
					source = Okio.source(new ByteArrayInputStream(file.getBytes()));
				}

				OnMultiFileProgress onFileProgress = file.getOnProgress();
				if (onFileProgress != null) {
					try {
						long contentLength = contentLength();
						long writeLen = 0;
						long readLen = -1;
						Buffer buffer = new Buffer();

						long MIN_PROGRESS_STEP = 65536;
						long MIN_PROGRESS_TIME = 300;

						long mLastUpdateBytes = 0;
						long mLastUpdateTime = 0l;
						while ((readLen = source.read(buffer, 8 * 1024)) != -1) {
							sink.write(buffer, readLen);
							writeLen += readLen;

							long now = System.currentTimeMillis();
							if (((writeLen - mLastUpdateBytes) > MIN_PROGRESS_STEP &&
									(now - mLastUpdateTime) > MIN_PROGRESS_TIME) ||
									writeLen == contentLength) {
								onFileProgress.onProgress(writeLen, contentLength);

								mLastUpdateBytes = writeLen;
								mLastUpdateTime = now;
							}
						}
					} catch (IOException e) {
						Logger.printExc(DefHttpUtility.class, e);
						throw e;
					} finally {
						Util.closeQuietly(source);
					}
				}
				else {
					try {
						sink.writeAll(source);
					} catch (IOException e) {
						Logger.printExc(DefHttpUtility.class, e);
						throw e;
					} finally {
						Util.closeQuietly(source);
					}
				}
			}

		};
	}

}
