package org.aisen.wen.ui.presenter.impl;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.adapter.IPagingAdapter;
import org.aisen.wen.ui.model.IPagingModel;
import org.aisen.wen.ui.model.IPagingModelListener;
import org.aisen.wen.ui.model.impl.AContentModel;
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
                            implements IPagingPresenter<Item, Result>, IPagingModelListener<Result> {

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
    }

    @Override
    final public void requestData() {
        requestData(RefreshMode.reset);
    }

    public void requestData(RefreshMode mode) {
        if (mode == RefreshMode.reset && mPaging != null)
            mPaging = newPaging();

        getMode().execute(mode, mPaging);
    }

    @Override
    public <Param extends OnSuccessParam<Result>> void onSuccess(Param param) {
        if (param.getResult() == null) {
            super.onSuccess(param);
            return;
        }

        Result result = param.getResult();
        RefreshMode mode = ((OnPagingSuccessParam<Result>) param).getRefreshMode();

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

}
