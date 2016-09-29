package org.aisen.sample.ui.fragment;

import org.aisen.android.ui.fragment.ARecycleViewSwipeRefreshFragment;
import org.aisen.android.ui.fragment.itemview.IItemViewCreator;
import org.aisen.sample.support.bean.NeihanduanziBean;
import org.aisen.sample.support.bean.NeihanduanziBeans;

import java.io.Serializable;

/**
 * Created by wangdan on 15/4/24.
 */
public class SwipeRefreshListFragment extends ARecycleViewSwipeRefreshFragment<NeihanduanziBean, NeihanduanziBeans, Serializable> {


    @Override
    public IItemViewCreator<NeihanduanziBean> configItemViewCreator() {
        return null;
    }

    @Override
    public void requestData(RefreshMode mode) {

    }

}
