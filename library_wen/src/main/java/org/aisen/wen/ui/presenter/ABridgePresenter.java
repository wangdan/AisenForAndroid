package org.aisen.wen.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.view.ABridgeView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/8.
 */
public abstract class ABridgePresenter<Mode extends IModel, View extends ABridgeView, Progress, Result extends Serializable>
                            extends APresenter<Mode, View, Progress, Result> {

    public ABridgePresenter(Mode mode, View view) {
        super(mode, view);
    }

    @Override
    public void onBridgeCreate(Bundle savedInstanceState) {
        getMode().bindCallback(this);
        getView().onBridgeCreate(savedInstanceState);
    }

    @Override
    public void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView().onBridgeCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onBridgeActivityCreate(Activity activity, Bundle savedInstanceState) {
        getView().onBridgeActivityCreate(activity, savedInstanceState);
        getView().bindView();
        getView().bindEvent();
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

}
