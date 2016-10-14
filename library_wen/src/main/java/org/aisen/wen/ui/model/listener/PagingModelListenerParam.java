package org.aisen.wen.ui.model.listener;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/12.
 */
public class PagingModelListenerParam<Result extends Serializable> extends ModelListenerParam<Result> {

    private final IPagingPresenter.RefreshMode mode;

    public PagingModelListenerParam(IContentPresenter.TaskState taskState, Result result, TaskException exception, IPagingPresenter.RefreshMode mode) {
        super(taskState, result, exception);

        this.mode = mode;
    }

    public IPagingPresenter.RefreshMode getRefreshMode() {
        return mode;
    }

}
