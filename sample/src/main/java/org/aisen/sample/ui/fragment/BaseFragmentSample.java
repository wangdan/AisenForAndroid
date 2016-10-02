package org.aisen.sample.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aisensample.R;

import org.aisen.android.support.inject.ViewInject;
import org.aisen.sample.support.bean.BaseBean;
import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.inject.InjectUtility;
import org.aisen.wen.ui.model.IModelListener;
import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;
import org.aisen.wen.ui.view.IContentView;
import org.aisen.wen.ui.view.IView;
import org.aisen.wen.ui.view.impl.AContentView;

/**
 * Created by wangdan on 15/4/23.
 */
public class BaseFragmentSample extends Fragment implements IView {

    public static Fragment newInstance() {
        return new BaseFragmentSample();
    }

    private BasePresenter basePresenter;
    private BaseModel baseModel;
    private BaseView baseView;

    @ViewInject(id = R.id.btn, click = "btnclick")
    View btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = new BaseView();
        basePresenter = new BasePresenter(baseView);
        baseModel = new BaseModel(basePresenter, basePresenter);

        basePresenter.setModel(baseModel);
        basePresenter.onCreate(inflater);

        return baseView.getContentView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        basePresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        basePresenter.onActivityCreate(getActivity(), savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        basePresenter.onDestory();
    }

    @Override
    public int layoutId() {
        return R.layout.ui_a_base;
    }

    @Override
    public void bindView() {
        InjectUtility.initInjectedView(baseView.getContext(), this, baseView.getContentView());
    }

    @Override
    public void bindEvent() {

    }

    @Override
    public View findViewById(int id) {
        return baseView.findViewById(id);
    }

    class BaseView extends AContentView {

        @Override
        public int layoutId() {
            return BaseFragmentSample.this.layoutId();
        }

        @Override
        public void bindView() {
            super.bindView();

            BaseFragmentSample.this.bindView();
        }

        @Override
        public void bindEvent() {
            BaseFragmentSample.this.bindEvent();
        }

    }

    class BasePresenter extends AContentPresenter<Void, Void, BaseBean> {

        public BasePresenter(IContentView view) {
            super(view);
        }

        @Override
        public void requestData() {
            getModel().execute();
        }

    }

    class BaseModel extends AContentModel<Void, Void, BaseBean> {

        public BaseModel(IModelListener<Void, BaseBean> modelListener, ITaskManager taskManager) {
            super(modelListener, taskManager);
        }

        @Override
        protected BaseBean workInBackground(Void... params) throws TaskException {
            return null;
        }

    }

}
