package org.aisen.wen.ui.presenter;

import org.aisen.wen.ui.model.IModelListener;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IPresenter<Progress, Result extends Serializable> extends IModelListener<Progress, Result> {

}
