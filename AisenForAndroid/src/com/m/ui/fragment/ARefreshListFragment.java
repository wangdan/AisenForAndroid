package com.m.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.m.R;
import com.m.support.Inject.ViewInject;

public abstract class ARefreshListFragment<T extends Serializable, Ts extends Serializable> 
								extends ARefreshFragment<T, Ts, ListView> implements OnRefreshListener2<ListView> {

	@ViewInject(idStr = "refreshListView")
	private PullToRefreshListView refreshListView;
	
	private View mFooterView;

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		super._layoutInit(inflater, savedInstanceState);

		refreshListView.getRefreshableView().setRecyclerListener(this);
		refreshListView.setAdapter(getAdapter());
		refreshListView.setOnRefreshListener(this);
		refreshListView.setMode(Mode.BOTH);
	}
	
	@Override
	public AbsListView getRefreshView() {
		return refreshListView.getRefreshableView();
	}

	@Override
	protected int inflateContentView() {
		return R.layout.layout_refresh_listview;
	}
	
	protected PullToRefreshListView getRefreshListView() {
		return refreshListView;
	}

	@Override
	protected void taskStateChanged(ABaseTaskState state, Serializable extra) {
		super.taskStateChanged(state, extra);

		if (state == ABaseTaskState.canceled || state == ABaseTaskState.finished) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					getRefreshListView().onRefreshComplete();
				}
			}, 250);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		onPullDownToRefresh();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		onPullUpToRefresh();
	};
	
	@Override
	public boolean setRefreshing() {
		refreshListView.setRefreshing(true);
		
		// 这个控件默认会回调下拉事件，父类不再requestData()
		return true;
	}
	
	@Override
	public boolean isRefreshing() {
		return refreshListView.isRefreshing();
	}
	
	@Override
	public void setRefreshViewComplete() {
		if (refreshListView.isRefreshing())
			refreshListView.onRefreshComplete();
	}
	
	@Override
	public void resetRefreshView(RefreshConfig config) {
		if (mFooterView == null) {
			mFooterView = View.inflate(getActivity(), R.layout.layout_footerview, null);
			TextView btnLoadMore = (TextView) mFooterView.findViewById(R.id.btnLoadMore);
			btnLoadMore.setText(R.string.cannotRequestUpdate);
			View layLoading = mFooterView.findViewById(R.id.layLoading);
			layLoading.setVisibility(View.GONE);
		}

		setRefreshViewComplete();
		ListView listView = refreshListView.getRefreshableView();
		if (config.canLoadMore) {
			if (listView.getFooterViewsCount() > 0) {
				listView.removeFooterView(mFooterView);
			}
			
			refreshListView.setMode(Mode.BOTH);			
		}
		else {
			listView.addFooterView(mFooterView);
			
			refreshListView.setMode(Mode.PULL_FROM_START);
		}
	}
	
}
