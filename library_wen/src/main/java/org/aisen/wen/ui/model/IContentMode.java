package org.aisen.wen.ui.model;

import org.aisen.wen.component.network.task.TaskException;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/10.
 */
public interface IContentMode<Result extends Serializable> extends IModel<Result> {

    boolean resultIsEmpty(Result result);

    Result workInBackground(Void... params) throws TaskException;

}
