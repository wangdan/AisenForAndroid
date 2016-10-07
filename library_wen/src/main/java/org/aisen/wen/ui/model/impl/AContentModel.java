package org.aisen.wen.ui.model.impl;

import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.model.IModelListener;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentModel<Progress, Result extends Serializable> implements IModel {

    private static final String TAST_ID = "ContentModelTask";

    private ITaskManager taskManager;
    private IModelListener<Progress, Result> modelListener;

    @Override
    public void bindCallback(IModelListener listener) {
        modelListener = listener;
        if (modelListener instanceof ITaskManager) {
            taskManager = (ITaskManager) modelListener;
        }
    }

    @Override
    public IModelListener<Progress, Result> getCallback() {
        return modelListener;
    }

    @Override
    final public void execute() {
        new ContentModelTask(TAST_ID).execute();
    }

    class ContentModelTask extends WorkTask<Void, Progress, Result> {

        public ContentModelTask(String taskId) {
            super(taskId, taskManager);
        }

        @Override
        protected void onPrepare() {
            super.onPrepare();

            getCallback().onPrepare();
        }

        @Override
        public Result workInBackground(Void... params) throws TaskException {
            return AContentModel.this.workInBackground();
        }

        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);

            getCallback().onProgressUpdate(values);
        }

        @Override
        protected void onSuccess(Result result) {
            super.onSuccess(result);

            getCallback().onSuccess(result);
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            getCallback().onFailure(exception);
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            getCallback().onFinished();
        }

    }

    public boolean resultIsEmpty(Result result) {
        return result == null ? true : false;
    }

    abstract protected Result workInBackground(Void... params) throws TaskException;

}
