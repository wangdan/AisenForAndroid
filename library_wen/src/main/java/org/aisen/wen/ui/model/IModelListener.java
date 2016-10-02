package org.aisen.wen.ui.model;

import org.aisen.wen.component.network.task.TaskException;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IModelListener<Progress, Result extends Serializable> {

    void onPrepare();

    void onProgressUpdate(Progress... values);

    void onSuccess(Result result);

    void onFailure(TaskException e);

    void onFinished();

}
