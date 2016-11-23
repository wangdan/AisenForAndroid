package org.aisen.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;

import org.aisen.android.R;
import org.aisen.android.R2;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 维护瀑布流的SwipeRefreshLayout控件
 *
 */
public abstract class AWaterfallSwipeRefreshFragment<T extends Serializable, Ts extends Serializable, Header extends Serializable>
                                    extends AWaterfallFragment<T, Ts, Header>
                                    implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public int inflateContentView() {
        return R.layout.comm_ui_waterfall_swiperefresh;
    }

    @Override
    void _layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        ButterKnife.bind(this, getContentView());

        super._layoutInit(inflater, savedInstanceSate);
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
    public void onRefreshViewFinished(RefreshMode mode) {
        if (mode != RefreshMode.update && swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

}
