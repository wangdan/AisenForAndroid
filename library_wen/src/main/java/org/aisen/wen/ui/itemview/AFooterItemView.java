package org.aisen.wen.ui.itemview;

import android.app.Activity;
import android.view.View;

import org.aisen.wen.ui.adapter.ARecycleViewItemView;

import java.io.Serializable;

/**
 * FooterView
 *
 * Created by wangdan on 16/1/9.
 */
public abstract class AFooterItemView<Item extends Serializable> extends ARecycleViewItemView<Item>
                                                                implements OnFooterViewListener {

    private OnFooterViewCallback onFooterViewCallback;

    public AFooterItemView(Activity context, View itemView, OnFooterViewCallback callback) {
        super(context, itemView);

        this.onFooterViewCallback = callback;
    }

    protected OnFooterViewCallback getCallback() {
        return onFooterViewCallback;
    }

    public interface OnFooterViewCallback {

        void onLoadMore();

        boolean canLoadMore();

    }

}
