package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.m.R;
import com.m.support.Inject.ViewInject;

public abstract class AGridFragment<T extends Serializable, Ts extends Serializable> extends ARefreshFragment<T, Ts, GridView> {

	@ViewInject(idStr = "gridview")
	private GridView gridView;

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		super._layoutInit(inflater, savedInstanceState);

		gridView.setRecyclerListener(this);
		gridView.setAdapter(getAdapter());
	}

	@Override
	public AbsListView getRefreshView() {
		return gridView;
	}
	
	protected void setItems(ArrayList<T> items) {
		setViewVisiable(loadingLayout, View.GONE);
		setViewVisiable(emptyLayout, View.GONE);
		setViewVisiable(loadFailureLayout, View.GONE);
		setViewVisiable(contentLayout, View.VISIBLE);
		setAdapterItems(items);
		notifyDataSetChanged();
	}

	@Override
	protected int inflateContentView() {
		return R.layout.layout_gridview;
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
