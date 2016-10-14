package org.aisen.wen.ui.presenter;

import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.view.IView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/9.
 */
public interface IPresenter<Result extends Serializable, PModel extends IModel<Result>, PView extends IView> {

    /**
     * 请求数据
     *
     */
    void requestData();

    PModel getPModel();

    PView getPView();

}
