package org.aisen.wen.ui.model.listener;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.IContentPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/12.
 */
public class ModelListenerParam<Result extends Serializable> {

    private final IContentPresenter.TaskState taskState;

    private final TaskException exception;

    private final Result result;

    public ModelListenerParam(IContentPresenter.TaskState taskState, Result result, TaskException exception) {
        this.taskState = taskState;
        this.exception = exception;
        this.result = result;
    }

    public IContentPresenter.TaskState getTaskState() {
        return taskState;
    }

    public TaskException getException() {
        return exception;
    }

    public Result getResult() {
        return result;
    }

}
