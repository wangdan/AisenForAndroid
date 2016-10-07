package org.aisen.wen.ui.presenter;

import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.model.IModelListener;
import org.aisen.wen.ui.view.IView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class APresenter<Mode extends IModel, View extends IView, Progress, Result extends Serializable>
                                            implements IModelListener<Progress, Result>, ILifecycleBridge {

    private final Mode mMode;
    private final View mView;

    public APresenter(Mode mode, View view) {
        this.mView = view;
        this.mMode = mode;
    }

    public Mode getMode() {
        return mMode;
    }

    public View getView() {
        return mView;
    }

}
