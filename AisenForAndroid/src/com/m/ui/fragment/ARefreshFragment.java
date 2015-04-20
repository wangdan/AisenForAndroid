package com.m.ui.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.m.R;
import com.m.common.setting.SettingUtility;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.common.utils.ViewUtils;
import com.m.component.bitmaploader.BitmapLoader;
import com.m.network.biz.IResult;
import com.m.network.task.TaskException;
import com.m.support.adapter.ABaseAdapter;
import com.m.support.paging.IPaging;
import com.m.support.paging.PagingProxy;
import com.m.ui.widget.AsToolbar;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ARefreshFragment<T extends Serializable, Ts extends Serializable, V extends View> extends ABaseFragment
									implements RecyclerListener, OnScrollListener, AsToolbar.OnToolbarDoubleClick, AdapterView.OnItemClickListener {

	private static final String TAG = "ARefresh";
	
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
	}
	
	Handler mHandler = new Handler() {
		
	};
	
	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super._layoutInit(inflater, savedInstanceSate);
		
		if (getRefreshView() != null) {
			getRefreshView().setOnScrollListener(this);
			getRefreshView().setRecyclerListener(this);
            getRefreshView().setOnItemClickListener(this);
		}

        refreshConfig = savedInstanceSate == null ? new RefreshConfig()
                                                  : (RefreshConfig) savedInstanceSate.getSerializable("refreshConfig");

        config(refreshConfig);

        if (refreshConfig.animEnable) {
            swingAnimAdapter = new SwingBottomInAnimationAdapter(mAdapter) {

                @Override
                protected Animator getAnimator(ViewGroup parent, View view) {
                    return super.getAnimator(parent, view);
                }

            };
            swingAnimAdapter.setAbsListView(getRefreshView());
        }
		
		if (!refreshConfig.canLoadMore)
			resetRefreshView(refreshConfig);
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
			mHandler.removeCallbacks(refreshRunnable);
			
			if (scrollState == SCROLL_STATE_FLING) {
				isScrolling = true;
			}
			else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				isScrolling = true;
			}
			else if (scrollState == SCROLL_STATE_IDLE) {
				isScrolling = false;
//				mHandler.postDelayed(refreshRunnable, 400);
				notifyDataSetChanged();

				if (!TextUtils.isEmpty(refreshConfig.saveLastPositionKey) && getRefreshView() != null) {
					putLastReadPosition(getRefreshView().getFirstVisiblePosition());
					
					putLastReadTop(getRefreshView().getChildAt(0).getTop());
				}
			}
		}
	}
	
	Runnable refreshRunnable = new Runnable() {
		
		@Override
		public void run() {
			Logger.w(TAG, "刷新视图");
			notifyDataSetChanged();
		}
	};
	
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

	public BaseAdapter getAdapter() {
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
		if (!isRefreshing())
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
    public abstract class PagingTask<Params, Progress, Result extends Serializable> extends ABaseTask<Params, Progress, Result> {

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

            if (getRefreshView().getAdapter() == null)
                getRefreshView().setAdapter(getAdapter());
			
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
			
            // 如果数据少于这个值，默认加载完了
            refreshConfig.canLoadMore = resultList.size() >= refreshConfig.minResultSize;
            // 没有更多数据了
			if (refreshConfig.canLoadMore && result instanceof IResult && mode != RefreshMode.refresh) {
				IResult iResult = (IResult) result;
				if (iResult.noMore()) {
					refreshConfig.canLoadMore = false;
				}
			}
			
			// 状态发生改变，重置刷新控件
			if (oldCanLoadMore != refreshConfig.canLoadMore || mode == RefreshMode.reset) {
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
		 * @return <tt>false</tt> 如果mode={@link com.m.ui.fragment.ARefreshFragment.RefreshMode#reset}
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
         * 异步执行方法
         *
         * @param mode 刷新模式
         * @param previousPage 上一页页码
         * @param nextPage 下一页页码
         * @param params task参数
         * @return
         * @throws TaskException
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
			if (getRefreshView() instanceof ListView) {
				ListView listView = (ListView) getRefreshView();
				listView.setSelectionFromTop(0, 0);
			}
			
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
		
//		releaseView(view);
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
		
		mHandler.removeCallbacks(releaseRunnable);

		releaseBitmap();
	}
	
	@Override
    public void onResume() {
		super.onResume();

		ignoreScroll = ActivityHelper.getBooleanShareData("com.m.IGNORE_SCROLL", false);
		
		mHandler.removeCallbacks(releaseRunnable);
		
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
	
	protected void toLastReadPosition() {
		if (getRefreshView() == null)
			return;
		
		getRefreshView().post(new Runnable() {
			
			@Override
			public void run() {
				if (getRefreshView() instanceof ListView) {
					ListView listView = (ListView) getRefreshView();
					listView.setSelectionFromTop(getLastReadPosition(), getLastReadTop() + listView.getPaddingTop());
				}
			}
		});
	}
	
	protected int getLastReadPosition() {
		return ActivityHelper.getIntShareData(refreshConfig.saveLastPositionKey + "Position", 0);
	}
	
	protected void putLastReadPosition(int position) {
		if (!TextUtils.isEmpty(refreshConfig.saveLastPositionKey))
			ActivityHelper.putIntShareData(refreshConfig.saveLastPositionKey + "Position", position);
	}
	
	private int getLastReadTop() {
		return ActivityHelper.getIntShareData(refreshConfig.saveLastPositionKey + "Top", 0);
	}
	
	protected void putLastReadTop(int top) {
		if (!TextUtils.isEmpty(refreshConfig.saveLastPositionKey))
			ActivityHelper.putIntShareData(refreshConfig.saveLastPositionKey + "Top", top);
	}
	
	public void notifyDataSetChanged() {
		if (swingAnimAdapter != null && refreshConfig.animEnable) {
			// 刷新的时候，不显示动画
			boolean anim = refreshConfig.animEnable;
			refreshConfig.animEnable = false;
			swingAnimAdapter.notifyDataSetChanged();
			refreshConfig.animEnable = anim;
		}
		else {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public ArrayList<T> getAdapterItems() {
		return mAdapter.getDatas();
	}
	
	private int getAdapterCount() {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
	 * Adapter的ItemView
	 * 
	 * @return
	 */
	abstract protected ABaseAdapter.AbstractItemView<T> newItemView();

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
	
	@Override
	public void onPause() {
		super.onPause();
		
		mHandler.postDelayed(releaseRunnable, delayRlease());
	}
	
	protected int delayRlease() {
		return 15 * 1000;
	}
	
	Runnable releaseRunnable = new Runnable() {
		
		@Override
		public void run() {
			releaseBitmap();
			Logger.w("释放图片");
		}
		
	};

    @Override
    public boolean onToolbarDoubleClick() {
        return false;
    }

    @Override
    protected void taskStateChanged(ABaseTaskState state, Serializable tag) {
        super.taskStateChanged(state, tag);

        if (state == ABaseTaskState.success) {
            if (isContentEmpty()) {
                if (emptyLayout != null && !TextUtils.isEmpty(refreshConfig.emptyLabel))
                    ViewUtils.setTextViewValue(emptyLayout, R.id.txtLoadEmpty, refreshConfig.emptyLabel);
            }
        }
        else if (state == ABaseTaskState.falid) {
            if (isContentEmpty()) {
                if (loadFailureLayout != null && !TextUtils.isEmpty(tag + ""))
                    ViewUtils.setTextViewValue(loadFailureLayout, R.id.txtLoadFailed, tag + "");
            }
        }
    }

    public static class RefreshConfig implements Serializable {

        public static final long serialVersionUID = -963125420415611042L;

		public boolean canLoadMore = true;// 是否可以加载更多
		
		public String saveLastPositionKey = null;// 最后阅读坐标的Key，null-不保存，针对缓存数据有效
		
		public int minResultSize = 10;// 当加载的数据少于这个值时，默认没有更多加载
		
		public boolean animEnable = false;// 是否启用加载动画
		
		public String emptyLabel;// 加载空显示文本
		
	}
	
}
