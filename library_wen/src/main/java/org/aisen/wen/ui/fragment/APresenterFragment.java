package org.aisen.wen.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aisen.wen.component.network.biz.IResult;
import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.IWorkTask;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.component.network.task.TaskManager;
import org.aisen.wen.support.utils.Logger;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.model.listener.IModelListener;
import org.aisen.wen.ui.model.listener.ModelListenerParam;
import org.aisen.wen.ui.presenter.IContentPresenter;
import org.aisen.wen.ui.view.IContentView;

import java.io.Serializable;

/**
 * 管理好IContentPresenter
 *
 * Created by wangdan on 16/10/2.
 */
public abstract class APresenterFragment<Result extends Serializable, Model extends IModel<Result>, V extends IContentView>
                extends ABaseFragment
                implements IContentPresenter<Result, Model, V>, IModelListener<Result>, ITaskManager {

    private final static String TAG = "ContentPresenterFragment";

    private Model mPModel;
    private V mPView;

    private final static long DELAY_REQUEST = 500;

    private TaskManager taskManager;// 管理线程

    // UI线程的Handler
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
    };

    @Override
    final public Model getPModel() {
        return mPModel;
    }

    @Override
    final public V getPView() {
        return mPView;
    }

    @Override
    public void onFinished(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onFinished(param);
        }

        onTaskStateChanged(param);
    }

    @Override
    public void onFailure(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onFailure(param);
        }

        onTaskStateChanged(param);
    }

    @Override
    public void onSuccess(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onSuccess(param);
        }

        Result result = param.getResult();

        // 默认加载数据成功，且ContentView有数据展示
        getPView().setContentLayout(resultIsEmpty(result));

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
    public void onPrepare(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onPrepare(param);
        }

        onTaskStateChanged(param);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPModel = setPresenterModel();
        mPView = setPresenterView();

        mPView.setPresenter(this);

        this.taskManager = new TaskManager();

        getPModel().setCallback(this);
        getPView().setBridgeContext(getActivity());
        getPView().onBridgeCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = getPView().onBridgeCreateView(inflater, container, savedInstanceState);
        if (contentView != null) {
            getPView().bindView(contentView, savedInstanceState);
            getPView().bindEvent(contentView, savedInstanceState);
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return contentView;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPView().onBridgeActivityCreate(getActivity(), savedInstanceState);

        if (savedInstanceState != null) {
            taskManager.restore(savedInstanceState);

            if (getPView().isContentEmpty()) {
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
    public void onStart() {
        super.onStart();

        getPView().onBridgeStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        getPView().onBridgeResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        getPView().onBridgePause();
    }

    @Override
    public void onStop() {
        super.onStop();

        getPView().onBridgeStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeAllTask(true);

        getPView().onBridgeDestory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        taskManager.save(outState);

        getPView().onBridgeSaveInstanceState(outState);
    }

    @Override
    public View getContentView() {
        return getPView().getContentView();
    }

    @Override
    final public int setLayoutId() {
        return -1;
    }

    @Override
    final public void addTask(IWorkTask task) {
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
    public void onTaskStateChanged(ModelListenerParam<Result> param) {
        TaskState state = param.getTaskState();
        TaskException exception = param.getException();

        // 开始Task
        if (state == TaskState.prepare) {
            if (getPView().isContentEmpty()) {
                setLoadingLayoutVisibility(View.VISIBLE);

                setContentLayoutVisibility(View.GONE);
            }
            else {
                setLoadingLayoutVisibility(View.GONE);

                setContentLayoutVisibility(View.VISIBLE);
            }

            setEmptyLayoutVisibility(View.GONE);
            if (getPView().isContentEmpty()) {
                setFailureLayoutVisibility(View.GONE, null);
            }
        }
        // Task成功
        else if (state == TaskState.success) {
            setLoadingLayoutVisibility(View.GONE);

            if (getPView().isContentEmpty()) {
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
            if (getPView().isContentEmpty()) {
                setFailureLayoutVisibility(View.VISIBLE, exception);

                setLoadingLayoutVisibility(View.GONE);
            }
        }
        // Task结束
        else if (state == TaskState.finished) {

        }
    }

    protected void setLoadingLayoutVisibility(int visibility) {
        if (getPView().getLoadingLayout() != null) {
            getPView().getLoadingLayout().setVisibility(visibility);
        }
    }

    protected void setEmptyLayoutVisibility(int visibility) {
        if (getPView().getEmptyLayout() != null) {
            getPView().getEmptyLayout().setVisibility(visibility);
        }
    }

    protected void setContentLayoutVisibility(int visibility) {
        if (getPView().getContentLayout() != null) {
            getPView().getContentLayout().setVisibility(visibility);
        }
    }

    protected void setFailureLayoutVisibility(int visibility, TaskException e) {
        if (getPView().getFailureLayout() != null) {
            getPView().getFailureLayout().setVisibility(visibility);

            if (e != null) {
                getPView().setFailureHint(e.getMessage());
            }

            setEmptyLayoutVisibility(View.GONE);
        } else {
            setEmptyLayoutVisibility(View.VISIBLE);
        }
    }

    @Override
    public void requestData() {
        getPModel().execute();
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

    abstract protected Model setPresenterModel();

    abstract protected V setPresenterView();

}
