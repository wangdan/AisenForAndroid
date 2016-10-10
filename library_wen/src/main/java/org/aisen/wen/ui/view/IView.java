package org.aisen.wen.ui.view;

import android.app.Activity;
import android.view.View;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IView {

    Activity getContext();

    View getContentView();

    void bindView(View contentView);

    void bindEvent(View contentView);

}
