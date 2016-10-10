package org.aisen.wen.ui.presenter;

import org.aisen.wen.component.network.task.ITaskManager;

/**
 * 根据Mode的回调方法，自动切换4种视图的切换
 * 管理Task的生命周期
 *
 */
public interface IContentPresenter extends IPresenter, ITaskManager {

    /**
     * 缓存数据过期时，再次请求数据
     */
    void requestDataOutofdate();

}
