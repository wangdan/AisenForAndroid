package com.m.ui.activity.basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

/**
 * 用户注册回调BaseActivity的生命周期及相关的方法，自行添加
 *
 * Created by wangdan on 15/4/14.
 */
public class BaseActivityHelper {

    private BaseActivity mActivity;

    protected void bindActivity(BaseActivity activity) {
        this.mActivity = activity;
    }

    protected BaseActivity getActivity() {
        return mActivity;
    }

    protected void onCreate(Bundle savedInstanceState) {

    }

    protected void onStart() {

    }

    protected void onRestart() {

    }

    protected void onResume() {

    }

    protected void onPause() {

    }

    protected void onStop() {

    }

    public void onDestroy() {

    }

    public void finish() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onSaveInstanceState(Bundle outState) {

    }

    // 这三个方法暂不支持
//    public void setContentView(int layoutResID) {
//
//    }
//
//    public void setContentView(View view) {
//
//    }
//
//    public void setContentView(View view, ViewGroup.LayoutParams params) {
//
//    }

    protected boolean onHomeClick() {
        return false;
    }

    public boolean onBackClick() {
        return false;
    }

    protected int configTheme() {
        return 0;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

}
