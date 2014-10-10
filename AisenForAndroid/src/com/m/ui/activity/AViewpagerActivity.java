package com.m.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.m.R;
import com.m.support.adapter.FragmentPagerAdapter;
import com.m.ui.fragment.ABaseFragment;

public abstract class AViewpagerActivity extends BaseActivity implements OnPageChangeListener {

	List<ABaseFragment> fragments;

	private int currentIndex = 0;

	private String[] pagerTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_viewpagers);
		
		pagerTitle = getResources().getStringArray(setViewPagerTitles());

		fragments = new ArrayList<ABaseFragment>();
		
		if (savedInstanceState == null) {
			currentIndex = defaultSelectedIndex();
			setViewPagerFragments(fragments);
		} else {
			currentIndex = savedInstanceState.getInt("index", currentIndex);

			for (String title : pagerTitle) {
				ABaseFragment fragment = (ABaseFragment) getFragmentManager().findFragmentByTag(title);
				if (fragment != null)
					fragments.add(fragment);
			}
		}

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOnPageChangeListener(this);
		viewPager.setAdapter(new MyViewPagerAdapter(getFragmentManager()));
//		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
//		if (showIndicator()) {
//			indicator.setViewPager(viewPager);
//			indicator.setOnPageChangeListener(this);
//		}
//		else {
//			viewPager.setOnPageChangeListener(this);
//			indicator.setVisibility(View.GONE);
//		}
		viewPager.setCurrentItem(currentIndex);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", currentIndex);
	}

	class MyViewPagerAdapter extends FragmentPagerAdapter {

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return pagerTitle[position];
		}

		@Override
		protected String makeFragmentName(int position) {
			return pagerTitle[position];
		}

	}
	
	protected int defaultSelectedIndex() {
		return 0;
	}
	
	protected boolean showIndicator() {
		return true;
	}

	protected abstract int setViewPagerTitles();
	
	protected abstract void setViewPagerFragments(List<ABaseFragment> fragmentList);

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		currentIndex = position;
	}
	
	protected ViewPager getViewPager() {
		return (ViewPager) findViewById(R.id.viewPager);
	}
	
	protected List<ABaseFragment> getFragments() {
		return fragments;
	}
	
	public ABaseFragment getCurrentFragment() {
		if (fragments.size() < currentIndex)
			return null;
		
		return fragments.get(currentIndex);
	}

}
