package org.aisen.wen.ui.model.listener;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;
import org.aisen.wen.ui.presenter.impl.APagingPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/12.
 */
public class PagingModelListenerParam<Result extends Serializable> extends ModelListenerParam<Result> {

    private final APagingPresenter.RefreshMode mode;

    public PagingModelListenerParam(AContentPresenter.TaskState taskState, Result result, TaskException exception, APagingPresenter.RefreshMode mode) {
        super(taskState, result, exception);

        this.mode = mode;
    }

    public APagingPresenter.RefreshMode getRefreshMode() {
        return mode;
    }

}
