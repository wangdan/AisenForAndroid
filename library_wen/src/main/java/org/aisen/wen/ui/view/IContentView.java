package org.aisen.wen.ui.view;

import android.view.View;

import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.presenter.ILifecycleBridge;

/**
 * 控制4种基本视图的页面刷新
 *
 * Created by wangdan on 16/10/10.
 */
public interface IContentView extends IView, ILifecycleBridge {

    View findViewById(int id);

    int setLayoutId();

    boolean isContentEmpty();

    void setContentLayout(boolean empty);

    View getLoadingLayout();

    View getEmptyLayout();

    View getContentLayout();

    View getFailureLayout();

    void setFailureHint(String hint);

    void setEmptyHind(String hint);

    void setPresenter(IContentPresenter presenter);

}
