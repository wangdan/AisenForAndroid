package org.aisen.wen.ui.view.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.aisen.wen.R;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.inject.InjectUtility;
import org.aisen.wen.support.inject.ViewInject;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.ui.view.ABridgeView;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentView extends ABridgeView {

    static final String TAG = "AContentView";

    private View mContentView;
    @ViewInject(idStr = "layoutLoading")
    View loadingLayout;// 加载中视图
    @ViewInject(idStr = "layoutLoadFailed")
    View loadFailureLayout;// 加载失败视图
    @ViewInject(idStr = "layoutContent")
    View contentLayout;// 内容视图
    @ViewInject(idStr = "layoutEmpty")
    View emptyLayout;// 空视图

    // 标志是否ContentView是否为空
    private boolean contentEmpty = true;

    @Override
    public void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onBridgeCreateView(inflater, container, savedInstanceState);

        mContentView = inflater.inflate(contentViewResId(), null);
    }

    @Override
    public void bindView() {
        super.bindView();

        InjectUtility.initInjectedView(getContext(), this, getContentView());
    }

    public View findViewById(int id) {
        return getContentView().findViewById(id);
    }

    public boolean isContentLayoutEmpty() {
        return contentEmpty;
    }

    public void setContentLayoutEmpty(boolean empty) {
        this.contentEmpty = empty;
    }

    public void setLoadingLayoutVisibility(int visibility) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(visibility);

            Logger.v(TAG, "setLoadingLayoutVisibility[%d]", visibility);
        }
    }

    public void setEmptyLayoutVisibility(int visibility) {
        if (emptyLayout != null) {
            emptyLayout.setVisibility(visibility);

            Logger.v(TAG, "setEmptyLayoutVisibility[%d]", visibility);
        }
    }

    public void setContentLayoutVisibility(int visibility) {
        if (contentLayout != null) {
            contentLayout.setVisibility(visibility);

            Logger.v(TAG, "setContentLayoutVisibility[%d]", visibility);
        }
    }

    public void setFailureLayoutVisibility(int visibility, TaskException e) {
        if (loadFailureLayout != null) {
            loadFailureLayout.setVisibility(visibility);

            Logger.v(TAG, "setFailureLayoutVisibility[%d]", visibility);

            if (e != null) {
                TextView txtLoadFailed = (TextView) loadFailureLayout.findViewById(R.id.txtLoadFailed);
                if (txtLoadFailed != null)
                    txtLoadFailed.setText(e.getMessage());
            }

            setEmptyLayoutVisibility(View.GONE);
        } else {
            setEmptyLayoutVisibility(View.VISIBLE);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    /**
     * ContentView的ID
     *
     * @return
     */
    abstract public int contentViewResId();

}
