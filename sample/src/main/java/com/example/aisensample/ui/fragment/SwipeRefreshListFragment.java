package com.example.aisensample.ui.fragment;

import com.example.aisensample.support.bean.NeihanduanziBean;
import com.example.aisensample.support.bean.NeihanduanziBeans;
import org.aisen.android.support.adapter.ABaseAdapter;
import org.aisen.android.ui.fragment.ARefreshFragment;
import org.aisen.android.ui.fragment.ASwipeRefreshListFragment;

/**
 * Created by wangdan on 15/4/24.
 */
public class SwipeRefreshListFragment extends ASwipeRefreshListFragment<NeihanduanziBean, NeihanduanziBeans> {

    @Override
    protected ABaseAdapter.AbstractItemView<NeihanduanziBean> newItemView() {
        return null;
    }

    @Override
    protected void requestData(RefreshMode mode) {

    }

}
