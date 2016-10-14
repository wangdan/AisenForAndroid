package org.aisen.sample.ui.fragment;

import android.app.Fragment;

import org.aisen.wen.ui.fragment.APresenterFragment;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.view.IContentView;

/**
 * Created by wangdan on 15/4/23.
 */
public class BaseFragmentSample extends APresenterFragment {

    public static Fragment newInstance() {
        return new BaseFragmentSample();
    }

    @Override
    protected IModel setPresenterModel() {
        return new BaseFragmentModel();
    }

    @Override
    protected IContentView setPresenterView() {
        return new BaseFragmentView();
    }

}
