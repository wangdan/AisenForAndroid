package org.aisen.wen.ui.model;

import org.aisen.wen.ui.presenter.impl.APagingPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/12.
 */
public interface IPagingModelListener<Result extends Serializable> extends IModelListener<Result> {

    interface IPaingModeListenerParam extends IModelListenerParam {

        APagingPresenter.RefreshMode getRefreshMode();

    }

}
