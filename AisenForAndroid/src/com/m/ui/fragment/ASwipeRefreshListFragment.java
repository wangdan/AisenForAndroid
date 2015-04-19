package com.m.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.m.R;
import com.m.support.inject.ViewInject;

import java.io.Serializable;

/**
 * Created by wangdan on 15-1-19.
 */
public abstract class ASwipeRefreshListFragment<T extends Serializable, Ts extends Serializable>
                                            extends ARefreshFragment<T, Ts, ListView>
                                            implements SwipeRefreshLayout.OnRefreshListener {
    static String TAG = "ASwipeRefreshListFragment";

    @ViewInject(idStr = "swipeRefreshLayout")
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewInject(idStr = "listView")
    ListView mListView;

    protected View mFooterView;

    @Override
    void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        mFooterView = View.inflate(getActivity(), R.layout.comm_lay_footerview, null);

        super._layoutInit(inflater, savedInstanceState);

        initRefreshList(savedInstanceState);

        setRefreshList();

        resetRefreshView(getConfig());
    }

    protected void initRefreshList(Bundle savedInstanceState) {
    	if (getConfig().hasFooterView)
    		getListView().addFooterView(mFooterView);
    }

    protected void setRefreshList() {
        getListView().setAdapter(getAdapter());
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public AbsListView getRefreshView() {
        return mListView;
    }

    @Override
    public void onRefresh() {
        onPullDownToRefresh();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.comm_lay_swiperefreshlist;
    }

    private ListView getListView() {
        return (ListView) getRefreshView();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);

        if (scrollState == SCROLL_STATE_FLING) {
        } else if (scrollState == SCROLL_STATE_IDLE) {
        	if (getConfig().hasFooterView) {
        		for (int i = 0; i < getListView().getFooterViewsCount(); i++) {
                    if (getListView().getChildAt(getListView().getChildCount() - i - 1) == mFooterView) {
                        if (getConfig().canLoadMore) {
                        	final View layLoading = mFooterView.findViewById(R.id.layLoading);
                        	final TextView btnLoadMore = (TextView) mFooterView.findViewById(R.id.btnLoadMore);
                        	layLoading.setVisibility(View.VISIBLE);
                            btnLoadMore.setVisibility(View.GONE);

                            onPullUpToRefresh();
                        }

                        break;
                    }
                }
        	}
        }
    }

    @Override
    public boolean setRefreshing() {
        swipeRefreshLayout.setRefreshing(true);

        return false;
    }

    @Override
    public boolean isRefreshing() {
        if (swipeRefreshLayout.isRefreshing())
            return true;

        return super.isRefreshing();
    }

    @Override
    public void setRefreshViewComplete() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void taskStateChanged(ABaseTaskState state, Serializable extra) {
        super.taskStateChanged(state, extra);

        if (state == ABaseTaskState.finished) {
        	setRefreshViewComplete();

        	if (getConfig().hasFooterView) {
        		final View layLoading = mFooterView.findViewById(R.id.layLoading);
                final TextView btnLoadMore = (TextView) mFooterView.findViewById(R.id.btnLoadMore);
                layLoading.setVisibility(View.GONE);
                btnLoadMore.setVisibility(View.VISIBLE);
        	}
        } else if (state == ABaseTaskState.prepare) {
//            if (loadingLayout != null && loadingLayout.getVisibility() != View.VISIBLE
//                    && layLoading.getVisibility() != View.VISIBLE) {
//                setRefreshing();
//            }
        }
    };

    @Override
    public void resetRefreshView(RefreshConfig config) {
        if (getConfig().hasFooterView) {
        	final View layLoading = mFooterView.findViewById(R.id.layLoading);
        	TextView txtLoadingHint = (TextView) mFooterView.findViewById(R.id.txtLoadingHint);
        	final TextView btnLoadMore = (TextView) mFooterView.findViewById(R.id.btnLoadMore);
            
            if (config.canLoadMore) {
                layLoading.setVisibility(View.GONE);
                btnLoadMore.setText(getConfig().loadMoreLabel);
                if (TextUtils.isEmpty(txtLoadingHint.getText()))
                    txtLoadingHint.setText(getConfig().loadingLabel);
                btnLoadMore.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        layLoading.setVisibility(View.VISIBLE);
                        btnLoadMore.setVisibility(View.GONE);

                        onPullUpToRefresh();
                    }
                });
            } else {
                layLoading.setVisibility(View.GONE);
                btnLoadMore.setVisibility(View.VISIBLE);
                btnLoadMore.setText(getConfig().loadDisableLabel);
                btnLoadMore.setOnClickListener(null);
            }
        }
    }

    protected View getFooterView() {
        return mFooterView;
    }

}
