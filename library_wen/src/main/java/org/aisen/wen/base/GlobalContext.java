package org.aisen.wen.base;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Application的上下文
 *
 * Created by wangdan on 16/9/29.
 */
public class GlobalContext {

    public final static int CONN_TIMEOUT = 30000;
    public final static int READ_TIMEOUT = 30000;

    private final static OkHttpClient mOkHttpClient = new OkHttpClient();;

    static {
        // 初始化OkHttpClient
        configOkHttpClient(CONN_TIMEOUT, READ_TIMEOUT);
    }

    private GlobalContext() {
    }

    private static Application _context;

    public static void onCreate(Application context) {
        _context = context;
    }

    public static Application getInstance() {
        return _context;
    }

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
