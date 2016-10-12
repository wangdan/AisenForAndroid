package org.aisen.wen.ui.model;

import org.aisen.wen.ui.model.listener.IModelListener;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IModel<Result extends Serializable> {

    void setCallback(IModelListener<Result> listener);

    IModelListener<Result> getCallback();

    void execute();

    boolean isRunning();

}
