package org.aisen.wen.ui.presenter.impl;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.model.listener.IModelListener;
import org.aisen.wen.ui.model.listener.ModelListenerParam;
import org.aisen.wen.ui.presenter.ILifecycleBridge;
import org.aisen.wen.ui.presenter.IPresenter;
import org.aisen.wen.ui.view.IContentView;

import java.io.Serializable;

/**
 * 主要实现Activity或者Fragment的生命周期方法
 * 将生命周期方法绑定到View
 * 如果View实现了IModeListener，将Mode回调到View
 *
 * @param <Result>
 * @param <Mode>
 * @param <View>
 */
public abstract class ABridgePresenter<Result extends Serializable,
                                       Mode extends IModel<Result>,
                                       View extends IContentView>
                                implements IPresenter, IModelListener<Result>, ILifecycleBridge {

    private final Mode mMode;
    private final View mView;

    public ABridgePresenter(Mode mode, View view) {
        this.mView = view;
        this.mMode = mode;
    }

    public Mode getMode() {
        return mMode;
    }

    public View getView() {
        return mView;
    }

    @Override
    public void setContext(Activity context) {
        getView().setContext(context);
    }

    @Override
    public void onBridgeCreate(Bundle savedInstanceState) {
        getMode().setCallback(this);
        getView().onBridgeCreate(savedInstanceState);
    }

    @Override
    public void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView().onBridgeCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState) {
        getView().onBridgeActivityCreate(activity, savedInstanceState);
        getView().bindView(getView().getContentView());
        getView().bindEvent(getView().getContentView());
    }

    @Override
    public void onBridgeStart() {
        getView().onBridgeStart();
    }

    @Override
    public void onBridgeResume() {
        getView().onBridgeResume();
    }

    @Override
    public void onBridgePause() {
        getView().onBridgePause();
    }

    @Override
    public void onBridgeStop() {
        getView().onBridgeStop();
    }

    @Override
    public void onBridgeDestory() {
        getView().onBridgeDestory();
    }

    @Override
    public void onBridgeSaveInstanceState(Bundle outState) {
        getView().onBridgeSaveInstanceState(outState);
    }

    @Override
    public void onFinished(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onFinished(param);
        }
    }

    @Override
    public void onFailure(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onFailure(param);
        }
    }

    @Override
    public void onSuccess(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onSuccess(param);
        }
    }

    @Override
    public void onPrepare(ModelListenerParam<Result> param) {
        if (getView() instanceof IModelListener) {
            ((IModelListener) getView()).onPrepare(param);
        }
    }

}
