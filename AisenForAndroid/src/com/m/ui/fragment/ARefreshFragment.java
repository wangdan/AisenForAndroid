package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.android.loader.BitmapLoader;

import com.m.R;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.support.adapter.*;
import com.m.support.adapter.ABaseAdapter.AbstractItemView;
import com.m.support.iclass.IResult;
import com.m.support.paging.IPaging;
import com.m.support.paging.PagingProxy;
import com.m.support.task.TaskException;
import com.m.ui.activity.BaseActivity.IAcUnusedDoubleClickedHandler;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.AbsListView.RecyclerListener;

public abstract class ARefreshFragment<T extends Serializable, Ts extends Serializable, V extends View> extends ABaseFragment 
									implements RecyclerListener, OnScrollListener, IAcUnusedDoubleClickedHandler {

	public static final String TAG = "ARefresh";
	
	private static final String SAVED_ADAPTER_DATAS = "com.m.ui.SAVED_ADAPTER_DATAS";
	private static final String SAVED_PAGINGPROCESSORPROXY = "com.m.ui.SAVED_PAGINGPROCESSORPROXY";

	@SuppressWarnings("rawtypes")
	PagingProxy mPagingProxy;

	private ABaseAdapter<T> mAdapter;
	
	private SwingBottomInAnimationAdapter swingAnimAdapter;
	
	@SuppressWarnings("rawtypes")
	private PagingTask pagingTask;
	
	private RefreshConfig refreshConfig;

	public enum RefreshMode {
		/**
		 * 重设数据
		 */
		reset,
		/**
		 * 拉取更多
		 */
		update,
		/**
		 * 刷新最新
		 */
		refresh
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<T> datas = savedInstanceState == null ? new ArrayList<T>() : (ArrayList<T>) savedInstanceState.getSerializable(SAVED_ADAPTER_DATAS);
		mAdapter = new MyBaseAdapter(datas, getActivity());
		
		if (savedInstanceState != null && savedInstanceState.getSerializable(SAVED_PAGINGPROCESSORPROXY) != null) {
			mPagingProxy = (PagingProxy) savedInstanceState.getSerializable(SAVED_PAGINGPROCESSORPROXY);
		} else {
			IPaging paging = configPaging();
			if (paging != null)
				mPagingProxy = new PagingProxy<T, Ts>(paging);
		}

		refreshConfig = savedInstanceState == null ? new RefreshConfig() 
												   : (RefreshConfig) savedInstanceState.getSerializable("refreshConfig");
		
		if (refreshConfig.animEnable) {
			swingAnimAdapter = new SwingBottomInAnimationAdapter(mAdapter) {
				
				@Override
				protected Animator getAnimator(ViewGroup parent, View view) {
					if (refreshConfig != null && !refreshConfig.animEnable)
						return null;
					
					return super.getAnimator(parent, view);
				}
				
			};
		}
	}
	
	Handler mHandler = new Handler() {
		
	};
	
	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super._layoutInit(inflater, savedInstanceSate);
		
		if (getRefreshView() != null) {
			getRefreshView().setOnScrollListener(this);
			getRefreshView().setRecyclerListener(this);
		}
		
		if (refreshConfig == null)
			refreshConfig = new RefreshConfig();
		
		if (swingAnimAdapter != null)
			swingAnimAdapter.setAbsListView(getRefreshView());
		
		if (!refreshConfig.canLoadMore)
			resetRefreshView(refreshConfig);
		
		config(refreshConfig);
		
		if (emptyLayout != null) {
			TextView txtView = (TextView) emptyLayout.findViewById(R.id.txtLoadFailed);
			if (txtView != null)
				txtView.setText(refreshConfig.emptyLabel);
		}
		if (loadFailureLayout != null) {
			TextView txtView = (TextView) loadFailureLayout.findViewById(R.id.txtLoadFailed);
			if (txtView != null)
				txtView.setText(refreshConfig.faildLabel);
		}
	}
	
	/**
	 * 子类配置
	 * 
	 * @param config
	 */
	protected void config(RefreshConfig config) {
		
	}
	
	final protected RefreshConfig getConfig() {
		return refreshConfig;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}
	
	private boolean isScrolling = false;
	private boolean ignoreScroll = false;// 忽略滚动的情况，实时加载图片
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (!ignoreScroll) {
			if (scrollState == SCROLL_STATE_FLING) {
				isScrolling = true;
			}
			else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				isScrolling = true;
			}
			else if (scrollState == SCROLL_STATE_IDLE) {
				isScrolling = false;
				
				notifyDataSetChanged();
				
				if (!TextUtils.isEmpty(getLastReadKey()) && getRefreshView() != null) {
					putLastReadPosition(getRefreshView().getFirstVisiblePosition());
					
					putLastReadTop(getRefreshView().getChildAt(0).getTop());
				}
			}
		}
	}
	
	@Override
	public boolean canDisplay() {
		if (ignoreScroll)
			return true;
		
		return !isScrolling;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// 将分页信息保存起来
		if (mPagingProxy != null)
			outState.putSerializable(SAVED_PAGINGPROCESSORPROXY, mPagingProxy);

		saveRefreshListState(outState);

		outState.putSerializable("refreshConfig", refreshConfig);
		
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * 数据量比较大的时候，子类可以不保存，会阻塞
	 * 
	 * @param outState
	 */
	protected void saveRefreshListState(Bundle outState) {
		// 将数据保存起来
		if (getAdapterItems() != null && getAdapterItems().size() != 0)
			outState.putSerializable(SAVED_ADAPTER_DATAS, getAdapterItems());
	}

	@Override
	public void requestData() {
		requestData(RefreshMode.reset);
	}

	protected BaseAdapter getAdapter() {
		if (swingAnimAdapter != null)
			return swingAnimAdapter;
		
		return mAdapter;
	}
	
	ABaseAdapter<T> getABaseAdapter() {
		return mAdapter;
	}

	public void onPullDownToRefresh() {
		requestData(RefreshMode.refresh);
	}

	public void onPullUpToRefresh() {
		requestData(RefreshMode.update);
	}
	
	@Override
	public boolean isContentEmpty() {
		return getAdapterItems() == null || getAdapterItems().size() == 0;
	}

	/**
	 * 分页线程，根据{@link IPaging}构造的分页参数列表调用接口
	 * 
	 * @author wangdan
	 * 
	 * @param <Params>
	 * @param <Progress>
	 * @param <Result>
	 */
	protected abstract class PagingTask<Params, Progress, Result extends Serializable> extends ABaseTask<Params, Progress, Result> {

		final protected RefreshMode mode;

		public PagingTask(String taskId, RefreshMode mode) {
			super(taskId);
			this.mode = mode;
			pagingTask = this;

			if (mode == RefreshMode.reset && mPagingProxy != null)
				mPagingProxy.newInstance();
		}

		@Override
		public Result workInBackground(Params... params) throws TaskException {
			String previousPage = null;
			String nextPage = null;

			if (mPagingProxy != null) {
				previousPage = mPagingProxy.getPreviousPage();
				nextPage = mPagingProxy.getNextPage();
			}

			return workInBackground(mode, previousPage, nextPage, params);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onSuccess(Result result) {
			if (result == null || getActivity() == null) {
				super.onSuccess(result);
				return;
			}
			
			List<T> resultList;
			if (result instanceof List)
				resultList = (List<T>) result;
			else {
				resultList = parseResult(result);
				if (resultList == null)
					resultList = new ArrayList<T>();
			}

			// 如果子类没有处理新获取的数据刷新UI，默认替换所有数据
			if (!handleResult(mode, resultList))
				if (mode == RefreshMode.reset)
					setAdapterItems(new ArrayList<T>());

			// append数据
			if (mode == RefreshMode.reset || mode == RefreshMode.refresh)
				addItemsAtFront(resultList);
			else if (mode == RefreshMode.update)
				addItems(resultList);
			
			notifyDataSetChanged();

			// 处理分页数据
			if (mPagingProxy != null) {
				if (getAdapterItems() != null && getAdapterItems().size() != 0)
					mPagingProxy.processData(result, getAdapterItems().get(0),
							getAdapterItems().get(getAdapterItems().size() - 1));
				else
					mPagingProxy.processData(result, null, null);
			}

			// 如果是缓存数据，且已经过期
			if (getTaskCount(getTaskId()) == 1 && result instanceof IResult) {
				// 这里增加一个自动刷新设置功能
				if (SettingUtility.getPermanentSettingAsBool("pAutoRefresh", true)) {
					IResult iResult = (IResult) result;
					// 数据是缓存数据，显示缓存且同时刷新数据
					if (iResult.isCache() && iResult.expired()) {
						requestDataDelay(700);
					}
					// 缓存数据没有过期，滚动到最后阅读位置
					else if (iResult.isCache()) {
						toLastReadPosition();
					}
				}
				else {
					toLastReadPosition();
				}
			}
			
			// 如果是重置数据，重置canLoadMore
			if (mode == RefreshMode.reset)
				refreshConfig.canLoadMore = true;
			boolean oldCanLoadMore = refreshConfig.canLoadMore;
			
			// 没有更多数据了
			if (result instanceof IResult && mode != RefreshMode.refresh) {
				IResult iResult = (IResult) result;
				if (iResult.noMore()) {
					refreshConfig.canLoadMore = false;
				}
				// 如果数据少于这个值，默认加载完了
				else {
					refreshConfig.canLoadMore = resultList.size() >= refreshConfig.minResultSize;
				}
			}
			
			// 状态发生改变，重置刷新控件
			if (oldCanLoadMore != refreshConfig.canLoadMore) {
				resetRefreshView(refreshConfig);
			}
			
			super.onSuccess(result);
		}
		
		@Override
		protected void onFinished() {
			super.onFinished();
			
			if (isRefreshing())
				setRefreshViewComplete();
			
			pagingTask = null;
		}

		/**
		 * 每次调用接口，获取新的数据时调用这个方法
		 * 
		 * @param mode
		 *            当次拉取数据的类型
		 * @param datas
		 *            当次拉取的数据
		 * @return <tt>false</tt> 如果mode={@link RefreshMode#reset}
		 *         默认清空adapter中的数据
		 */
		protected boolean handleResult(RefreshMode mode, List<T> datas) {
			return false;
		}

		/**
		 * 将Ts转换成List(T)
		 * 
		 * @param result
		 *            List(T)
		 * @return
		 */
		abstract protected List<T> parseResult(Result result);

		/**
		 * 异步执行方法，传入一个由
		 * {@link PagingProxy#generateHttpParams(RefreshMode)}构造的参数列表<br/>
		 * 子类可在{@link #onPreParams(HttpParams)}中添加额外的参数
		 * 
		 * @param params
		 * @return
		 * @throws AisenException
		 */
		abstract protected Result workInBackground(RefreshMode mode, String previousPage, String nextPage, Params... params) throws TaskException;

	}
	
	public void requestDataDelay(int delay) {
		mHandler.removeCallbacks(refreshDelay);
		mHandler.postDelayed(refreshDelay, delay);
	}
	
	Runnable refreshDelay = new Runnable() {
		
		@Override
		public void run() {
			if (getRefreshView() instanceof ListView)
				((ListView) getRefreshView()).setSelectionFromTop(0, 0);
			
			putLastReadPosition(0);
			putLastReadTop(0);
			// 如果没有正在刷新，设置刷新控件，且子类没有自动刷新
			if (!isRefreshing() && !setRefreshing())
				requestData(RefreshMode.reset);
		}
		
	};

	class MyBaseAdapter extends ABaseAdapter<T> {

		public MyBaseAdapter(ArrayList<T> datas, Activity context) {
			super(datas, context);
		}

		@Override
		protected AbstractItemView<T> newItemView() {
			return ARefreshFragment.this.newItemView();
		}

	}
	
	@Override
	public void onMovedToScrapHeap(View view) {
		Logger.v(ARefreshFragment.class.getSimpleName(), "onMovedToScrapHeap");
		
		releaseView(view);
	}

	/**
	 * 
	 * @param view
	 * @return true:子类自行释放，父类不做处理
	 */
	protected boolean releaseView(View view) {
		if (recyleImageViewRes() != null) {
			for (int imgId : recyleImageViewRes()) {
				ImageView imgView = (ImageView) view.findViewById(imgId);
				if (imgView != null) {
					imgView.setImageDrawable(BitmapLoader.getLoadingDrawable(imgView));
				
					Logger.v(ARefreshFragment.class.getSimpleName(), "释放ImageView");
				}
			}
		}
		
		return false;
	}

	protected int[] recyleImageViewRes() {
		return null;
	}

	@Override
	public void onStop() {
		super.onStop();

		releaseBitmap();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		ignoreScroll = ActivityHelper.getInstance().getBooleanShareData("com.m.IGNORE_SCROLL", false);
		
		refreshUI();
	}

	public void refreshUI() {
		notifyDataSetChanged();

		_refreshUI();
	}

	void _refreshUI() {

	}
	
	public void releaseBitmap() {
		if (getRefreshView() != null) {
			int childSize = getRefreshView().getChildCount();
			for (int i = 0; i < childSize; i++) {
				releaseView(getRefreshView().getChildAt(i));
			}
		}
	}
	
	/**
	 * 设置分页
	 * 
	 * @return <tt>null</tt> 不分页
	 */
	protected IPaging<T, Ts> configPaging() {
		return null;
	}
	
	/**
	 * 返回为null，不记录阅读状态
	 * 
	 * @return
	 */
	public String getLastReadKey() {
		return null;
	}
	
	protected void toLastReadPosition() {
		if (getRefreshView() == null)
			return;
		
		getRefreshView().post(new Runnable() {
			
			@Override
			public void run() {
				if (getRefreshView() instanceof ListView) 
					((ListView) getRefreshView()).setSelectionFromTop(getLastReadPosition(), getLastReadTop());
			}
		});
	}
	
	protected int getLastReadPosition() {
		return ActivityHelper.getInstance().getIntShareData(getLastReadKey() + "Position", 0);
	}
	
	protected void putLastReadPosition(int position) {
		if (refreshConfig.savePosition)
			ActivityHelper.getInstance().putIntShareData(getLastReadKey() + "Position", position);
	}
	
	private int getLastReadTop() {
		return ActivityHelper.getInstance().getIntShareData(getLastReadKey() + "Top", 0);
	}
	
	protected void putLastReadTop(int top) {
		if (refreshConfig.savePosition)
			ActivityHelper.getInstance().putIntShareData(getLastReadKey() + "Top", top);
	}
	
	@Override
	public boolean onAcUnusedDoubleClicked() {
		// 置顶
		if (getRefreshView() != null && getRefreshView() instanceof ListView)
			((ListView) getRefreshView()).setSelectionFromTop(0, 0);
		
		// 刷新
		if (!isRefreshing() &&
				SettingUtility.getPermanentSettingAsBool("com.m.ON_DOUBLE_CLICK_AC_TO_REFRESH", SettingUtility.getBooleanSetting("double_click_refresh")))
			requestDataDelay(200);
		
		return true;
	}
	
	public void notifyDataSetChanged() {
		if (swingAnimAdapter != null)
			swingAnimAdapter.notifyDataSetChanged();
		else
			mAdapter.notifyDataSetChanged();
	}
	
	public ArrayList<T> getAdapterItems() {
		return mAdapter.getDatas();
	}
	
	public int getAdapterCount() {
		if (swingAnimAdapter != null)
			return swingAnimAdapter.getCount();
		
		return mAdapter.getCount();
	}
	
	public void setAdapterSelected(int position) {
		mAdapter.setSelected(position);
	}
	
	public void setAdapterItems(ArrayList<T> items) {
		mAdapter.setDatas(items);
	}
	
	public void addItemsAtFront(List<T> items) {
		mAdapter.addItemsAtFront(items);
	}
	
	public void addItems(List<T> items) {
		mAdapter.addItems(items);
	}
	
	/**
	 * Adapter的ItemView
	 * 
	 * @return
	 */
	abstract protected AbstractItemView<T> newItemView();

	/**
	 * 根据RefreshMode拉取数据
	 * 
	 * @param mode
	 */
	abstract protected void requestData(RefreshMode mode);
	
	/**
	 * 列表控件
	 * 
	 * @return
	 */
	abstract public AbsListView getRefreshView();
	
	/**
	 * 设置列表控件状态为刷新状态
	 * 
	 * @return true:子类回调了刷新事件
	 */
	abstract public boolean setRefreshing();
	
	/**
	 * 设置列表FooterView显示Loading状态
	 * 
	 * @return
	 */
	public boolean setFooterRefreshing() {
		return false;
	}
	
	/**
	 * 设置列表控件没有更多了
	 */
	abstract public void resetRefreshView(RefreshConfig config);
	
	/**
	 * 设置列表控件状态为刷新结束
	 */
	abstract public void setRefreshViewComplete();
	
	public boolean isRefreshing() {
		return pagingTask != null;
	}
	
	public static class RefreshConfig implements Serializable {
		
		private static final long serialVersionUID = -963125420415611042L;

		public boolean canLoadMore = true;// 是否可以加载更多
		
		public boolean soundPlay = false;// 控件操作是否播放声音
		
		public boolean savePosition = false;// 是否保存最后阅读位置
		
		public String faildLabel;// 加载失败显示文本
		
		public String emptyLabel;// 加载空显示文本
		
		public int minResultSize = 10;// 当加载的数据少于这个值时，默认没有更多加载
		
		public boolean animEnable = true;// 是否启用加载动画
		
	}
	
}
