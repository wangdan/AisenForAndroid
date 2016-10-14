package org.aisen.wen.ui.view.impl;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.aisen.wen.R;
import org.aisen.wen.R2;
import org.aisen.wen.ui.adapter.BasicListAdapter;
import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.itemview.AFooterItemView;
import org.aisen.wen.ui.itemview.AHeaderItemViewCreator;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by wangdan on 16/10/13.
 */
public abstract class AListView<Item extends Serializable, Result extends Serializable, Header extends Serializable>
                            extends APagingView<Item, Result, Header, ListView>
                            implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener{

    @BindView(R2.id.listView)
    ListView mListView;

    @Override
    public int setLayoutId() {
        return R.layout.comm_ui_list;
    }

    @Override
    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);

        // 设置事件
        getRefreshView().setOnScrollListener(this);
        getRefreshView().setOnItemClickListener(this);
    }

    @Override
    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {
        getRefreshView().addFooterView(footerItemView.getConvertView());
    }

    @Override
    protected void addHeaderViewToRefreshView(AHeaderItemViewCreator<?> headerItemViewCreator) {

    }

    @Override
    public void bindAdapter(IPagingAdapter adapter) {
        if (getRefreshView().getAdapter() == null)
            getRefreshView().setAdapter((BasicListAdapter) adapter);
    }

    @Override
    public IPagingAdapter<Item> newAdapter(ArrayList<Item> datas) {
        return new BasicListAdapter<>(this, datas);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        onScroll(firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        onScrollStateChanged(scrollState);
    }

    @Override
    public boolean handleResult(IPagingPresenter.RefreshMode mode, List<Item> datas) {
        return false;
    }

    @Override
    public int getFirstVisiblePosition() {
        return getRefreshView().getFirstVisiblePosition();
    }

    @Override
    public ListView getRefreshView() {
        return mListView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 初始化ListView
     *
     * @param items
     */
    public void setItems(ArrayList<Item> items) {
        if (items == null)
            return;

        getLoadingLayout().setVisibility(View.GONE);
        getFailureLayout().setVisibility(View.GONE);
        if (items.size() == 0 && emptyLayout != null) {
            getEmptyLayout().setVisibility(View.VISIBLE);
            getContentLayout().setVisibility(View.GONE);
        }
        else {
            getEmptyLayout().setVisibility(View.GONE);
            getContentLayout().setVisibility(View.VISIBLE);
        }
        getAdapter().getDatas().clear();
        getAdapter().getDatas().addAll(items);
        if (mListView.getAdapter() == null) {
            bindAdapter(getAdapter());
        }
        else {
            mListView.setSelectionFromTop(0, 0);
            getAdapter().notifyDataSetChanged();
        }
    }

}
