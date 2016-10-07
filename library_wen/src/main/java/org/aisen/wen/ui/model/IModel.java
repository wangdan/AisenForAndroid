package org.aisen.wen.ui.model;

import android.os.Process;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IModel<Progress, Result extends Serializable> {

    void bindCallback(IModelListener<Progress, Result> listener);

    IModelListener<Process, Result> getCallback();

    void execute();

}
