package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.android.loader.BitmapLoader;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.m.R;
import com.m.common.utils.ActivityHelper;
import com.m.common.utils.Logger;
import com.m.support.Inject.ViewInject;
import com.m.support.adapter.FragmentPagerAdapter;
import com.m.ui.activity.BaseActivity;
import com.m.ui.fragment.ATabTitlePagerFragment.TabTitlePagerBean;

public abstract class ATabTitlePagerFragment<T extends TabTitlePagerBean> extends ABaseFragment 
								implements OnPageChangeListener {// , IRefreshAfterLoadCache 

	static final String TAG = ATabTitlePagerFragment.class.getSimpleName();
	
	@ViewInject(idStr = "tabs")
	PagerSlidingTabStrip pagerTabs;
	@ViewInject(idStr = "pager")
	ViewPager viewPager;
	MyViewPagerAdapter mViewPagerAdapter;
	
	private ArrayList<T> mChanneList;
	private Map<String, ABaseFragment> fragments;
	private int selectedIndex = 0;
	private long lastChanneTime;
	
	private String lastPageName;
	
	@Override
	protected int inflateContentView() {
		return R.layout.ui_tabtitle_pager;
	}
	
	abstract protected ArrayList<T> getPageTitleBeans();
	
	abstract protected String setFragmentTitle();
	
	abstract protected ABaseFragment newFragment(T bean);
	
	abstract protected void replaceSelfInActivity();
	
	protected void onPageStart(String beanTitle) {
		
	}
	
	protected void onPageEnd(String beanTitle) {
		
	}
	
	protected void onEvent(String eventId, String tag, long time) {
	}
	
	@Override
	protected void layoutInit(LayoutInflater inflater, final Bundle savedInstanceSate) {
		super.layoutInit(inflater, savedInstanceSate);
		
		setHasOptionsMenu(true);
		
		setTab(savedInstanceSate);
	}
	
//	@Override
//	public boolean refreshAfterLoadCache() {
//		ABaseFragment fragment = getCurrentFragment();
//		// 如果正在刷新
//		if (fragment != null && fragment instanceof ARefreshFragment) {
//			ARefreshFragment refreshFragment = (ARefreshFragment) fragment;
//			if (refreshFragment.isRefreshing())
//				return true;
//		}
//		if (fragment != null && fragment instanceof IRefreshAfterLoadCache) {
//			Logger.w("ATab refreshAfterLoadCache , position = " + getViewPager().getCurrentItem());
//			return ((IRefreshAfterLoadCache) fragment).refreshAfterLoadCache();
//		}
//		else {
//			Logger.w("ATab refreshAfterLoadCache , fragment = null");
//		}
//		
//		return false;
//	}
	
	@SuppressWarnings("unchecked")
	protected void setTab(final Bundle savedInstanceSate) {
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (getActivity() == null)
					return;
				
				if (savedInstanceSate == null) {
					mChanneList = getPageTitleBeans();
					
					String lastReadType = ActivityHelper.getInstance().getShareData("PagerLastPosition" + setFragmentTitle(), null);
					selectedIndex = 0;
					if (getArguments() != null) {
						lastReadType = null;
						
						selectedIndex = getArguments().getInt("index", 0);
					}
						
					
					if (!TextUtils.isEmpty(lastReadType)) {
						for (int i = 0; i < mChanneList.size(); i++) {
							TabTitlePagerBean bean = mChanneList.get(i);
							if (lastReadType.equals(bean.getType())) {
								selectedIndex = i;
								break;
							}
						}
					}
					
				} else {
					mChanneList = (ArrayList<T>) savedInstanceSate.getSerializable("channes");
					selectedIndex = savedInstanceSate.getInt("selectedIndex");
				}
				
				fragments = new HashMap<String, ABaseFragment>();
				
				if (mChanneList == null)
					return;
				
				if (mChanneList.size() == 0) {
					findViewById(R.id.layoutEmpty).setVisibility(View.VISIBLE);
					findViewById(R.id.layoutContent).setVisibility(View.GONE);
				} else {
					for (int i = 0; i < mChanneList.size(); i++) {
						ABaseFragment fragment = (ABaseFragment) getActivity().getFragmentManager()
													.findFragmentByTag(mChanneList.get(i).getTitle() + setFragmentTitle());
						if (fragment != null)
							fragments.put(mChanneList.get(i).getTitle() + setFragmentTitle(), fragment);
					}
					
					mViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager());
//					viewPager.setOffscreenPageLimit(mViewPagerAdapter.getCount());
					viewPager.setOffscreenPageLimit(3);
					viewPager.setAdapter(mViewPagerAdapter);
					if (selectedIndex >= mViewPagerAdapter.getCount())
						selectedIndex = 0;
					viewPager.setCurrentItem(selectedIndex);
					pagerTabs.setViewPager(viewPager);
					if (mViewPagerAdapter.getCount() <= 3)
						pagerTabs.setShouldExpand(true);
					pagerTabs.setOnPageChangeListener(ATabTitlePagerFragment.this);
				
					lastChanneTime = System.currentTimeMillis();
				}
				
				if (mChanneList != null && TextUtils.isEmpty(lastPageName) && selectedIndex < mChanneList.size()) {
					lastPageName = mChanneList.get(selectedIndex).getTitle();
					onPageStart(lastPageName);
				}
				
//				mHandler.postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						if (getActivity() != null)
//						refreshAfterLoadCache();
//					}
//					
//				}, 250);
			}
			
		}, 270);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		try {
			ActivityHelper.getInstance().putShareData("PagerLastPosition" + setFragmentTitle(), 
					mChanneList.get(viewPager.getCurrentItem()).getType());
			
			destoryFragments();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PagerSlidingTabStrip getTabStrip() { 
		return pagerTabs;
	}
	
	private void destoryFragments() {
		if (getActivity() != null) {
			if (getActivity() instanceof BaseActivity) {
				BaseActivity mainActivity = (BaseActivity) getActivity();
				if (mainActivity.mIsDestoryed())
					return;
			}
			
			try {
				FragmentTransaction trs = getFragmentManager().beginTransaction();
				Set<String> keySet = fragments.keySet();
				for (String key : keySet) {
					if (fragments.get(key) != null)
						trs.remove(fragments.get(key));
				}
				trs.commit();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		selectedIndex = viewPager.getCurrentItem();
		outState.putSerializable("channes", mChanneList);
		outState.putInt("selectedIndex", selectedIndex);
	}
	
	class MyViewPagerAdapter extends FragmentPagerAdapter {

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			ABaseFragment fragment = fragments.get(makeFragmentName(position));
			if (fragment == null) {
				fragment = newFragment(mChanneList.get(position));
				
				fragments.put(makeFragmentName(position), fragment);
			}
			
			return fragment;
		}
		
		@Override
		protected void freshUI(Fragment fragment) {
		}
		
		@Override
		public int getCount() {
			return mChanneList.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return mChanneList.get(position).getTitle();
		}

		@Override
		protected String makeFragmentName(int position) {
			return mChanneList.get(position).getTitle() + setFragmentTitle();
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (mChanneList != null && TextUtils.isEmpty(lastPageName) && selectedIndex < mChanneList.size()) {
			lastPageName = mChanneList.get(selectedIndex).getTitle();
			onPageStart(lastPageName);
		}
		
		if (ActivityHelper.getInstance().getBooleanShareData("ChanneSortHasChanged", false) || 
				ActivityHelper.getInstance().getBooleanShareData("offlineChanneChanged", false)) {
			
			ActivityHelper.getInstance().putBooleanShareData("ChanneSortHasChanged", false);
			ActivityHelper.getInstance().putBooleanShareData("offlineChanneChanged", false);
			
			destoryFragments();
			
			replaceSelfInActivity();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (!TextUtils.isEmpty(lastPageName)) {
			onPageEnd(lastPageName);
			lastPageName = null;
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	
	@Override
	public void onPageSelected(int position) {
		
		if (!TextUtils.isEmpty(lastPageName))
			onPageEnd(lastPageName);
		
		lastPageName = mChanneList.get(position).getTitle();
		onPageStart(lastPageName);
		
		if (System.currentTimeMillis() - lastChanneTime > 30 * 1000)
			onEvent(mChanneList.get(selectedIndex).getType(), 
					mChanneList.get(selectedIndex).getTitle(), System.currentTimeMillis() - lastChanneTime);
		
		lastChanneTime = System.currentTimeMillis();

		selectedIndex = position;
		
//		refreshAfterLoadCache();
		
		// 释放资源
		mHandler.removeCallbacks(releaseFragmentRunnable);
		mHandler.postDelayed(releaseFragmentRunnable, 5 * 1000);
		// 刷新当前显示
		mHandler.removeCallbacks(refreshFragmentRunnable);
		mHandler.postDelayed(refreshFragmentRunnable, 1000);
	}
	
	Runnable refreshFragmentRunnable = new Runnable() {
		
		@Override
		public void run() {
			Fragment fragment = fragments.get(mViewPagerAdapter.makeFragmentName(selectedIndex));
			if (fragment != null) {
				Logger.w(String.format("刷新第%d个fragment的资源", selectedIndex));
				BitmapLoader.getInstance().clearCache();
				
				((ARefreshFragment<Serializable, Serializable, View>) fragment).refreshUI();
			}
		}
	};
	
	Runnable releaseFragmentRunnable = new Runnable() {
		
		@Override
		public void run() {
			Logger.w(String.format("准备释放第%d个fragment的资源", selectedIndex + 1));
			Logger.w(String.format("准备释放第%d个fragment的资源", selectedIndex - 1));
			releaseFragment(selectedIndex + 1);
			releaseFragment(selectedIndex - 1);
		}
	};
	
	public void releaseFragment(int position) {
		if (position < mChanneList.size() && position >= 0) {
			Fragment fragment = fragments.get(mViewPagerAdapter.makeFragmentName(position));
			if (fragment != null) {
				Logger.w(String.format("释放第%d个fragment的资源", position));
				
				((ARefreshFragment<Serializable, Serializable, View>) fragment).releaseBitmap();
			} 
			else {
				Logger.e(String.format("释放的第%d个fragment不存在", position));
			}
		}
	}
	
	Handler mHandler = new Handler() {
		
	};
	
	public ViewPager getViewPager() {
		return viewPager;
	}
	
	public FragmentPagerAdapter getViewPagerAdapter() {
		return mViewPagerAdapter;
	}
	
	public ABaseFragment getCurrentFragment() {
		if (mViewPagerAdapter.getCount() < selectedIndex)
			return null;
		
		ABaseFragment fragment = fragments.get(mViewPagerAdapter.makeFragmentName(selectedIndex));
		Logger.d(TAG, String.format("getCurrentFragment, position = %d, title = %s", selectedIndex, mViewPagerAdapter.makeFragmentName(selectedIndex)));
		return fragment;
	}
	
	public Fragment getFragment(String title) {
		if (fragments == null)
			return null;
		
		return fragments.get(title);
	}
	
	public Map<String, ABaseFragment> getFragments() {
		return fragments;
	}
	
//	@Override
//	public boolean onAcUnusedDoubleClicked() {
//		ABaseFragment fragment = getCurrentFragment();
//		if (fragment != null && fragment instanceof IAcUnusedDoubleClickedHandler) {
//			((IAcUnusedDoubleClickedHandler) fragment).onAcUnusedDoubleClicked();
//			
//			Logger.w(mViewPagerAdapter.makeFragmentName(selectedIndex) + "双击了");
//			
//			return true;
//		}
//		
//		return false;
//	}
	
	public static class TabTitlePagerBean implements Serializable {

		private static final long serialVersionUID = 3680682035685685311L;

		private String type;
		
		private String title;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
	}
	
}
