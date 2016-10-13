package org.aisen.wen.ui.view.impl;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import org.aisen.wen.R;
import org.aisen.wen.base.GlobalContext;
import org.aisen.wen.support.utils.SharedPreferencesUtils;
import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.itemview.AFooterItemView;
import org.aisen.wen.ui.itemview.AHeaderItemViewCreator;
import org.aisen.wen.ui.itemview.BasicFooterView;
import org.aisen.wen.ui.itemview.IITemView;
import org.aisen.wen.ui.itemview.IItemViewCreator;
import org.aisen.wen.ui.model.listener.PagingModelListenerParam;
import org.aisen.wen.ui.presenter.impl.APagingPresenter;
import org.aisen.wen.ui.view.IPaingView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wangdan on 16/10/12.
 */
public abstract class APagingView<Item extends Serializable, Result extends Serializable, Header extends Serializable, V extends ViewGroup>
                            extends AContentView implements IPaingView<Item, Result, Header, V>,
                                                            AFooterItemView.OnFooterViewCallback,
                                                            IPaingView.IPagingViewCallback {


    private static final String SAVED_DATAS = "org.aisen.android.ui.Datas";
    private static final String SAVED_CONFIG = "org.aisen.android.ui.Config";

    IItemViewCreator<Item> mFooterItemViewCreator;
    AFooterItemView<Item> mFooterItemView;// FooterView，滑动到底部时，自动加载更多数据
    AHeaderItemViewCreator<Header> mHeaderItemViewCreator;

    private IPagingAdapter<Item> mAdapter;

    RefreshConfig refreshConfig;// 刷新方面的配置

    private IPagingViewCallback pagingViewCallback;

    @Override
    public void onBridgeCreate(Bundle savedInstanceState) {
        super.onBridgeCreate(savedInstanceState);

        if (savedInstanceState == null) {
            refreshConfig = new RefreshConfig();
        }
        else {
            refreshConfig = (RefreshConfig) savedInstanceState.getSerializable(SAVED_CONFIG);
        }

        ArrayList<Item> datas = savedInstanceState == null ? new ArrayList<Item>()
                                                           : (ArrayList<Item>) savedInstanceState.getSerializable(SAVED_DATAS);
        mAdapter = newAdapter(datas);
    }

    @Override
    public void onBridgeSaveInstanceState(Bundle outState) {
        super.onBridgeSaveInstanceState(outState);

        if (refreshConfig != null)
            outState.putSerializable(SAVED_CONFIG, refreshConfig);

        onSaveDatas(outState);
    }

    /**
     * 数据量比较大的时候，子类可以不保存，会阻塞
     *
     * @param outState
     */
    protected void onSaveDatas(Bundle outState) {
        // 将数据保存起来
        if (getAdapter() != null && getAdapter().getDatas().size() != 0)
            outState.putSerializable(SAVED_DATAS, getAdapter().getDatas());
    }

    @Override
    public void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState) {
        super.onBridgeActivityCreate(activity, savedInstanceState);

        setupRefreshConfig(refreshConfig);

        setupRefreshView(savedInstanceState);

        setupRefreshViewWithConfig(refreshConfig);

        bindAdapter(getAdapter());
    }

    /**
     * 子类配置
     *
     * @param config
     */
    protected void setupRefreshConfig(RefreshConfig config) {
        config.emptyHint = getContext().getString(R.string.comm_content_empty);
    }

    /**
     * 初始化RefreshView
     *
     */
    protected void setupRefreshView(Bundle savedInstanceSate) {
        if (refreshConfig != null && refreshConfig.footerMoreEnable) {
            mFooterItemViewCreator = configFooterViewCreator();
            View convertView = mFooterItemViewCreator.newContentView(getContext().getLayoutInflater(), null, IPagingAdapter.TYPE_FOOTER);
            mFooterItemView = (AFooterItemView<Item>) mFooterItemViewCreator.newItemView(convertView, IPagingAdapter.TYPE_FOOTER);
        }

        mHeaderItemViewCreator = configHeaderViewCreator();

        if (mFooterItemView != null) {
            addFooterViewToRefreshView(mFooterItemView);
        }
        if (mHeaderItemViewCreator != null) {
            addHeaderViewToRefreshView(mHeaderItemViewCreator);
        }
    }

    @Override
    public boolean setRefreshViewToLoading() {
        return false;
    }

    /**
     * 根据Config刷新RefreshView
     */
    @Override
    public void setupRefreshViewWithConfig(RefreshConfig config) {

    }

    /**
     * 设置列表控件状态为刷新结束
     */
    public void onRefreshViewFinished(APagingPresenter.RefreshMode mode) {

    }

    @Override
    public IPagingAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onPullDownToRefresh() {
        if (pagingViewCallback != null) {
            pagingViewCallback.onPullDownToRefresh();
        }
    }

    @Override
    public void onPullUpToRefresh() {
        if (pagingViewCallback != null) {
            pagingViewCallback.onPullUpToRefresh();
        }
    }

    @Override
    public boolean isContentEmpty() {
        return getAdapter() == null || getAdapter().getDatas().size() == 0;
    }

    @Override
    public AHeaderItemViewCreator<Header> configHeaderViewCreator() {
        return null;
    }

    @Override
    public void setPagingViewCallback(IPagingViewCallback pagingViewCallback) {
        this.pagingViewCallback = pagingViewCallback;
    }

    protected IItemViewCreator<Item> configFooterViewCreator() {
        return new IItemViewCreator<Item>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<Item> newItemView(View convertView, int viewType) {
                return new BasicFooterView<>(getContext(), convertView, APagingView.this);
            }

        };
    }

    abstract protected void addFooterViewToRefreshView(AFooterItemView<?> footerItemView);

    abstract protected void addHeaderViewToRefreshView(AHeaderItemViewCreator<?> headerItemViewCreator);

    /*********************************************开始阅读位置历史************************************************/

    @Override
    public void toLastReadPosition() {
        if (getRefreshView() == null || TextUtils.isEmpty(refreshConfig.positionKey))
            return;

        if (getRefreshView() instanceof ListView) {
            ListView listView = (ListView) getRefreshView();
            listView.setSelectionFromTop(getLastReadPosition(), getLastReadTop() + listView.getPaddingTop());
        }
    }

    @Override
    public int getLastReadPosition() {
        return SharedPreferencesUtils.getIntShareData(GlobalContext.getInstance(), refreshConfig.positionKey + "Position", 0);
    }

    @Override
    public void putLastReadPosition(int position) {
        if (!TextUtils.isEmpty(refreshConfig.positionKey))
            SharedPreferencesUtils.putIntShareData(GlobalContext.getInstance(), refreshConfig.positionKey + "Position", position);
    }

    @Override
    public int getLastReadTop() {
        return SharedPreferencesUtils.getIntShareData(GlobalContext.getInstance(), refreshConfig.positionKey + "Top", 0);
    }

    @Override
    public void putLastReadTop(int top) {
        if (!TextUtils.isEmpty(refreshConfig.positionKey))
            SharedPreferencesUtils.putIntShareData(GlobalContext.getInstance(), refreshConfig.positionKey + "Top", top);
    }

    public int getFirstVisiblePosition() {
        return 0;
    }

    /*********************************************结束阅读位置历史************************************************/

    /**
     * 设置列表控件状态为刷新结束
     */
    @Override
    public void setRefreshViewFinished(APagingPresenter.RefreshMode mode) {

    }

    @Override
    public void onTaskStateChanged(PagingModelListenerParam<Result> param) {
        // 刷新FooterView
        if (refreshConfig == null || !refreshConfig.footerMoreEnable || mFooterItemView == null)
            return;

        if (mFooterItemView != null) {
            mFooterItemView.onTaskStateChanged(param);
        }
    }

    void onScrollStateChanged(int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && // 停止滚动
                !refreshConfig.pagingEnd && // 分页未加载完
                refreshConfig.footerMoreEnable && // 自动加载更多
                mFooterItemView != null // 配置了FooterView
                ) {
            int childCount = getRefreshView().getChildCount();
            if (childCount > 0 && getRefreshView().getChildAt(childCount - 1) == mFooterItemView.getConvertView()) {
                if (mFooterItemView != null) {
                    mFooterItemView.setFooterViewToRefreshing();
                }
            }
        }

        // 保存最后浏览位置
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (!TextUtils.isEmpty(refreshConfig.positionKey) && getRefreshView() != null) {
                putLastReadPosition(getFirstVisiblePosition());

                putLastReadTop(getRefreshView().getChildAt(0).getTop());
            }
        }
    }

    void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * FooterView加载更多数据使能
     *
     * @return
     */
    @Override
    public boolean footerViewLoadMoreAbility() {
        return refreshConfig == null || !refreshConfig.pagingEnd;
    }

    /**
     * FooterView开始加载更多
     *
     */
    @Override
    public void onFooterViewLoadMore() {
        if (footerViewLoadMoreAbility()) {
            onPullUpToRefresh();
        }
    }

    @Override
    public RefreshConfig getRefreshConfig() {
        return refreshConfig;
    }
}
