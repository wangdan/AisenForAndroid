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
public abstract class AContentModel<Params, Progress, Result extends Serializable> implements IModel<Params> {

    private static final String TAST_ID = "ContentModelTask";

    private final ITaskManager taskManager;
    private final IModelListener<Progress, Result> modelListener;

    public AContentModel(IModelListener<Progress, Result> modelListener, ITaskManager taskManager) {
        this.modelListener = modelListener;
        this.taskManager = taskManager;
    }

    @Override
    final public void execute(Params... params) {
        new ContentModelTask(TAST_ID).execute(params);
    }

    protected class ContentModelTask extends WorkTask<Params, Progress, Result> {

        public ContentModelTask(String taskId) {
            super(taskId, taskManager);
        }

        @Override
        protected void onPrepare() {
            super.onPrepare();

            modelListener.onPrepare();
        }

        @Override
        public Result workInBackground(Params... params) throws TaskException {
            return AContentModel.this.workInBackground(params);
        }

        @Override
        protected void onProgressUpdate(Progress... values) {
            super.onProgressUpdate(values);
            
            modelListener.onProgressUpdate(values);
        }

        @Override
        protected void onSuccess(Result result) {
            super.onSuccess(result);

            modelListener.onSuccess(result);
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            modelListener.onFailure(exception);
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            modelListener.onFinished();
        }

    }

    public boolean resultIsEmpty(Result result) {
        return result == null ? true : false;
    }

    abstract protected Result workInBackground(Params... params) throws TaskException;

}
