package org.aisen.wen.ui.fragment;

import android.os.Bundle;
import android.view.ViewGroup;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.model.IPagingModel;
import org.aisen.wen.ui.model.listener.IModelListener;
import org.aisen.wen.ui.model.listener.ModelListenerParam;
import org.aisen.wen.ui.model.listener.PagingModelListenerParam;
import org.aisen.wen.ui.presenter.IPagingPresenter;
import org.aisen.wen.ui.view.IPaingView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdan on 16/10/14.
 */
public abstract class APagingPresenterFragment<Item extends Serializable,
                                               Result extends Serializable,
                                               Header extends Serializable,
                                               RefreshView extends ViewGroup,
                                               ContentMode extends IPagingModel<Item, Result>,
                                               ContentView extends IPaingView<Item, Result, Header, RefreshView>> extends APresenterFragment<Result, ContentMode, ContentView>
                                implements IPagingPresenter<Item, Result, Header, RefreshView, ContentMode, ContentView>, 
                                           IModelListener<Result>, 
                                           IPaingView.IPagingViewCallback{

    private static final String SAVED_PAGING = "org.aisen.android.ui.Paging";

    private IPaging<Item, Result> mPaging;// 分页器
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getPView().setPagingViewCallback(this);

        if (savedInstanceState != null && savedInstanceState.getSerializable(SAVED_PAGING) != null) {
            mPaging = (IPaging) savedInstanceState.getSerializable(SAVED_PAGING);
        } else {
            mPaging = newPaging();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
        if (getPView().getAdapter().getDatas().size() == 0 && getPView().getLoadingLayout() == null)
            mode = RefreshMode.update;

        requestData(mode);
    }

    public void requestData(RefreshMode mode) {
        if (mode == RefreshMode.reset && mPaging != null)
            mPaging = newPaging();

        getPModel().execute(mode, mPaging);
    }

    @Override
    public void requestDataOutofdate() {
        getPView().putLastReadPosition(0);
        getPView().putLastReadTop(0);

        requestDataSetRefreshing();
    }

    /**
     * 设置刷新控件为刷新状态且刷新数据
     *
     */
    public void requestDataSetRefreshing() {
        // 如果没有正在刷新，设置刷新控件，且子类没有自动刷新
        if (!getPModel().isRunning() && !getPView().setRefreshViewToLoading())
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

        getPView().bindAdapter(getPView().getAdapter());

        List<Item> resultList;
        if (result instanceof List)
            resultList = (List<Item>) result;
        else {
            resultList = getPModel().parseResult(result);
            if (resultList == null)
                resultList = new ArrayList<>();
        }

        // 如果子类没有处理新获取的数据刷新UI，默认替换所有数据
        if (!getPView().handleResult(mode, resultList, result)) {
            if (mode == RefreshMode.reset) {
                getPView().getAdapter().getDatas().clear();
                getPView().getAdapter().getDatas().addAll(new ArrayList<Item>());
            }
        }

        // append数据
        if (mode == RefreshMode.reset || mode == RefreshMode.refresh)
            IPagingAdapter.Utils.addItemsAtFrontAndRefresh(getPView().getAdapter(), resultList);
        else if (mode == RefreshMode.update)
            IPagingAdapter.Utils.addItemsAndRefresh(getPView().getAdapter(), resultList);

        // 处理分页数据
        if (mPaging != null) {
            if (getPView().getAdapter() != null && getPView().getAdapter().getDatas().size() != 0)
                mPaging.processData(result, (Item) getPView().getAdapter().getDatas().get(0),
                        (Item) getPView().getAdapter().getDatas().get(getPView().getAdapter().getDatas().size() - 1));
            else
                mPaging.processData(result, null, null);
        }

        // 如果是重置数据，重置canLoadMore
        if (mode == RefreshMode.reset)
            getPView().getRefreshConfig().pagingEnd = false;
        // 如果数据少于这个值，默认加载完了
        if (mode == RefreshMode.update || mode == RefreshMode.reset)
            getPView().getRefreshConfig().pagingEnd = resultList.size() == 0;

        // 如果是缓存数据，且已经过期
        if (result instanceof IResult) {
            // 这里增加一个自动刷新设置功能
            IResult iResult = (IResult) result;

            if (iResult.fromCache() && !iResult.outofdate())
                getPView().toLastReadPosition();

            if (mode == RefreshMode.reset || mode == RefreshMode.update) {
                if (iResult.endPaging())
                    getPView().getRefreshConfig().pagingEnd = true;
                else
                    getPView().getRefreshConfig().pagingEnd = false;
            }
        }

        if (mode == RefreshMode.reset && getTaskCount(getPModel().getTaskId()) > 1)
            getPView().getAdapter().notifyDataSetChanged();

        getPView().setupRefreshViewWithConfig(getPView().getRefreshConfig());

        super.onSuccess(param);
    }

    @Override
    public IPaging<Item, Result> newPaging() {
        return null;
    }

    @Override
    public IPaging<Item, Result> getPaging() {
        return mPaging;
    }

    @Override
    public void onTaskStateChanged(ModelListenerParam<Result> param) {
        super.onTaskStateChanged(param);

        RefreshMode mode = ((PagingModelListenerParam) param).getRefreshMode();
        TaskState state = param.getTaskState();
        TaskException exception = param.getException();

        getPView().onTaskStateChanged((PagingModelListenerParam<Result>) param);

        if (state == TaskState.success) {
            if (getPView().isContentEmpty()) {
                getPView().setEmptyHind(getPView().getRefreshConfig().emptyHint);
            }
        }
        else if (state == TaskState.falid) {
            if (getPView().isContentEmpty()) {
                getPView().setFailureHint(exception.getMessage());
            }
        }
        else if (state == TaskState.finished) {
            getPView().setRefreshViewFinished(mode);
        }
    }
    
}
