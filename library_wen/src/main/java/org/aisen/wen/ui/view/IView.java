package org.aisen.wen.ui.view;

import android.view.View;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IView {

    View getContentView();

    void bindView(View contentView);

    void bindEvent(View contentView);

}
