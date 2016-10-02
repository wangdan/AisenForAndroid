package org.aisen.wen.ui.presenter.impl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.TaskManager;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.presenter.ILifecycleBridge;
import org.aisen.wen.ui.presenter.IPresenter;
import org.aisen.wen.ui.view.IContentView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentPresenter<Params, Progress, Result extends Serializable>
                                                        implements IPresenter<Progress, Result>, ITaskManager, ILifecycleBridge {

    private final static String TAG = "ContentPresenter";

    public enum TaskState {
        none, prepare, falid, success, finished, canceled
    }

    private final static long DELAY_REQUEST = 500;

    private final TaskManager taskManager;// 管理线程

    private final IContentView mView;
    private AContentModel<Params, Progress, Result> mModel;

    // UI线程的Handler
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
    };

    public AContentPresenter(IContentView view) {
        this.mView = view;
        this.taskManager = new TaskManager();
    }

    @Override
    public void onCreate(LayoutInflater inflater) {
        mView.onCreate(inflater);
        mView.bindView();
        mView.bindEvent();
    }

    @Override
    public void onActivityCreate(Activity activity, Bundle savedInstanceState) {
        taskManager.restore(savedInstanceState);

        if (savedInstanceState != null) {
            if (mView.isContentLayoutEmpty()) {
                requestData();
            }
            else {
                mView.setContentLayoutVisibility(View.VISIBLE);
                mView.setEmptyLayoutVisibility(View.GONE);
                mView.setFailureLayoutVisibility(View.GONE, null);
                mView.setLoadingLayoutVisibility(View.GONE);
            }
        }
        else {
            requestData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        taskManager.save(outState);
        mView.onSaveInstanceState(outState);
    }

    @Override
    public void onPrepare() {
        onTaskStateChanged(TaskState.prepare, null);
    }

    @Override
    public void onProgressUpdate(Progress... values) {
    }

    @Override
    public void onSuccess(Result result) {
        // 默认加载数据成功，且ContentView有数据展示
        mView.setContentLayoutEmpty(mModel.resultIsEmpty(result));

        onTaskStateChanged(TaskState.success, null);

        if (result instanceof IResult) {
            IResult iResult = (IResult) result;

            // 数据是缓存数据
            if (iResult.fromCache()) {
                // 缓存过期刷新数据
                if (iResult.outofdate()) {
                    runUIRunnable(new Runnable() {

                        @Override
                        public void run() {
                            Logger.d(TAG, "数据过期，开始刷新, " + toString());

                            requestDataOutofdate();
                        }

                    }, DELAY_REQUEST);
                }
            }
        }
    }

    @Override
    public void onFailure(TaskException e) {
        onTaskStateChanged(TaskState.falid, e);
    }

    @Override
    public void onFinished() {
        onTaskStateChanged(TaskState.finished, null);
    }

    @Override
    public void onDestory() {
        removeAllTask(true);
    }

    @Override
    final public void addTask(WorkTask task) {
        taskManager.addTask(task);
    }

    @Override
    final public void removeTask(String taskId, boolean cancelIfRunning) {
        taskManager.removeTask(taskId, cancelIfRunning);
    }

    @Override
    final public void removeAllTask(boolean cancelIfRunning) {
        taskManager.removeAllTask(cancelIfRunning);
    }

    @Override
    final public int getTaskCount(String taskId) {
        return taskManager.getTaskCount(taskId);
    }

    @Override
    final public void clearTaskCount(String taskId) {
        taskManager.clearTaskCount(taskId);
    }

    public void runUIRunnable(Runnable runnable) {
        runUIRunnable(runnable, 0);
    }

    public void runUIRunnable(Runnable runnable, long delay) {
        if (delay > 0) {
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, delay);
        }
        else {
            mHandler.post(runnable);
        }
    }

    /**
     *
     * @param state
     *
     * @param exception
     */
    protected void onTaskStateChanged(TaskState state, TaskException exception) {
        // 开始Task
        if (state == TaskState.prepare) {
            if (mView.isContentLayoutEmpty()) {
                mView.setLoadingLayoutVisibility(View.VISIBLE);

                mView.setContentLayoutVisibility(View.GONE);
            }
            else {
                mView.setLoadingLayoutVisibility(View.GONE);

                mView.setContentLayoutVisibility(View.VISIBLE);
            }

            mView.setEmptyLayoutVisibility(View.GONE);
            if (mView.isContentLayoutEmpty()) {
                mView.setContentLayoutVisibility(View.VISIBLE);
            }

            mView.setFailureLayoutVisibility(View.GONE, null);
        }
        // Task成功
        else if (state == TaskState.success) {
            mView.setLoadingLayoutVisibility(View.GONE);

            if (mView.isContentLayoutEmpty()) {
                mView.setEmptyLayoutVisibility(View.VISIBLE);
                mView.setContentLayoutVisibility(View.GONE);
            }
            else {
                mView.setContentLayoutVisibility(View.VISIBLE);
                mView.setEmptyLayoutVisibility(View.GONE);
            }
        }
        // 取消Task
        else if (state == TaskState.canceled) {
            if (mView.isContentLayoutEmpty()) {
                mView.setLoadingLayoutVisibility(View.GONE);
                mView.setEmptyLayoutVisibility(View.VISIBLE);
            }
        }
        // Task失败
        else if (state == TaskState.falid) {
            if (mView.isContentLayoutEmpty()) {
                mView.setFailureLayoutVisibility(View.VISIBLE, exception);

                mView.setLoadingLayoutVisibility(View.GONE);
            }
        }
        // Task结束
        else if (state == TaskState.finished) {

        }
    }

    public AContentModel<Params, Progress, Result> getModel() {
        return mModel;
    }

    public void setModel(AContentModel<Params, Progress, Result> model) {
        this.mModel = model;
    }

    /**
     * 缓存数据失效，重新刷新数据
     *
     */
    public void requestDataOutofdate() {
        requestData();
    }

    abstract public void requestData();

}
