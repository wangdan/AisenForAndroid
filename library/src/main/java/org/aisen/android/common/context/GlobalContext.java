package org.aisen.android.common.context;

import android.app.Application;
import android.os.Handler;

import com.squareup.okhttp.OkHttpClient;

import org.aisen.android.common.utils.ActivityHelper;

import java.util.concurrent.TimeUnit;

public class GlobalContext extends Application {

	private static GlobalContext _context;

	public final static int CONN_TIMEOUT = 30000;
	public final static int READ_TIMEOUT = 30000;

	private static OkHttpClient mOkHttpClient;

	static {
		// 初始化OkHttpClient
		mOkHttpClient = new OkHttpClient();
		configOkHttpClient(CONN_TIMEOUT, READ_TIMEOUT);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		_context = this;

		// 初始化ActivityHelper
		ActivityHelper.config(this);
	}

	public static GlobalContext getInstance() {
		return _context;
	}
	
	public Handler getHandler() {
		return mHandler;
	}

	Handler mHandler = new Handler() {
		
	};
	
	public static OkHttpClient getOkHttpClient() {
		return mOkHttpClient;
	}

	public static void configOkHttpClient(int connTimeout, int socketTimeout) {
		if (mOkHttpClient != null) {
			mOkHttpClient.setConnectTimeout(connTimeout, TimeUnit.MILLISECONDS);
			mOkHttpClient.setReadTimeout(socketTimeout, TimeUnit.MILLISECONDS);
		}
	}

}