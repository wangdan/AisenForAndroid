package org.aisen.wen.ui.model;

import org.aisen.wen.component.network.task.TaskException;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IModelListener<Result extends Serializable> {

    <Param extends OnPrepareParam> void onPrepare(Param param);

    <Param extends OnSuccessParam<Result>> void onSuccess(Param param);

    <Param extends OnFailureParam> void onFailure(Param param);

    <Param extends OnFinishedParam> void onFinished(Param param);

    interface OnPrepareParam {

    }

    interface OnSuccessParam<Result extends Serializable> {

        Result getResult();

    }

    interface OnFailureParam {

        TaskException getException();

    }

    interface OnFinishedParam {

    }

}
