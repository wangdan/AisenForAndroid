package org.aisen.wen.ui.model.impl;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.model.IPagingModel;
import org.aisen.wen.ui.model.IPagingModelListener;
import org.aisen.wen.ui.presenter.impl.APagingPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/10.
 */
public abstract class APaingModel<Item extends Serializable,
                                  Result extends Serializable>
                        extends AContentModel<Result>
                        implements IPagingModel<Item, Result>, IPagingModelListener<Result> {

    @Override
    public void execute() {
        execute(APagingPresenter.RefreshMode.reset, null);
    }

    @Override
    public void execute(APagingPresenter.RefreshMode mode, IPaging<Item, Result> paging) {
        new PagingTask(mode, paging).execute();
    }

    @Override
    public Result workInBackground() throws TaskException {
        throw new TaskException("not supported");
    }

    class PagingTask extends ContentModelTask {

        APagingPresenter.RefreshMode mode;
        IPaging<Item, Result> paging;

        public PagingTask(APagingPresenter.RefreshMode mode, IPaging<Item, Result> paging) {
            super();

            this.mode = mode;
            this.paging = paging;
        }

        @Override
        protected void onPrepare() {
            getCallback().onPrepare(new OnPagingPrepareParam() {

                @Override
                public APagingPresenter.RefreshMode getRefreshMode() {
                    return mode;
                }

            });
        }

        @Override
        public Result workInBackground(Void... params) throws TaskException {
            return APaingModel.this.workInBackground(mode, paging);
        }

        @Override
        protected void onFailure(final TaskException exception) {
            getCallback().onFailure(new OnPagingFailureParam() {

                @Override
                public APagingPresenter.RefreshMode getRefreshMode() {
                    return mode;
                }

                @Override
                public TaskException getException() {
                    return exception;
                }

            });
        }

        @Override
        protected void onSuccess(final Result result) {
            getCallback().onSuccess(new OnPagingSuccessParam<Result>() {

                @Override
                public Result getResult() {
                    return result;
                }

                @Override
                public APagingPresenter.RefreshMode getRefreshMode() {
                    return mode;
                }

            });
        }

        @Override
        protected void onFinished() {
            getCallback().onFinished(new OnPagingFinishedParam() {

                @Override
                public APagingPresenter.RefreshMode getRefreshMode() {
                    return mode;
                }

            });
        }

    }

    /**
     * 异步执行方法
     *
     * @param mode 刷新模式
     * @param paging 分页
     * @return
     * @throws TaskException
     */
    abstract protected Result workInBackground(APagingPresenter.RefreshMode mode, IPaging<Item, Result> paging) throws TaskException;

}
