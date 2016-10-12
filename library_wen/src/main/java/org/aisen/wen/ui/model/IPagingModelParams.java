package org.aisen.wen.ui.model;

import org.aisen.wen.ui.presenter.impl.APagingPresenter;

/**
 * Created by wangdan on 16/10/12.
 */
public interface IPagingModelParams extends IModelParams {

    APagingPresenter.RefreshMode getRefreshMode();

}
