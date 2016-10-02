package org.aisen.wen.ui.view;

import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.ILifecycleBridge;

/**
 * Created by wangdan on 16/9/30.
 */
public interface IContentView extends IView, ILifecycleBridge {

    void setLoadingLayoutVisibility(int visibility);

    void setEmptyLayoutVisibility(int visibility);

    void setContentLayoutVisibility(int visibility);

    void setFailureLayoutVisibility(int visibility, TaskException e);

    boolean isContentLayoutEmpty();

    void setContentLayoutEmpty(boolean empty);

}
