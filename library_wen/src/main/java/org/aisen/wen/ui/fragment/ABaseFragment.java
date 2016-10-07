package org.aisen.wen.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.presenter.ILifecycleBridge;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;
import org.aisen.wen.ui.view.impl.AContentView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/2.
 */
public abstract class ABaseFragment<Result extends Serializable, ContentMode extends AContentModel<Result>, ContentView extends AContentView>
                            extends Fragment {

    private ILifecycleBridge lifecycleBridge;
    private AContentPresenter<Result, ContentMode, ContentView> contentPresenter;

    public abstract ContentView newContentView();

    public abstract ContentMode newContentMode();

    class InnerPresenter extends AContentPresenter<Result, ContentMode, ContentView> {

        public InnerPresenter() {
            super(newContentMode(), newContentView());
        }

        @Override
        public void requestData() {
            getMode().execute();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentPresenter = new InnerPresenter();
        lifecycleBridge = contentPresenter;
        lifecycleBridge.onBridgeCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lifecycleBridge.onBridgeCreateView(inflater, container, savedInstanceState);

        return contentPresenter.getView().getContentView();
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

}
