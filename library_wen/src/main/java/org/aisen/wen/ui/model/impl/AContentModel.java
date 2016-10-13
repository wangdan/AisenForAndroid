package org.aisen.wen.ui.model.impl;

import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.model.listener.IModelListener;
import org.aisen.wen.ui.model.listener.ModelListenerParam;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentModel<Result extends Serializable> implements IModel<Result> {

    private static final String TASK_ID = "ModeTask";

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

    @Override
    public String getTaskId() {
        return TASK_ID;
    }

    class ContentModelTask extends WorkTask<Void, Void, Result> {

        public ContentModelTask() {
            super(AContentModel.this.getTaskId(), taskManager);

            if (mTask != null) {
                mTask.cancel(true);
            }
            mTask = this;
        }

        @Override
        protected void onPrepare() {
            super.onPrepare();

            getCallback().onPrepare(getListenerParam(AContentPresenter.TaskState.prepare, null, null));
        }

        @Override
        public Result workInBackground(Void... params) throws TaskException {
            return AContentModel.this.workInBackground();
        }

        @Override
        protected void onSuccess(final Result result) {
            super.onSuccess(result);

            getCallback().onSuccess(getListenerParam(AContentPresenter.TaskState.success, null, result));
        }

        @Override
        protected void onFailure(final TaskException exception) {
            super.onFailure(exception);

            getCallback().onFailure(getListenerParam(AContentPresenter.TaskState.falid, exception, null));
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            getCallback().onFinished(getListenerParam(AContentPresenter.TaskState.finished, null, null));

            mTask = null;
        }

        ModelListenerParam<Result> getListenerParam(AContentPresenter.TaskState taskState, TaskException exception, Result result) {
            return new ModelListenerParam(taskState, result, exception);
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
