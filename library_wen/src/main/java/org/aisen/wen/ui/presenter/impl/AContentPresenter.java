package org.aisen.wen.ui.presenter.impl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.TaskManager;
import org.aisen.wen.component.network.task.WorkTask;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.view.IContentView;

import java.io.Serializable;

/**
 * 根据Mode的回调方法，自动切换4种视图的切换
 * 管理Task的生命周期
 *
 * @param <Result>
 * @param <ContentMode>
 * @param <ContentView>
 */
public abstract class AContentPresenter<Result extends Serializable,
                                        ContentMode extends IModel<Result>,
                                        ContentView extends IContentView>
                                            extends ABridgePresenter<Result, ContentMode, ContentView>
                                            implements IContentPresenter {

    private final static String TAG = "ContentPresenter";

    private final static long DELAY_REQUEST = 500;

    /**
     * ContentView对应Mode的四种状态
     */
    public enum TaskState {
        prepare, falid, success, finished
    }

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

            if (getView().isContentEmpty()) {
                requestData();
            }
            else {
                setContentLayoutVisibility(android.view.View.VISIBLE);
                setEmptyLayoutVisibility(android.view.View.GONE);
                setFailureLayoutVisibility(android.view.View.GONE, null);
                setLoadingLayoutVisibility(android.view.View.GONE);
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
    public <Param extends IModelListenerParam> void onPrepare(Param param) {
        super.onPrepare(param);

        onTaskStateChanged(param);
    }

    @Override
    public <Param extends IModelListenerParam<Result>> void onSuccess(Param param) {
        super.onSuccess(param);

        Result result = param.getResult();

        // 默认加载数据成功，且ContentView有数据展示
        getView().setContentLayout(resultIsEmpty(result));

        onTaskStateChanged(param);

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
    public <Param extends IModelListenerParam> void onFailure(final Param param) {
        super.onFailure(param);

        onTaskStateChanged(param);
    }

    @Override
    public <Param extends IModelListenerParam> void onFinished(Param param) {
        super.onFinished(param);

        onTaskStateChanged(param);
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
     * Task状态改变时，切换各种View的状态
     *
     */
    public void onTaskStateChanged(IModelListenerParam param) {
        TaskState state = param.getTaskState();
        TaskException exception = param.getException();

        // 开始Task
        if (state == TaskState.prepare) {
            if (getView().isContentEmpty()) {
                setLoadingLayoutVisibility(View.VISIBLE);

                setContentLayoutVisibility(View.GONE);
            }
            else {
                setLoadingLayoutVisibility(View.GONE);

                setContentLayoutVisibility(View.VISIBLE);
            }

            setEmptyLayoutVisibility(View.GONE);
            if (getView().isContentEmpty()) {
                setFailureLayoutVisibility(View.GONE, null);
            }
        }
        // Task成功
        else if (state == TaskState.success) {
            setLoadingLayoutVisibility(View.GONE);

            if (getView().isContentEmpty()) {
                setEmptyLayoutVisibility(View.VISIBLE);
                setContentLayoutVisibility(View.GONE);
            }
            else {
                setContentLayoutVisibility(View.VISIBLE);
                setEmptyLayoutVisibility(View.GONE);
            }
        }
        // Task失败
        else if (state == TaskState.falid) {
            if (getView().isContentEmpty()) {
                setFailureLayoutVisibility(View.VISIBLE, exception);

                setLoadingLayoutVisibility(View.GONE);
            }
        }
        // Task结束
        else if (state == TaskState.finished) {

        }
    }

    protected void setLoadingLayoutVisibility(int visibility) {
        if (getView().getLoadingLayout() != null) {
            getView().getLoadingLayout().setVisibility(visibility);
        }
    }

    protected void setEmptyLayoutVisibility(int visibility) {
        if (getView().getEmptyLayout() != null) {
            getView().getEmptyLayout().setVisibility(visibility);
        }
    }

    protected void setContentLayoutVisibility(int visibility) {
        if (getView().getContentLayout() != null) {
            getView().getContentLayout().setVisibility(visibility);
        }
    }

    protected void setFailureLayoutVisibility(int visibility, TaskException e) {
        if (getView().getFailureLayout() != null) {
            getView().getFailureLayout().setVisibility(visibility);

            if (e != null) {
                getView().setFailureHint(e.getMessage());
            }

            setEmptyLayoutVisibility(View.GONE);
        } else {
            setEmptyLayoutVisibility(View.VISIBLE);
        }
    }

    /**
     * 缓存数据失效，重新刷新数据
     *
     */
    @Override
    public void requestDataOutofdate() {
        requestData();
    }

    public boolean resultIsEmpty(Result result) {
        return result == null ? true : false;
    }

}
