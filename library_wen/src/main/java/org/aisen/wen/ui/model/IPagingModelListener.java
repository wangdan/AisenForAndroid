package org.aisen.wen.ui.model;

import org.aisen.wen.ui.presenter.impl.APagingPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/12.
 */
public interface IPagingModelListener<Result extends Serializable> extends IModelListener<Result> {

    interface IPaingModeParam {

        APagingPresenter.RefreshMode getRefreshMode();

    }

    interface OnPagingPrepareParam extends OnPrepareParam, IPagingModelParams {

    }

    interface OnPagingSuccessParam<Result extends Serializable>
                    extends OnSuccessParam<Result>, IPagingModelParams {

        Result getResult();

    }

    interface OnPagingFailureParam extends OnFailureParam, IPagingModelParams {


    }

    interface OnPagingFinishedParam extends OnFinishedParam, IPagingModelParams {

    }

}
