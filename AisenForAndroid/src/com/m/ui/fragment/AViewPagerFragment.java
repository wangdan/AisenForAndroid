package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.android.loader.BitmapLoader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.m.R;
import com.m.support.Inject.ViewInject;
import com.m.support.adapter.ABaseAdapter;
import com.m.support.adapter.ABaseAdapter.AbstractItemView;

public abstract class AViewPagerFragment<T extends Serializable, Ts extends Serializable> extends ARefreshFragment<T, Ts, View> {

	@ViewInject(idStr = "viewPager")
	private ViewPager viewPager;
	private ABasePagerAdapter pagerAdapter;
	@ViewInject(idStr = "dotLayout")
	private ViewGroup dotLayout;

	@Override
	protected int inflateContentView() {
		return R.layout.layout_viewpager;
	}

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		super._layoutInit(inflater, savedInstanceSate);

		List<T> datas = savedInstanceSate != null ? (List<T>) savedInstanceSate.getSerializable("datas") : new ArrayList<T>();
		int index = savedInstanceSate != null ? savedInstanceSate.getInt("", -1) : -1;

		pagerAdapter = new ABasePagerAdapter(getActivity(), datas);
//		viewPager.setTransitionEffect(TransitionEffect.FlipHorizontal);
//		viewPager.setPageMargin(0);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setSeletecdDotImage(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		if (index != -1)
			viewPager.setCurrentItem(index);
	}
	
	@Override
	public AbsListView getRefreshView() {
		return null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (pagerAdapter != null) {
			outState.putSerializable("datas", (Serializable) pagerAdapter.getDatas());
			outState.putInt("currentIndex", getViewPager().getCurrentItem());
		}
	}

	@Override
	void _refreshUI() {
		getViewPagerAdapter().notifyDataSetChanged();
	}

	/**
	 * 
	 * @return ViewPage使用的Adapter
	 */
	public ABasePagerAdapter getViewPagerAdapter() {
		return pagerAdapter;
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	@Override
	public boolean isContentEmpty() {
		return getViewPagerAdapter() != null && getViewPagerAdapter().getCount() > 0;
	}

	/**
	 * 请使用{@link #getViewPagerAdapter}
	 */
	@Override
	@Deprecated
	public ABaseAdapter<T> getAdapter() {
		return null;
	}

	private void generateDotView() {

		if (dotLayout == null || getViewPagerAdapter().getDatas() == null)
			return;

		if (getViewPagerAdapter().getCount() <= 1) {
			dotLayout.setVisibility(View.GONE);
			return;
		} else {
			dotLayout.setVisibility(View.VISIBLE);
		}

		int dotCount = getViewPagerAdapter().getDatas().size();
		dotLayout.removeAllViews();
		for (int i = 0; i < dotCount; i++) {
			if (getActivity() == null)
				break;

			View dotView = setDotView();
			dotLayout.addView(dotView, new ViewGroup.LayoutParams(15, 15));
			dotView.setBackgroundResource(setDotDrawable());
		}

		setSeletecdDotImage(0);
	}

	protected void setSeletecdDotImage(int index) {
		if (getViewPagerAdapter().getDatas().size() <= 1)
			return;

		if (getViewPagerAdapter().getDatas().size() > 0)
			index = index % getViewPagerAdapter().getDatas().size();

		if (dotLayout == null || dotLayout.getChildCount() == 0 || index >= getViewPagerAdapter().getDatas().size())
			return;

		for (int i = 0; i < dotLayout.getChildCount(); i++)
			dotLayout.getChildAt(i).setSelected(index == i);

	}

	protected int setDotDrawable() {
		return R.drawable.selector_dot;
	}

	protected View setDotView() {
		return new ImageView(getActivity());
	}

	protected abstract class ViewPagerTask<Params, Progress, Result extends Serializable> extends PagingTask<Params, Progress, Result> {

		public ViewPagerTask(String taskId, RefreshMode mode) {
			super(taskId, mode);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onSuccess(Result result) {
			List<T> resultList;
			if (result instanceof List)
				resultList = (List<T>) result;
			else
				resultList = parseResult(result);

			if (getViewPagerAdapter() != null) {
				// 如果子类没有处理新获取的数据刷新UI，默认替换所有数据
				if (!handleResult(mode, resultList))
					if (mode == RefreshMode.reset)
						getViewPagerAdapter().setDataAndRefresh(new ArrayList<T>());
				
				if (resultList.size() != 0) {
					generateDotView();
				}
				
				if (mode == RefreshMode.reset || mode == RefreshMode.refresh)
					getViewPagerAdapter().addItemsAtFrontAndRefresh(resultList);
				else if (mode == RefreshMode.update)
					getViewPagerAdapter().addItemsAndRefresh(resultList);

				if (mPagingProxy != null) {
					if (getViewPagerAdapter() != null && getViewPagerAdapter().getDatas().size() != 0)
						mPagingProxy.processData(result, getViewPagerAdapter().getDatas().get(0),
								getViewPagerAdapter().getDatas().get(getViewPagerAdapter().getCount() - 1));
					else
						mPagingProxy.processData(result, null, null);
				}
				
			}

			super.onSuccess(result);
		}

		@Override
		protected boolean handleResult(RefreshMode mode, List<T> datas) {
			if (true) return false;
			
			boolean isRepeat = pagerAdapter.isRepeat;
			pagerAdapter = new ABasePagerAdapter(getActivity(), datas);
			pagerAdapter.setRepeat(isRepeat);
			viewPager.setAdapter(pagerAdapter);

			if (pagerAdapter.isRepeat())
				viewPager.setCurrentItem(datas.size() * 1000);
			return true;
		}

	}

	public class ABasePagerAdapter extends PagerAdapter {

		private Context mContext;
		private Queue<View> unUsedViewStorer;
		private List<T> datas;
		private boolean isRepeat;

		public ABasePagerAdapter(Context context, List<T> datas) {
			if (datas == null)
				datas = new ArrayList<T>();
			this.mContext = context;
			this.datas = datas;
			unUsedViewStorer = new LinkedList<View>();
		}

		public void setRepeat(boolean isRepeat) {
			this.isRepeat = isRepeat;
		}

		public boolean isRepeat() {
			return isRepeat;
		}
		
		public void addItemsAtFront(List<T> entries) {
	        for (int i = entries.size() - 1; i >= 0; i--) {
	            datas.add(0, entries.get(i));
	        }
	    }
		
		public void addItemsAtFrontAndRefresh(List<T> entries) {
	        addItemsAtFront(entries);
	        notifyDataSetChanged();
	    }
		
		public void addItems(List<T> entries) {
	        for (T entry : entries)
	            datas.add(entry);
	    }

	    public void addItemsAndRefresh(List<T> entries) {
	        addItems(entries);
	        notifyDataSetChanged();
	    }

		public void setDataAndRefresh(List<T> datas) {
			this.datas = datas;
			notifyDataSetChanged();
		}

		public void setDatas(List<T> datas) {
			this.datas = datas;
		}

		public List<T> getDatas() {
			return datas;
		}

		@Override
		public int getCount() {
			if (datas.size() < 3)
				return datas.size();

			return isRepeat ? Integer.MAX_VALUE : datas.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (isRepeat && datas.size() != 0)
				position = position % datas.size();

			AbstractItemView<T> viewProcessor = null;

			View childView = unUsedViewStorer.poll();
			if (childView == null) {
				viewProcessor = newItemView();
				childView = View.inflate(mContext, viewProcessor.inflateViewId(), null);
				childView.setTag(viewProcessor);
				viewProcessor.bindingView(childView);

			} else {
				viewProcessor = (AbstractItemView) childView.getTag();
			}

			childView.setId(position);
			viewProcessor.setPosition(position);

			viewProcessor.bindingData(childView, datas.get(position));

			// XXX 暂时不支持选中项
			viewProcessor.updateConvertView(datas.get(position), childView, -1);

			container.addView(childView);

//			viewPager.setObjectForPosition(childView, position);

			return childView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (isRepeat)
				position = position % datas.size();

			View childView = container.findViewById(position);

			int[] ids = recyleImageViewRes();
			if (ids != null) {
				for (int id : ids) {
					ImageView imgView = (ImageView) childView.findViewById(id);
					if (imgView != null)
						imgView.setImageDrawable(BitmapLoader.getLoadingDrawable(imgView));
				}
			}

			unUsedViewStorer.add(childView);
			AbstractItemView viewProcessor = (AbstractItemView) childView.getTag();
			viewProcessor.recycleView(childView);
			container.removeView(childView);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}

}
