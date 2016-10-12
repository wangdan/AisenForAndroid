package org.aisen.wen.ui.model.impl;

import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.model.IModelListener;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;

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

            getCallback().onPrepare(new IModelListener.IModelParam() {
                @Override
                public AContentPresenter.TaskState getTaskState() {
                    return AContentPresenter.TaskState.prepare;
                }

                @Override
                public Result getResult() {
                    return null;
                }

                @Override
                public TaskException getException() {
                    return null;
                }

            });
        }

        @Override
        public Result workInBackground(Void... params) throws TaskException {
            return AContentModel.this.workInBackground();
        }

        @Override
        protected void onSuccess(final Result result) {
            super.onSuccess(result);

            getCallback().onSuccess(new IModelListener.IModelParam<Result>() {

                @Override
                public AContentPresenter.TaskState getTaskState() {
                    return AContentPresenter.TaskState.success;
                }

                @Override
                public Result getResult() {
                    return result;
                }

                @Override
                public TaskException getException() {
                    return null;
                }

            });
        }

        @Override
        protected void onFailure(final TaskException exception) {
            super.onFailure(exception);

            getCallback().onFailure(new IModelListener.IModelParam() {
                @Override
                public AContentPresenter.TaskState getTaskState() {
                    return AContentPresenter.TaskState.falid;
                }

                @Override
                public Result getResult() {
                    return null;
                }

                @Override
                public TaskException getException() {
                    return exception;
                }

            });
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            getCallback().onFinished(new IModelListener.IModelParam() {
                @Override
                public AContentPresenter.TaskState getTaskState() {
                    return AContentPresenter.TaskState.finished;
                }

                @Override
                public Result getResult() {
                    return null;
                }

                @Override
                public TaskException getException() {
                    return null;
                }

            });

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
