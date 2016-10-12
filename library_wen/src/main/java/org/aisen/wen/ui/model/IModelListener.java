package org.aisen.wen.ui.model;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IModelListener<Result extends Serializable> {

    <Param extends IModelListenerParam> void onPrepare(Param param);

    <Param extends IModelListenerParam<Result>> void onSuccess(Param param);

    <Param extends IModelListenerParam> void onFailure(Param param);

    <Param extends IModelListenerParam> void onFinished(Param param);

    interface IModelListenerParam<Result extends Serializable> {

        AContentPresenter.TaskState getTaskState();

        Result getResult();

        TaskException getException();

    }

}
