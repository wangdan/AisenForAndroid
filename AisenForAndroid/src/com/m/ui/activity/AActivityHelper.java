package com.m.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.m.support.Inject.InjectUtility;

public abstract class AActivityHelper {

    private BaseActivity mActivity;

    public void setBaseActivity(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    protected BaseActivity getBaseActivity() {
        return mActivity;
    }

    protected BaseActivity getActivity() {
        return mActivity;
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public void layoutInit(View rootView, LayoutInflater inflater, Bundle savedInstanceSate) {
        InjectUtility.initInjectedView(this, rootView);
    }

    public ViewGroup setContentView(int layoutResID) {
        return null;
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public boolean onHomeClick() {
        return false;
    }

    public boolean onBackClick() {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
