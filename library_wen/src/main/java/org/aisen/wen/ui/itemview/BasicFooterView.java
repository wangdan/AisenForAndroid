package org.aisen.wen.ui.itemview;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import org.aisen.wen.R;
import org.aisen.wen.R2;
import org.aisen.wen.ui.model.listener.PagingModelListenerParam;
import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangdan on 16/1/9.
 */
public class BasicFooterView<T extends Serializable> extends AFooterItemView<T> {

    public static final int LAYOUT_RES = R.layout.comm_lay_footerview;

    private View footerView;

    @BindView(R2.id.btnMore)
    TextView btnMore;
    @BindView(R2.id.layLoading)
    View layLoading;
    @BindView(R2.id.txtLoading)
    TextView txtLoading;

    public BasicFooterView(Activity context, View itemView, OnFooterViewCallback callback) {
        super(context, itemView, callback);

        this.footerView = itemView;

        ButterKnife.bind(this, getConvertView());

        btnMore.setVisibility(View.VISIBLE);
        layLoading.setVisibility(View.GONE);

        btnMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getCallback() != null && getCallback().footerViewLoadMoreAbility()) {
                    getCallback().onFooterViewLoadMore();
                }
            }

        });
    }

    @Override
    public void onBindView(View convertView) {

    }

    @Override
    public void onBindData(View convertView, T data, int position) {
    }

    @Override
    public View getConvertView() {
        return footerView;
    }

    @Override
    public void onTaskStateChanged(PagingModelListenerParam param) {
        IContentPresenter.TaskState state = param.getTaskState();
        IPagingPresenter.RefreshMode mode = param.getRefreshMode();

        if (state == IContentPresenter.TaskState.finished) {
            if (mode == IPagingPresenter.RefreshMode.update) {
                if (layLoading.getVisibility() == View.VISIBLE) {
                    layLoading.setVisibility(View.GONE);
                    btnMore.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (state == IContentPresenter.TaskState.prepare) {
            if (mode == IPagingPresenter.RefreshMode.update) {
                txtLoading.setText(loadingText());
                layLoading.setVisibility(View.VISIBLE);
                btnMore.setVisibility(View.GONE);
                btnMore.setText(moreText());
            }
        }
        else if (state == IContentPresenter.TaskState.success) {
            if ((mode == IPagingPresenter.RefreshMode.update || mode == IPagingPresenter.RefreshMode.reset)) {
                if (!getCallback().footerViewLoadMoreAbility()) {
                    btnMore.setText(endpagingText());
                } else {
                    btnMore.setText(moreText());
                }
            }
        }
        else if (state == IContentPresenter.TaskState.falid) {
            if (mode == IPagingPresenter.RefreshMode.update) {
                if (layLoading.getVisibility() == View.VISIBLE) {
                    btnMore.setText(faildText());
                }
            }
        }
    }

    @Override
    public void setFooterViewToRefreshing() {
        if (layLoading.getVisibility() != View.VISIBLE) {
            layLoading.setVisibility(View.VISIBLE);

            getCallback().onFooterViewLoadMore();
        }
    }

    protected String moreText() {
        return getContext().getString(R.string.comm_footer_more);
    }

    protected String loadingText() {
        return getContext().getString(R.string.comm_footer_loading);
    }

    protected String endpagingText() {
        return getContext().getString(R.string.comm_footer_pagingend);
    }

    protected String faildText() {
        return getContext().getString(R.string.comm_footer_faild);
    }

}
