package org.aisen.wen.ui.model;

import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangdan on 16/10/11.
 */
public interface IPagingModel<Item extends Serializable,
                              Result extends Serializable>
                        extends IModel<Result> {

    void execute(IPagingPresenter.RefreshMode mode, IPaging<Item, Result> paging);

    /**
     * 将Ts转换成List(T)
     *
     * @param result
     *            List(T)
     * @return
     */
    List<Item> parseResult(Result result);

}
