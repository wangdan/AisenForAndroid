package org.aisen.wen.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aisen.wen.ui.presenter.ILifecycleBridge;
import org.aisen.wen.ui.presenter.impl.ABridgePresenter;

/**
 * 管理好IContentPresenter
 *
 * Created by wangdan on 16/10/2.
 */
public abstract class APresenterFragment extends ABaseFragment {

    private View contentView;
    private ILifecycleBridge lifecycleBridge;
    private ABridgePresenter contentPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentPresenter = setPresenter();
        lifecycleBridge = contentPresenter;
        lifecycleBridge.setBridgeContext(getActivity());
        lifecycleBridge.onBridgeCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lifecycleBridge.onBridgeCreateView(inflater, container, savedInstanceState);

        contentView = contentPresenter.getView().getContentView();
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lifecycleBridge.onBridgeActivityCreate(getActivity(), savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        lifecycleBridge.onBridgeStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        lifecycleBridge.onBridgeResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        lifecycleBridge.onBridgePause();
    }

    @Override
    public void onStop() {
        super.onStop();

        lifecycleBridge.onBridgeStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        lifecycleBridge.onBridgeDestory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        lifecycleBridge.onBridgeSaveInstanceState(outState);
    }

    @Override
    public View getContentView() {
        return contentView;
    }

    abstract protected ABridgePresenter setPresenter();

}
