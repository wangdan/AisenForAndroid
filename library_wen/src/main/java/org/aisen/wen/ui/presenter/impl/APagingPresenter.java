package org.aisen.wen.ui.presenter.impl;

import android.os.Bundle;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.model.listener.IModelListener;
import org.aisen.wen.ui.model.IPagingModel;
import org.aisen.wen.ui.model.IPagingModelParams;
import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.model.listener.ModelListenerParam;
import org.aisen.wen.ui.model.listener.PagingModelListenerParam;
import org.aisen.wen.ui.presenter.IPagingPresenter;
import org.aisen.wen.ui.view.IPaingView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdan on 16/10/10.
 */
public abstract class APagingPresenter<Item extends Serializable,
                                       Result extends Serializable,
                                       ContentMode extends IPagingModel<Item, Result>,
                                       ContentView extends IPaingView>
                            extends AContentPresenter<Result, ContentMode, ContentView>
                            implements IPagingPresenter<Item, Result>, IModelListener<Result>, IPaingView.IPagingViewCallback {

    private static final String SAVED_PAGING = "org.aisen.android.ui.Paging";

    public enum RefreshMode {
        /**
         * 重设数据
         */
        reset,
        /**
         * 上拉，加载更多
         */
        update,
        /**
         * 下拉，刷新最新
         */
        refresh
    }

    private IPaging<Item, Result> mPaging;// 分页器

    public APagingPresenter(ContentMode contentMode, ContentView view) {
        super(contentMode, view);

        view.setPagingViewCallback(this);
    }

    @Override
    public void onBridgeCreate(Bundle savedInstanceState) {
        super.onBridgeCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getSerializable(SAVED_PAGING) != null) {
            mPaging = (IPaging) savedInstanceState.getSerializable(SAVED_PAGING);
        } else {
            mPaging = newPaging();
        }
    }

    @Override
    public void onBridgeSaveInstanceState(Bundle outState) {
        super.onBridgeSaveInstanceState(outState);

        // 将分页信息保存起来
        if (mPaging != null)
            outState.putSerializable(SAVED_PAGING, mPaging);
    }

    @Override
    public void onPullDownToRefresh() {
        requestData(RefreshMode.refresh);
    }

    @Override
    public void onPullUpToRefresh() {
        requestData(RefreshMode.update);
    }

    @Override
    final public void requestData() {
        // 如果没有Loading视图，且数据为空，就显示FootView加载状态
        RefreshMode mode = RefreshMode.reset;
        if (getView().getAdapter().getDatas().size() == 0 && getView().getLoadingLayout() == null)
            mode = RefreshMode.update;

        requestData(mode);
    }

    public void requestData(RefreshMode mode) {
        if (mode == RefreshMode.reset && mPaging != null)
            mPaging = newPaging();

        getMode().execute(mode, mPaging);
    }

    @Override
    public void requestDataOutofdate() {
        getView().putLastReadPosition(0);
        getView().putLastReadTop(0);

        requestDataSetRefreshing();
    }

    /**
     * 设置刷新控件为刷新状态且刷新数据
     *
     */
    public void requestDataSetRefreshing() {
        // 如果没有正在刷新，设置刷新控件，且子类没有自动刷新
        if (!getMode().isRunning() && !getView().setRefreshViewToLoading())
            requestData(RefreshMode.reset);
    }

    @Override
    public void onSuccess(ModelListenerParam<Result> param) {
        if (param.getResult() == null) {
            super.onSuccess(param);
            return;
        }

        Result result = param.getResult();
        RefreshMode mode = ((PagingModelListenerParam<Result>) param).getRefreshMode();

        getView().bindAdapter(getView().getAdapter());

        List<Item> resultList;
        if (result instanceof List)
            resultList = (List<Item>) result;
        else {
            resultList = getMode().parseResult(result);
            if (resultList == null)
                resultList = new ArrayList<>();
        }

        // 如果子类没有处理新获取的数据刷新UI，默认替换所有数据
        if (!getView().handleResult(mode, resultList)) {
            if (mode == RefreshMode.reset) {
                getView().getAdapter().getDatas().clear();
                getView().getAdapter().getDatas().addAll(new ArrayList<Item>());
            }
        }

        // append数据
        if (mode == RefreshMode.reset || mode == RefreshMode.refresh)
            IPagingAdapter.Utils.addItemsAtFrontAndRefresh(getView().getAdapter(), resultList);
        else if (mode == RefreshMode.update)
            IPagingAdapter.Utils.addItemsAndRefresh(getView().getAdapter(), resultList);

        // 处理分页数据
        if (mPaging != null) {
            if (getView().getAdapter() != null && getView().getAdapter().getDatas().size() != 0)
                mPaging.processData(result, (Item) getView().getAdapter().getDatas().get(0),
                        (Item) getView().getAdapter().getDatas().get(getView().getAdapter().getDatas().size() - 1));
            else
                mPaging.processData(result, null, null);
        }

        // 如果是重置数据，重置canLoadMore
        if (mode == RefreshMode.reset)
            getView().getRefreshConfig().pagingEnd = false;
        // 如果数据少于这个值，默认加载完了
        if (mode == RefreshMode.update || mode == RefreshMode.reset)
            getView().getRefreshConfig().pagingEnd = resultList.size() == 0;

        // 如果是缓存数据，且已经过期
        if (result instanceof IResult) {
            // 这里增加一个自动刷新设置功能
            IResult iResult = (IResult) result;

            if (iResult.fromCache() && !iResult.outofdate())
                getView().toLastReadPosition();

            if (mode == RefreshMode.reset || mode == RefreshMode.update) {
                if (iResult.endPaging())
                    getView().getRefreshConfig().pagingEnd = true;
                else
                    getView().getRefreshConfig().pagingEnd = false;
            }
        }

        if (mode == RefreshMode.reset && getTaskCount(AContentModel.TASK_ID) > 1)
            getView().getAdapter().notifyDataSetChanged();

        getView().setupRefreshViewWithConfig(getView().getRefreshConfig());

        super.onSuccess(param);
    }

    @Override
    public IPaging<Item, Result> getPaging() {
        return mPaging;
    }

    @Override
    public void onTaskStateChanged(ModelListenerParam<Result> param) {
        super.onTaskStateChanged(param);

        RefreshMode mode = ((IPagingModelParams) param).getRefreshMode();
        TaskState state = param.getTaskState();
        TaskException exception = param.getException();

        getView().onTaskStateChanged((PagingModelListenerParam<Result>) param);

        if (state == TaskState.success) {
            if (getView().isContentEmpty()) {
                getView().setEmptyHind(getView().getRefreshConfig().emptyHint);
            }
        }
        else if (state == TaskState.falid) {
            if (getView().isContentEmpty()) {
                getView().setFailureHint(exception.getMessage());
            }
        }
        else if (state == TaskState.finished) {
            getView().setRefreshViewFinished(mode);
        }
    }

}
