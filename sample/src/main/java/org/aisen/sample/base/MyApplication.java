package org.aisen.sample.base;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.ui.activity.basic.BaseActivity;


/**
 * Created by wangdan on 15/4/23.
 */
public class MyApplication extends GlobalContext {

    @Override
    public void onCreate() {
        super.onCreate();

        BaseActivity.setHelper(SampleActivityHelper.class);
    }

}
