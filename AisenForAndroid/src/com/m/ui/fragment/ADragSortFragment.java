package com.m.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.m.R;
import com.m.support.Inject.ViewInject;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;

/**
 * 可拖拽排序的ListView
 * 
 * @author wangdan
 * 
 * @param <T>
 * @param <Ts>
 */
public abstract class ADragSortFragment<T extends Serializable, Ts extends Serializable> extends ARefreshFragment<T, Ts, DragSortListView> {

	@ViewInject(idStr = "dragsortListview")
	private DragSortListView dragSortListView;

	private DragSortController controller;

	@Override
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		super._layoutInit(inflater, savedInstanceState);

		controller = buildController(dragSortListView);
		dragSortListView.setFloatViewManager(controller);
		dragSortListView.setOnTouchListener(controller);
		dragSortListView.setDragEnabled(true);

		dragSortListView.setRecyclerListener(this);
		dragSortListView.setAdapter(getAdapter());
	}
	
	@Override
	public AbsListView getRefreshView() {
		return dragSortListView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		dragSortListView.setDropListener(onDropListener);
	}

	@Override
	protected int inflateContentView() {
		return R.layout.layout_dragsort_listview;
	}

	protected DragSortListView getListView() {
		return dragSortListView;
	}

	protected void setItems(ArrayList<T> items) {
		setViewVisiable(loadingLayout, View.GONE);
		setViewVisiable(emptyLayout, View.GONE);
		setViewVisiable(loadFailureLayout, View.GONE);
		setViewVisiable(contentLayout, View.VISIBLE);
		setAdapterItems(items);
		notifyDataSetChanged();
		getListView().setSelectionFromTop(0, 0);
	}

	protected DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.drag_handle);
		controller.setClickRemoveId(R.id.click_remove);
		controller.setRemoveEnabled(false);
		controller.setSortEnabled(true);
		controller.setDragInitMode(DragSortController.ON_DOWN);
		controller.setRemoveMode(DragSortController.FLING_REMOVE);
		return controller;
	}

	/**
	 * 准备移动item
	 * 
	 * @param from
	 * @param to
	 * @return false:置换数据
	 */
	protected boolean onItemDrop(int from, int to) {
		return false;
	}

	/**
	 * 移动item，默认替换adapter位置
	 * 
	 * @param from
	 * @param to
	 */
	protected void onItemDroped(int from, int to) {
		T moveItemT = (T) getAdapter().getItem(from);
		getABaseAdapter().removeItem(from);
		getABaseAdapter().getDatas().add(to, moveItemT);
		notifyDataSetChanged();
	}

	private DropListener onDropListener = new DropListener() {

		@Override
		public void drop(int from, int to) {
			if (!onItemDrop(from, to)) {
				onItemDroped(from, to);
			}
		}
	};

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
