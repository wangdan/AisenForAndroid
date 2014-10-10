package com.m.ui.fragment;

import java.io.Serializable;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.m.R;
import com.m.support.Inject.ViewInject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public abstract class ASwipeRefreshListFragment<T extends Serializable, Ts extends Serializable> extends ARefreshFragment<T, Ts, ListView> 
									implements OnRefreshListener {

	static String TAG = "ASwipeRefreshListFragment";
	
	@ViewInject(idStr = "listView")
	ListView mListView;
	
	protected View mFooterView;
	View layLoading;
	TextView txtLoadingHint;
	TextView btnLoadMore;
	
	private PullToRefreshLayout mPullToRefreshLayout;
	private boolean autoLoadMore = true;

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		super._layoutInit(inflater, savedInstanceState);

		mFooterView = View.inflate(getActivity(), R.layout.layout_footerview, null);
		layLoading = mFooterView.findViewById(R.id.layLoading);
		txtLoadingHint = (TextView) mFooterView.findViewById(R.id.txtLoadingHint);
		btnLoadMore = (TextView) mFooterView.findViewById(R.id.btnLoadMore);
		
		resetRefreshView(getConfig());
		
		mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
		ActionBarPullToRefresh.from(getActivity()).insertLayoutInto((ViewGroup) findViewById(refreshLayoutInfo()))
				.theseChildrenArePullable(R.id.listView).listener(this).setup(mPullToRefreshLayout);
		
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(getAdapter());
		
		SmoothProgressBar mHeaderProgressBar = (SmoothProgressBar) mFooterView.findViewById(R.id.progress);
		mHeaderProgressBar.setIndeterminate(true);
		
		if (savedInstanceState == null) {
			setFooterRefreshing();
		}
	}
	
	@Override
	public AbsListView getRefreshView() {
		return mListView;
	}
	
	public int refreshLayoutInfo() {
		return R.id.layoutContent;
	}
	
	@Override
	public void onRefreshStarted(View view) {
		requestData(RefreshMode.refresh);
	}
	
	@Override
	protected int inflateContentView() {
		return R.layout.layout_swiperefreshlist;
	}

	private ListView getListView() {
		return mListView;
	}
	
	public PullToRefreshLayout getPullToRefreshLayout() {
		return mPullToRefreshLayout;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);
		
		if (scrollState == SCROLL_STATE_FLING) {
		}
		else if (scrollState == SCROLL_STATE_IDLE) {
			if (getListView().getFooterViewsCount() == 0)
				getListView().addFooterView(mFooterView);
			
			if (getListView().getChildAt(getListView().getChildCount() - 1) == mFooterView) {
				if (autoLoadMore) {
					setFooterRefreshing();
					
					onPullUpToRefresh();
				}
			}
		}
	}
	
	public boolean setFooterRefreshing() {
		layLoading.setVisibility(View.VISIBLE);
		btnLoadMore.setVisibility(View.GONE);
		
		return false;
	}
	
	@Override
	public boolean setRefreshing() {
		mPullToRefreshLayout.setRefreshing(true);
		
		return false;
	}
	
	@Override
	public boolean isRefreshing() {
		return mPullToRefreshLayout.isRefreshing();
	}
	
	@Override
	public void setRefreshViewComplete() {
		if (mPullToRefreshLayout.isRefreshing())
			mPullToRefreshLayout.setRefreshComplete();
	}

	@Override
	protected void taskStateChanged(ABaseTaskState state, Serializable extra) {
		super.taskStateChanged(state, extra);

		if (state == ABaseTaskState.finished) {
			if (mPullToRefreshLayout != null && mPullToRefreshLayout.isRefreshing()
					&& layLoading.getVisibility() != View.VISIBLE)
				setRefreshViewComplete();
			
			layLoading.setVisibility(View.GONE);
			btnLoadMore.setVisibility(View.VISIBLE);
		}
		else if (state == ABaseTaskState.prepare) {
			if (loadingLayout != null && loadingLayout.getVisibility() != View.VISIBLE && mPullToRefreshLayout != null
					&& layLoading.getVisibility() != View.VISIBLE) {
				setRefreshing();
			}
		}
	};
	
	@Override
	public void resetRefreshView(RefreshConfig config) {
		autoLoadMore = config.canLoadMore;
		
		if (autoLoadMore) {
			layLoading.setVisibility(View.GONE);
			// 没有设置文本
			if (TextUtils.isEmpty(btnLoadMore.getText()))
				btnLoadMore.setText(R.string.hintLoadMore);
			if (TextUtils.isEmpty(txtLoadingHint.getText()))
				txtLoadingHint.setText(R.string.hintLoading);
			btnLoadMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					layLoading.setVisibility(View.VISIBLE);
					btnLoadMore.setVisibility(View.GONE);
					
					onPullUpToRefresh();
				}
			});
		}
		else {
			layLoading.setVisibility(View.GONE);
			btnLoadMore.setVisibility(View.VISIBLE);
			btnLoadMore.setText(R.string.cannotRequestUpdate);
			btnLoadMore.setOnClickListener(null);
		}
	}

}
