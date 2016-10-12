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
public abstract class AContentModel<Result extends Serializable> implements IModel<Result> {

    public static final String TASK_ID = "ModeTask";

    private ITaskManager taskManager;
    private IModelListener<Result> modelListener;
    private ContentModelTask mTask;

    @Override
    public void setCallback(IModelListener<Result> listener) {
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
    public void execute() {
        new ContentModelTask().execute();
    }

    class ContentModelTask extends WorkTask<Void, Void, Result> {

        public ContentModelTask() {
            super(TASK_ID, taskManager);

            if (mTask != null) {
                mTask.cancel(true);
            }
            mTask = this;
        }

        @Override
        protected void onPrepare() {
            super.onPrepare();

            getCallback().onPrepare(null);
        }

        @Override
        public Result workInBackground(Void... params) throws TaskException {
            return AContentModel.this.workInBackground();
        }

        @Override
        protected void onSuccess(final Result result) {
            super.onSuccess(result);

            getCallback().onSuccess(new IModelListener.OnSuccessParam<Result>() {

                @Override
                public Result getResult() {
                    return result;
                }

            });
        }

        @Override
        protected void onFailure(final TaskException exception) {
            super.onFailure(exception);

            getCallback().onFailure(new IModelListener.OnFailureParam() {
                @Override
                public TaskException getException() {
                    return exception;
                }

            });
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            getCallback().onFinished(null);

            mTask = null;
        }

    }

    @Override
    public boolean isRunning() {
        return mTask != null;
    }

    /**
     * 执行异步任务
     *
     * @return
     * @throws TaskException
     */
    abstract protected Result workInBackground() throws TaskException;

}
