package org.aisen.wen.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * 生命周期
 *
 * Created by wangdan on 16/9/30.
 */
public interface ILifecycleBridge {

    void setContext(Activity context);

    Activity getContext();

    void onBridgeCreate(Bundle savedInstanceState);

    void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState);

    void onBridgeStart();

    void onBridgeResume();

    void onBridgePause();

    void onBridgeStop();

    void onBridgeDestory();

    void onBridgeSaveInstanceState(Bundle outState);

}
