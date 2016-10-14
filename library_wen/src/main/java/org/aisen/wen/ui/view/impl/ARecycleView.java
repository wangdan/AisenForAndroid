package org.aisen.wen.ui.view.impl;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import org.aisen.wen.R;
import org.aisen.wen.R2;
import org.aisen.wen.ui.adapter.BasicRecycleViewAdapter;
import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.itemview.AFooterItemView;
import org.aisen.wen.ui.itemview.AHeaderItemViewCreator;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * RecycleView的PagingView实现
 *
 * Created by wangdan on 16/10/13.
 */
public abstract class ARecycleView<Item extends Serializable, Result extends Serializable, Header extends Serializable>
                extends APagingView<Item, Result, Header, RecyclerView>
                implements AdapterView.OnItemClickListener {

    @BindView(R2.id.recycleview)
    RecyclerView mRecycleView;

    @Override
    public int setLayoutId() {
        return R.layout.comm_ui_recycleview;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void setupRefreshConfig(RefreshConfig config) {
        super.setupRefreshConfig(config);

        getRefreshView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                ARecycleView.this.onScrollStateChanged(newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });
        getRefreshView().setLayoutManager(configLayoutManager());
    }

    @Override
    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {
        ((BasicRecycleViewAdapter) getAdapter()).addFooterView(footerItemView);
    }

    @Override
    protected void addHeaderViewToRefreshView(AHeaderItemViewCreator<?> headerItemViewCreator) {
        ((BasicRecycleViewAdapter) getAdapter()).setHeaderItemViewCreator(headerItemViewCreator);
    }

    @Override
    public void toLastReadPosition() {
        super.toLastReadPosition();
        if (getRefreshView() == null || TextUtils.isEmpty(refreshConfig.positionKey) ||
                getLastReadPosition() < 0)
            return;

        if (getRefreshView().getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager manager = (LinearLayoutManager) getRefreshView().getLayoutManager();

            if (getAdapter().getDatas().size() > getLastReadPosition()) {
                manager.scrollToPositionWithOffset(getLastReadPosition(), getLastReadTop() + getRefreshView().getPaddingTop());
            }
        }
    }

    @Override
    public int getFirstVisiblePosition() {
        if (getRefreshView().getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager manager = (LinearLayoutManager) getRefreshView().getLayoutManager();

            return manager.findFirstVisibleItemPosition();
        }

        return 0;
    }

    @Override
    public void bindAdapter(IPagingAdapter adapter) {
        if (getRefreshView().getAdapter() == null) {
            getRefreshView().setAdapter((BasicRecycleViewAdapter) adapter);
        }

        if (((BasicRecycleViewAdapter) getAdapter()).getOnItemClickListener() != this) {
            ((BasicRecycleViewAdapter) getAdapter()).setOnItemClickListener(this);
        }
    }

    @Override
    public IPagingAdapter<Item> newAdapter(ArrayList<Item> datas) {
        return new BasicRecycleViewAdapter<>(getViewContext(), this, newItemViewCreator(), datas);
    }

    @Override
    public boolean handleResult(IPagingPresenter.RefreshMode mode, List<Item> datas) {
        return false;
    }

    @Override
    public RecyclerView getRefreshView() {
        return mRecycleView;
    }

    /**
     * 默认是LinearLayoutManager
     *
     * @return
     */
    protected RecyclerView.LayoutManager configLayoutManager() {
        return new LinearLayoutManager(getViewContext());
    }

}
