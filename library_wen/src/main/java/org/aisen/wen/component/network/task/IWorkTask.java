package org.aisen.wen.component.network.task;

/**
 * 执行线程
 *
 * Created by wangdan on 16/10/13.
 */
public interface IWorkTask {

    /**
     * 线程ID
     *
     * @return
     */
    String getTaskId();

    boolean cancel(boolean mayInterruptIfRunning);

}
