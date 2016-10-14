package org.aisen.wen.ui.presenter;

import android.view.ViewGroup;

import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.view.IPaingView;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/11.
 */
public interface IPagingPresenter<Item extends Serializable,// Item实体
                                  Result extends Serializable,// 接口实体
                                  Header extends Serializable,// Header实体
                                  RefreshView extends ViewGroup,// 列表控件
                                  Model extends IModel<Result>,// Model
                                  View extends IPaingView<Item, Result, Header, RefreshView>>// View
                        extends IContentPresenter<Result, Model, View> {

    enum RefreshMode {
        /**
         * 重设数据
         */
        reset,
        /**
         * 上拉，加载更多
         */
        update,
        /**
         * 下拉，刷新最新
         */
        refresh
    }

    IPaging<Item, Result> newPaging();

    IPaging<Item, Result> getPaging();

}
