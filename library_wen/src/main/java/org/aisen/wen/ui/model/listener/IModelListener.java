package org.aisen.wen.ui.model.listener;

import java.io.Serializable;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IModelListener<Result extends Serializable> {

    void onPrepare(ModelListenerParam<Result> param);

    void onSuccess(ModelListenerParam<Result> param);

    void onFailure(ModelListenerParam<Result> param);

    void onFinished(ModelListenerParam<Result> param);

}
