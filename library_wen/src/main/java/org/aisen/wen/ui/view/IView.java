package org.aisen.wen.ui.view;

import android.view.View;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IView {

    int layoutId();

    void bindView();

    void bindEvent();

    View findViewById(int id);

}
