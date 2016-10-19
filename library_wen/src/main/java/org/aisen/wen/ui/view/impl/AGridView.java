package org.aisen.wen.ui.view.impl;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Created by wangdan on 16/10/14.
 */
public abstract class AGridView<Item extends Serializable, Result extends Serializable, Header extends Serializable>
                            extends APagingView<Item, Result, Header, GridView> implements AdapterView.OnItemClickListener {

    @BindView(R2.id.gridview)
    GridView gridView;

    @Override
    public int setLayoutId() {
        return R.layout.comm_ui_gridview;
    }

    @Override
    public GridView getRefreshView() {
        return gridView;
    }

    @Override
    protected void setupRefreshView(Bundle savedInstanceSate) {
        super.setupRefreshView(savedInstanceSate);

        // 设置事件
        getRefreshView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public IPagingAdapter<Item> newAdapter(ArrayList<Item> datas) {
        return new BasicListAdapter<>(this, datas);
    }

    @Override
    public boolean handleResult(IPagingPresenter.RefreshMode mode, List<Item> list, Result result) {
        return false;
    }

    @Override
    public void bindAdapter(IPagingAdapter adapter) {
        if (getRefreshView().getAdapter() == null)
            getRefreshView().setAdapter((BasicListAdapter) adapter);
    }

    @Override
    protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView) {

    }

    @Override
    protected void addHeaderViewToRefreshView(AHeaderItemViewCreator<?> headerItemViewCreator) {

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
        if (getRefreshView().getAdapter() == null) {
            bindAdapter(getAdapter());
        }
        else {
            getRefreshView().smoothScrollToPosition(0);
            getAdapter().notifyDataSetChanged();
        }
    }

}
