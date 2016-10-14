package org.aisen.wen.ui.view.impl;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.aisen.wen.R;
import org.aisen.wen.R2;
import org.aisen.wen.base.Consts;
import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.view.IContentView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 控制4种基本视图的页面刷新
 *
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentView implements IContentView {

    private View mContentView;
    @BindView(R2.id.layoutLoading)
    View loadingLayout;// 加载中视图
    @BindView(R2.id.layoutLoadFailed)
    View loadFailureLayout;// 加载失败视图
    @BindView(R2.id.layoutContent)
    View contentLayout;// 内容视图
    @BindView(R2.id.layoutEmpty)
    View emptyLayout;// 空视图

    private Activity mContext;

    // 标志是否ContentView是否为空
    private boolean contentEmpty = true;

    private IContentPresenter mPresenter;

    @Override
    public void bindView(View contentView, Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
    }

    @Override
    public void bindEvent(View contentView, Bundle savedInstanceState) {
        if (loadFailureLayout != null) {
            View reloadView = loadFailureLayout.findViewById(R.id.layoutReload);
            if (reloadView != null) {
                reloadView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter != null) {
                            mPresenter.requestData();
                        }
                    }
                });
            }
        }
        if (emptyLayout != null) {
            View reloadView = emptyLayout.findViewById(R.id.layoutReload);
            if (reloadView != null) {
                reloadView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter != null) {
                            mPresenter.requestData();
                        }
                    }
                });
            }
        }
    }

    @Override
    public View findViewById(int id) {
        return getContentView().findViewById(id);
    }

    @Override
    public boolean isContentEmpty() {
        return contentEmpty;
    }

    @Override
    public void setContentLayout(boolean empty) {
        this.contentEmpty = empty;
    }

    @Override
    public View getLoadingLayout() {
        return loadingLayout;
    }

    @Override
    public View getEmptyLayout() {
        return emptyLayout;
    }

    @Override
    public View getContentLayout() {
        return contentLayout;
    }

    @Override
    public View getFailureLayout() {
        return loadFailureLayout;
    }

    @Override
    public void setFailureHint(String hint) {
        if (getFailureLayout() == null)
            return;

        TextView txtLoadFailed = (TextView) getFailureLayout().findViewById(R.id.txtLoadFailed);
        if (txtLoadFailed != null)
            txtLoadFailed.setText(hint);
    }

    @Override
    public void setEmptyHind(String hint) {
        if (getEmptyLayout() == null)
            return;

        TextView txtLoadEmpty = (TextView) getFailureLayout().findViewById(R.id.txtLoadEmpty);
        if (txtLoadEmpty != null)
            txtLoadEmpty.setText(hint);
    }

    @Override
    public View getContentView() {
        return mContentView;
    }

    @Override
    public void setPresenter(IContentPresenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onBridgeCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setContentLayout(savedInstanceState.getBoolean(Consts.KEY_PREFIX + ".AContentView_ContentEmpty", true));
        }
    }

    @Override
    public void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(setLayoutId(), null);
    }

    @Override
    public void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState) {

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
        outState.putBoolean(Consts.KEY_PREFIX + ".AContentView_ContentEmpty", isContentEmpty());
    }

    @Override
    public Activity getBridgeContext() {
        return getViewContext();
    }

    @Override
    public void setBridgeContext(Activity context) {
        setViewContext(context);
    }

    @Override
    public Activity getViewContext() {
        return mContext;
    }

    @Override
    public void setViewContext(Activity context) {
        this.mContext = context;
    }

}
