package org.aisen.wen.ui.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IView {

    Activity getViewContext();

    void setViewContext(Activity context);

    View getContentView();

    void bindView(View contentView, Bundle savedInstanceState);

    void bindEvent(View contentView, Bundle savedInstanceState);

}
