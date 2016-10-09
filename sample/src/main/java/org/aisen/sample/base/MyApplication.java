package org.aisen.sample.base;

import android.app.Application;

import org.aisen.wen.base.GlobalContext;

/**
 * Created by wangdan on 15/4/23.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GlobalContext.onCreate(this);
    }

}
