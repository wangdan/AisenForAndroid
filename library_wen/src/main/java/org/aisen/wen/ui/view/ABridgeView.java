package org.aisen.wen.ui.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.aisen.wen.ui.presenter.ILifecycleBridge;

/**
 * Created by wangdan on 16/10/8.
 */
public abstract class ABridgeView implements IView, ILifecycleBridge {

    private Activity mContext;

    @Override
    public void onBridgeCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    public void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState) {
        mContext = activity;
    }

    @Override
    public void onBridgeStart() {

    }

    @Override
    public void onBridgeResume() {

    }

    @Override
    public void onBridgePause() {

    }

    @Override
    public void onBridgeStop() {

    }

    @Override
    public void onBridgeDestory() {

    }

    @Override
    public void onBridgeSaveInstanceState(Bundle outState) {

    }

    @Override
    public void bindEvent() {

    }

    @Override
    public void bindView() {

    }

    public Activity getContext() {
        return mContext;
    }

}
