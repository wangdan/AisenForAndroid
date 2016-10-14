package org.aisen.wen.ui.presenter;

import org.aisen.wen.component.network.task.ITaskManager;
import org.aisen.wen.ui.model.IModel;
import org.aisen.wen.ui.view.IContentView;

import java.io.Serializable;

/**
 * 根据Mode的回调方法，自动切换4种视图的切换
 * 管理Task的生命周期
 *
 */
public interface IContentPresenter<Result extends Serializable, Model extends IModel<Result>, View extends IContentView>
                                            extends IPresenter<Result, Model, View>, ITaskManager {

    /**
     * ContentView对应Mode的四种状态
     */
    enum TaskState {
        prepare, falid, success, finished
    }

    /**
     * 缓存数据过期时，再次请求数据
     */
    void requestDataOutofdate();

}
