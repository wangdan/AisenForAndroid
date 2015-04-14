package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;

import com.m.R;
import com.m.support.adapter.FragmentPagerAdapter;
import com.m.support.inject.ViewInject;
import com.m.ui.activity.basic.BaseActivity;
import com.m.ui.widget.SlidingTabLayout;

/**
 * Created by wangdan on 15-1-20.
 */
public abstract class AStripTabsFragment<T extends AStripTabsFragment.StripTabItem> extends ABaseFragment
                                implements ViewPager.OnPageChangeListener {

    static final String TAG = AStripTabsFragment.class.getSimpleName();

    @ViewInject(idStr = "slidingTabs")
    SlidingTabLayout slidingTabs;
    @ViewInject(idStr = "pager")
    ViewPager viewPager;
    MyViewPagerAdapter mViewPagerAdapter;

    private ArrayList<T> mChanneList;
    private Map<String, Fragment> fragments;
    private int selectedIndex = 0;

    @Override
    protected int inflateContentView() {
        return R.layout.comm_ui_tabs;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        selectedIndex = viewPager.getCurrentItem();
        outState.putSerializable("channes", mChanneList);
        outState.putInt("selectedIndex", selectedIndex);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, final Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        setHasOptionsMenu(true);

        setTab(savedInstanceSate);
    }

    @SuppressWarnings("unchecked")
    protected void setTab(final Bundle savedInstanceSate) {
        if (getActivity() == null)
            return;

        if (savedInstanceSate == null) {
            mChanneList = generateTabs();

            selectedIndex = 0;
        } else {
            mChanneList = (ArrayList<T>) savedInstanceSate.getSerializable("channes");
            selectedIndex = savedInstanceSate.getInt("selectedIndex");
        }

        fragments = new HashMap<String, Fragment>();

        if (mChanneList == null)
            return;

        for (int i = 0; i < mChanneList.size(); i++) {
            Fragment fragment = (Fragment) getActivity().getFragmentManager()
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
//        pagerTabs.setViewPager(viewPager);
//        pagerTabs.setShouldExpand(true);
//        pagerTabs.setOnPageChangeListener(AStripTabsFragment.this);

        slidingTabs.setCustomTabView(R.layout.comm_lay_tab_indicator, android.R.id.text1);
        Resources res = getResources();
        slidingTabs.setSelectedIndicatorColors(res.getColor(R.color.comm_tab_selected_strip));
        slidingTabs.setDistributeEvenly(true);
        slidingTabs.setViewPager(viewPager);
        slidingTabs.setOnPageChangeListener(this);
    }

    protected void destoryFragments() {
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
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    abstract protected ArrayList<T> generateTabs();

    abstract protected String setFragmentTitle();

    abstract protected Fragment newFragment(T bean);

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            destoryFragments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class StripTabItem implements Serializable {

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

    class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(makeFragmentName(position));
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

}
