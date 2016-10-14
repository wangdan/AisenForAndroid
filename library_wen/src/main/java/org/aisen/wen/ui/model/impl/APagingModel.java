package org.aisen.wen.ui.model.impl;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.model.IPagingModel;
import org.aisen.wen.ui.model.listener.ModelListenerParam;
import org.aisen.wen.ui.model.listener.PagingModelListenerParam;
import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/10.
 */
public abstract class APagingModel<Item extends Serializable,
                                  Result extends Serializable>
                        extends AContentModel<Result>
                        implements IPagingModel<Item, Result> {

    @Override
    public void execute() {
        execute(IPagingPresenter.RefreshMode.reset, null);
    }

    @Override
    public void execute(IPagingPresenter.RefreshMode mode, IPaging<Item, Result> paging) {
        new PagingTask(mode, paging).execute();
    }

    @Override
    public Result workInBackground() throws TaskException {
        throw new TaskException("not supported");
    }

    class PagingTask extends ContentModelTask {

        IPagingPresenter.RefreshMode mode;
        IPaging<Item, Result> paging;

        public PagingTask(IPagingPresenter.RefreshMode mode, IPaging<Item, Result> paging) {
            super();

            this.mode = mode;
            this.paging = paging;
        }

        @Override
        public Result workInBackground(Void... params) throws TaskException {
            return APagingModel.this.workInBackground(mode, paging);
        }

        @Override
        ModelListenerParam getListenerParam(IContentPresenter.TaskState taskState, TaskException exception, Result result) {
            return new PagingModelListenerParam(taskState, result, exception, mode);
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
    abstract protected Result workInBackground(IPagingPresenter.RefreshMode mode, IPaging<Item, Result> paging) throws TaskException;

}
