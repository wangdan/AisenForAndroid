package org.aisen.wen.ui.presenter.impl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.TaskManager;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.presenter.ABridgePresenter;
import org.aisen.wen.ui.view.impl.AContentView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentPresenter<Result extends Serializable, ContentMode extends AContentModel<Result>, ContentView extends AContentView>
                                                        extends ABridgePresenter<Result, ContentMode, ContentView>
                                                        implements ITaskManager {

    private final static String TAG = "ContentPresenter";

    public enum TaskState {
        none, prepare, falid, success, finished, canceled
    }

    private final static long DELAY_REQUEST = 500;

    private final TaskManager taskManager;// 管理线程

    // UI线程的Handler
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
    };

    public AContentPresenter(ContentMode mode, ContentView view) {
        super(mode, view);

        view.setPresenter(this);

        this.taskManager = new TaskManager();
    }

    @Override
    public void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState) {
        super.onBridgeActivityCreate(activity, savedInstanceState);

        if (savedInstanceState != null) {
            taskManager.restore(savedInstanceState);

            if (getView().isContentLayoutEmpty()) {
                requestData();
            }
            else {
                getView().setContentLayoutVisibility(android.view.View.VISIBLE);
                getView().setEmptyLayoutVisibility(android.view.View.GONE);
                getView().setFailureLayoutVisibility(android.view.View.GONE, null);
                getView().setLoadingLayoutVisibility(android.view.View.GONE);
            }
        }
        else {
            requestData();
        }
    }

    @Override
    public void onBridgeSaveInstanceState(Bundle outState) {
        super.onBridgeSaveInstanceState(outState);

        taskManager.save(outState);
    }

    @Override
    public void onBridgeDestory() {
        super.onBridgeDestory();

        removeAllTask(true);
    }

    @Override
    public void onPrepare() {
        onTaskStateChanged(TaskState.prepare, null);
    }

    @Override
    public void onSuccess(Result result) {
        // 默认加载数据成功，且ContentView有数据展示
        getView().setContentLayoutEmpty(getMode().resultIsEmpty(result));

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
            if (getView().isContentLayoutEmpty()) {
                getView().setLoadingLayoutVisibility(View.VISIBLE);

                getView().setContentLayoutVisibility(View.GONE);
            }
            else {
                getView().setLoadingLayoutVisibility(View.GONE);

                getView().setContentLayoutVisibility(View.VISIBLE);
            }

            getView().setEmptyLayoutVisibility(View.GONE);
            if (getView().isContentLayoutEmpty()) {
                getView().setFailureLayoutVisibility(View.GONE, null);
            }
        }
        // Task成功
        else if (state == TaskState.success) {
            getView().setLoadingLayoutVisibility(View.GONE);

            if (getView().isContentLayoutEmpty()) {
                getView().setEmptyLayoutVisibility(View.VISIBLE);
                getView().setContentLayoutVisibility(View.GONE);
            }
            else {
                getView().setContentLayoutVisibility(View.VISIBLE);
                getView().setEmptyLayoutVisibility(View.GONE);
            }
        }
        // 取消Task
        else if (state == TaskState.canceled) {
            if (getView().isContentLayoutEmpty()) {
                getView().setLoadingLayoutVisibility(View.GONE);
                getView().setEmptyLayoutVisibility(View.VISIBLE);
            }
        }
        // Task失败
        else if (state == TaskState.falid) {
            if (getView().isContentLayoutEmpty()) {
                getView().setFailureLayoutVisibility(View.VISIBLE, exception);

                getView().setLoadingLayoutVisibility(View.GONE);
            }
        }
        // Task结束
        else if (state == TaskState.finished) {

        }
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
