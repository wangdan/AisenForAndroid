package org.aisen.wen.ui.itemview;

import android.app.Activity;
import android.view.View;

import org.aisen.wen.ui.adapter.ARecycleViewItemView;
import org.aisen.wen.ui.model.listener.PagingModelListenerParam;

import java.io.Serializable;

/**
 * FooterView
 *
 * Created by wangdan on 16/1/9.
 */
public abstract class AFooterItemView<Item extends Serializable> extends ARecycleViewItemView<Item> {

    private OnFooterViewCallback onFooterViewCallback;

    public AFooterItemView(Activity context, View itemView, OnFooterViewCallback callback) {
        super(context, itemView);

        this.onFooterViewCallback = callback;
    }

    protected OnFooterViewCallback getCallback() {
        return onFooterViewCallback;
    }

    abstract public void onTaskStateChanged(PagingModelListenerParam param);

    abstract public void setFooterViewToRefreshing();

    public interface OnFooterViewCallback {

        void onFooterViewLoadMore();

        boolean footerViewLoadMoreAbility();

    }

}
