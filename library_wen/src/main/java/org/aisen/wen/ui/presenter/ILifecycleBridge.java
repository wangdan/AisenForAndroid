package org.aisen.wen.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 *
 * Created by wangdan on 16/9/30.
 */
public interface ILifecycleBridge {

    void onCreate(LayoutInflater inflater);

    void onActivityCreate(Activity activity, Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    void onDestory();

}
