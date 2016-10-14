package org.aisen.wen.ui.view.impl;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import org.aisen.wen.R;
import org.aisen.wen.R2;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by wangdan on 16/10/14.
 */
public abstract class AListViewSwipeRefreshVIew<Item extends Serializable, Result extends Serializable, Header extends Serializable>
                    extends AListView<Item, Result, Header>
                    implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public int setLayoutId() {
        return R.layout.comm_ui_list_swiperefresh;
    }

    @Override
    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);

        setupSwipeRefreshLayout();
    }

    protected void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        onPullDownToRefresh();
    }

    @Override
    public boolean setRefreshViewToLoading() {
        swipeRefreshLayout.setRefreshing(true);

        return false;
    }

    @Override
    public void setRefreshViewFinished(IPagingPresenter.RefreshMode mode) {
        if (mode != IPagingPresenter.RefreshMode.update && swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

}
