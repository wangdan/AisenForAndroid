package org.aisen.wen.ui.view;

import android.view.View;

import org.aisen.wen.component.network.task.TaskException;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class IContentView extends ABridgeView {

    /**
     * ContentView的ID
     *
     * @return
     */
    abstract public int contentViewResId();

    abstract public View findViewById(int id);

    abstract public void setLoadingLayoutVisibility(int visibility);

    abstract public void setEmptyLayoutVisibility(int visibility);

    abstract public void setContentLayoutVisibility(int visibility);

    abstract public void setFailureLayoutVisibility(int visibility, TaskException e);

    /**
     * ContentView是否是Empty
     *
     * @return
     */
    abstract public boolean isContentLayoutEmpty();

    /**
     * 设置ContentView的Empty属性
     *
     * @param empty
     */
    abstract public void setContentLayoutEmpty(boolean empty);

}
