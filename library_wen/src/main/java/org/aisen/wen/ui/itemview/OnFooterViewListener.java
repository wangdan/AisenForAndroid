package org.aisen.wen.ui.itemview;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;
import org.aisen.wen.ui.presenter.impl.APagingPresenter;

/**
 * Created by wangdan on 16/1/9.
 */
public interface OnFooterViewListener {

    void onTaskStateChanged(AFooterItemView<?> footerItemView, AContentPresenter.TaskState state, TaskException exception, APagingPresenter.RefreshMode mode);

    void setFooterViewToRefreshing();

}
