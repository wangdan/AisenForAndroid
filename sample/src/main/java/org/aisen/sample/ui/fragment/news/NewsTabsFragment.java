package org.aisen.sample.ui.fragment.news;

import android.app.Fragment;

import org.aisen.android.support.bean.TabItem;
import org.aisen.android.ui.fragment.ATabsTabLayoutFragment;
import org.aisen.huaban.R;
import org.aisen.sample.ui.fragment.huaban.HuabanFragment;

import java.util.ArrayList;

/**
 * 新闻的TabLayout
 *
 * Created by wangdan on 15/12/22.
 */
public class NewsTabsFragment extends ATabsTabLayoutFragment<TabItem> {

    public static NewsTabsFragment newInstance() {
        return new NewsTabsFragment();
    }

    @Override
    protected ArrayList<TabItem> generateTabs() {
        ArrayList<TabItem> tabs = new ArrayList<>();

        String[] tabsArr = getResources().getStringArray(R.array.news_tabs);
        for (int i = 0; i < tabsArr.length; i++) {
            tabs.add(new TabItem(String.valueOf(i), tabsArr[i]));
        }

        return tabs;
    }

    @Override
    protected Fragment newFragment(TabItem bean) {
        return HuabanFragment.newInstance(HuabanFragment.Category.beauty);
    }

}
