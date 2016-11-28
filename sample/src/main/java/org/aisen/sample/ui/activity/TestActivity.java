package org.aisen.sample.ui.activity;

import android.os.Bundle;

import org.aisen.android.ui.activity.basic.BaseActivity;

/**
 * Created by wangdan on 16/11/28.
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Test.test();
    }

}
