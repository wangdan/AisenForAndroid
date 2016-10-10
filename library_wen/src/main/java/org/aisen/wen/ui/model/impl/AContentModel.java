package org.aisen.wen.ui.model.impl;

import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.ui.model.IContentMode;
import org.aisen.wen.ui.model.IModelListener;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentModel<Result extends Serializable> implements IContentMode<Result> {

    private static final String TAST_ID = "ContentModel";

    private ITaskManager taskManager;
    private IModelListener<Result> modelListener;

    @Override
    public void setCallback(IModelListener listener) {
        modelListener = listener;
        if (modelListener instanceof ITaskManager) {
            taskManager = (ITaskManager) modelListener;
        }
    }

    @Override
    public IModelListener<Result> getCallback() {
        return modelListener;
    }

    @Override
    final public void execute() {
        new ContentModelTask(TAST_ID).execute();
    }

    class ContentModelTask extends WorkTask<Void, Void, Result> {

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

    @Override
    public boolean resultIsEmpty(Result result) {
        return result == null ? true : false;
    }

}
