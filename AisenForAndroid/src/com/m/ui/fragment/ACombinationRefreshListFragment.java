package com.m.ui.fragment;

import java.io.Serializable;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.m.R;
import com.m.common.context.GlobalContext;
import com.m.common.settings.SettingUtility;
import com.m.support.Inject.ViewInject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * 思来想去，刷新页面都差不多，干脆统一放置到一起<br/>
 * 统一操作规范：下拉刷新、FooterView自动加载更多
 * 
 * @author Jeff.Wang
 *
 * @date 2014年9月29日
 */
public abstract class ACombinationRefreshListFragment<T extends Serializable, Ts extends Serializable> 
							extends ARefreshFragment<T, Ts, ListView> 
								implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener, OnRefreshListener, OnRefreshListener2<ListView> {

	static String TAG = "ACombinationRefreshListFragment";

	public static final String REFRESH_LIST_TYPE = "pRefreshViewType";
	
	public enum RefreshListType {
		pulltorefresh,// 
		actionbarPulltorefresh,// 
		androidPulltorefresh// google v4包刷新控件
	}
	
	private RefreshListType listType;
	
	@ViewInject(idStr = "listView")
	ListView mListView;
	@ViewInject(idStr = "refreshListView")
	PullToRefreshListView refreshListView;
	@ViewInject(idStr = "swipeRefreshLayout")
	SwipeRefreshLayout swipeRefreshLayout;
	@ViewInject(idStr = "listView_2")
	ListView mListView_2;
	
	private boolean isResetRefreshing = false;// 2014-10-10 新增这个属性，当控件是PullToRefreshListView时，调用setRefresh()方法没有重置数据，而是refresh数据的BUG

	protected View mFooterView;
	View layLoading;
	TextView txtLoadingHint;
	TextView btnLoadMore;

	private PullToRefreshLayout mPullToRefreshLayout;
	private boolean autoLoadMore = true;

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		listType = configListType();

		mFooterView = View.inflate(getActivity(), R.layout.layout_footerview, null);
		SmoothProgressBar mHeaderProgressBar = (SmoothProgressBar) mFooterView.findViewById(R.id.progress);
		mHeaderProgressBar.setIndeterminate(true);
		layLoading = mFooterView.findViewById(R.id.layLoading);
		txtLoadingHint = (TextView) mFooterView.findViewById(R.id.txtLoadingHint);
		btnLoadMore = (TextView) mFooterView.findViewById(R.id.btnLoadMore);

		super._layoutInit(inflater, savedInstanceState);

		initRefreshList(savedInstanceState);
		getListView().addFooterView(mFooterView);

		setRefreshList();

		resetRefreshView(getConfig());
		
		if (savedInstanceState == null) {
			setFooterRefreshing();
		}
	}
	
	protected void initRefreshList(Bundle savedInstanceState) {
		
	}
	
	private void setRefreshList() {
		if (listType == RefreshListType.actionbarPulltorefresh) {
			getListView().setAdapter(getAdapter());
			mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
			ActionBarPullToRefresh.from(getActivity())
										.insertLayoutInto((ViewGroup) findViewById(refreshLayoutInfo()))
										.theseChildrenArePullable(R.id.listView).listener(this).setup(mPullToRefreshLayout);
			
			if (refreshListView != null)
				refreshListView.setVisibility(View.GONE);
			if (swipeRefreshLayout != null)
				swipeRefreshLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		else if (listType == RefreshListType.pulltorefresh) {
			refreshListView.setAdapter(getAdapter());
			refreshListView.setOnRefreshListener(this);
			refreshListView.getRefreshableView().setRecyclerListener(this);
			refreshListView.getRefreshableView().setOnScrollListener(this);
			refreshListView.setMode(Mode.PULL_FROM_START);
			
			refreshListView.setVisibility(View.VISIBLE);
			if (mListView != null)
				mListView.setVisibility(View.GONE);
			if (swipeRefreshLayout != null)
				swipeRefreshLayout.setVisibility(View.GONE);
		}
		else if (listType == RefreshListType.androidPulltorefresh) {
			getListView().setAdapter(getAdapter());
			swipeRefreshLayout.setOnRefreshListener(this);
			swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
								            android.R.color.holo_green_light, 
								            android.R.color.holo_orange_light, 
								            android.R.color.holo_red_light);

			swipeRefreshLayout.setVisibility(View.VISIBLE);
			if (mListView != null)
				mListView.setVisibility(View.GONE);
			if (refreshListView != null)
				refreshListView.setVisibility(View.GONE);
		}
	}

	@Override
	public AbsListView getRefreshView() {
		if (listType == RefreshListType.actionbarPulltorefresh)
			return mListView;
		else if (listType == RefreshListType.pulltorefresh)
			return refreshListView.getRefreshableView();
		else if (listType == RefreshListType.androidPulltorefresh)
			return mListView_2;
		
		return null;
	}

	public int refreshLayoutInfo() {
		return R.id.layoutContent;
	}
	
	@Override
	public void onPullDownToRefresh() {
		if (isResetRefreshing) {
			isResetRefreshing = false;

			requestData(RefreshMode.reset);
		}
		else {
			super.onPullDownToRefresh();
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		requestData(RefreshMode.refresh);
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
	public void onRefresh() {
		onPullDownToRefresh();
	}

	@Override
	protected int inflateContentView() {
		return R.layout.layout_combination_list;
	}

	private ListView getListView() {
		return (ListView) getRefreshView();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);

		if (scrollState == SCROLL_STATE_FLING) {
		} else if (scrollState == SCROLL_STATE_IDLE) {
			for (int i = 0; i < getListView().getFooterViewsCount(); i++) {
				if (getListView().getChildAt(getListView().getChildCount() - i - 1) == mFooterView) {
					if (autoLoadMore) {
						setFooterRefreshing();
						
						onPullUpToRefresh();
					}
					
					break;
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
		if (listType == RefreshListType.actionbarPulltorefresh) {
			mPullToRefreshLayout.setRefreshing(true);
			
			return false;
		}
		else if (listType == RefreshListType.pulltorefresh) {
			isResetRefreshing = true;
			
			refreshListView.setRefreshing(true);
			
			// 这个控件默认会回调下拉事件，父类不再requestData()
			return true;
		}
		else if (listType == RefreshListType.androidPulltorefresh) {
			swipeRefreshLayout.setRefreshing(true);
			
			return false;
		}
		
		return false;
	}

	@Override
	public boolean isRefreshing() {
		if (listType == RefreshListType.actionbarPulltorefresh)
			if(mPullToRefreshLayout.isRefreshing()) return true;
		else if (listType == RefreshListType.pulltorefresh)
			if (refreshListView.isRefreshing()) return true;
		else if (listType == RefreshListType.androidPulltorefresh)
			if (swipeRefreshLayout.isRefreshing()) return true;
		
		return super.isRefreshing();
	}

	@Override
	public void setRefreshViewComplete() {
		if (listType == RefreshListType.actionbarPulltorefresh) {
			if (mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshComplete();
		}
		else if (listType == RefreshListType.pulltorefresh) {
			if (refreshListView.isRefreshing())
				refreshListView.onRefreshComplete();
		}
		else if (listType == RefreshListType.androidPulltorefresh) {
			if (swipeRefreshLayout.isRefreshing())
				swipeRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	protected void taskStateChanged(ABaseTaskState state, Serializable extra) {
		super.taskStateChanged(state, extra);

		if (state == ABaseTaskState.finished) {
			if (mPullToRefreshLayout != null && mPullToRefreshLayout.isRefreshing() && layLoading.getVisibility() != View.VISIBLE)
				setRefreshViewComplete();

			layLoading.setVisibility(View.GONE);
			btnLoadMore.setVisibility(View.VISIBLE);
		} else if (state == ABaseTaskState.prepare) {
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
		} else {
			layLoading.setVisibility(View.GONE);
			btnLoadMore.setVisibility(View.VISIBLE);
			btnLoadMore.setText(R.string.cannotRequestUpdate);
			btnLoadMore.setOnClickListener(null);
		}
	}
	
	protected RefreshListType configListType() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GlobalContext.getInstance());
		int value = Integer.parseInt(prefs.getString("pRefreshViewType", 
							SettingUtility.getPermanentSettingAsStr(REFRESH_LIST_TYPE, "0")));
		
		switch (value) {
		case 0:
			return RefreshListType.pulltorefresh;
		case 1:
			return RefreshListType.actionbarPulltorefresh;
		case 2:
			return RefreshListType.androidPulltorefresh;
		default:
			return RefreshListType.actionbarPulltorefresh;
		}
	}
	
	protected View getFooterView() {
		return mFooterView;
	}
	
	protected PullToRefreshLayout getPullToRefreshLayout() {
		return mPullToRefreshLayout;
	}
	
}
