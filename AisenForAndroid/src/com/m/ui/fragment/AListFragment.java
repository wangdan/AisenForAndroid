package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.m.R;
import com.m.support.Inject.ViewInject;

public abstract class AListFragment<T extends Serializable, Ts extends Serializable> extends ARefreshFragment<T, Ts, ListView> {

	@ViewInject(idStr = "listView")
	private ListView listView;

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		super._layoutInit(inflater, savedInstanceState);

		View headView = addHeadView();
		if (headView != null)
			listView.addHeaderView(headView);

		View footerView = addFooterView();
		if (footerView != null)
			listView.addFooterView(footerView);

		listView.setRecyclerListener(this);
		listView.setAdapter(getAdapter());
//		listView.setEmptyView(_getEmptyView());
	}
	
	@Override
	public AbsListView getRefreshView() {
		return listView;
	}

	public View addHeadView() {
		return null;
	}

	public View addFooterView() {
		return null;
	}

	@Override
	protected int inflateContentView() {
		return R.layout.layout_listview;
	}

	protected ListView getListView() {
		return listView;
	}

	protected void setItems(ArrayList<T> items) {
		setViewVisiable(loadingLayout, View.GONE);
		setViewVisiable(emptyLayout, View.GONE);
		setViewVisiable(loadFailureLayout, View.GONE);
		setViewVisiable(contentLayout, View.VISIBLE);
		getAdapter().setDatasAndRefresh(items);
		getListView().setSelectionFromTop(0, 0);
	}

	@Override
	public boolean onAcUnusedDoubleClicked() {
		getListView().setSelectionFromTop(0, 0);
		
		return true;
	}
	
	@Override
	public boolean setRefreshing() {
		return false;
	}

	@Override
	public void setRefreshViewComplete() {
	}

	@Override
	public void resetRefreshView(RefreshConfig config) {
	}

}
