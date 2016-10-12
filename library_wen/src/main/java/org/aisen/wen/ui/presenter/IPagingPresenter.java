package org.aisen.wen.ui.presenter;

import org.aisen.wen.support.paging.IPaging;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/11.
 */
public interface IPagingPresenter<Item extends Serializable, Result extends Serializable> extends IContentPresenter {

    IPaging<Item, Result> newPaging();

    IPaging<Item, Result> getPaging();

}
