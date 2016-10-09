package org.aisen.wen.ui.view.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.aisen.wen.R;
import org.aisen.wen.R2;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.presenter.impl.AContentPresenter;
import org.aisen.wen.ui.view.ABridgeView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangdan on 16/9/30.
 */
public abstract class AContentView extends ABridgeView {

    static final String TAG = "AContentView";

    private View mContentView;
    @BindView(R2.id.layoutLoading)
    View loadingLayout;// 加载中视图
    @BindView(R2.id.layoutLoadFailed)
    View loadFailureLayout;// 加载失败视图
    @BindView(R2.id.layoutContent)
    View contentLayout;// 内容视图
    @BindView(R2.id.layoutEmpty)
    View emptyLayout;// 空视图

    // 标志是否ContentView是否为空
    private boolean contentEmpty = true;

    private AContentPresenter mPresenter;

    @Override
    public void onBridgeCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onBridgeCreateView(inflater, container, savedInstanceState);

        mContentView = inflater.inflate(contentViewResId(), null);
    }

    @Override
    public void bindView(View contentView) {
        super.bindView(contentView);

        ButterKnife.bind(this, contentView);
    }

    @Override
    public void bindEvent(View contentView) {
        super.bindEvent(contentView);

        if (loadFailureLayout != null) {
            View reloadView = loadFailureLayout.findViewById(R.id.layoutReload);
            if (reloadView != null) {
                reloadView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter != null) {
                            mPresenter.requestData();
                        }
                    }
                });
            }
        }
        if (emptyLayout != null) {
            View reloadView = emptyLayout.findViewById(R.id.layoutReload);
            if (reloadView != null) {
                reloadView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter != null) {
                            mPresenter.requestData();
                        }
                    }
                });
            }
        }
    }

    public View findViewById(int id) {
        return getContentView().findViewById(id);
    }

    public boolean isContentLayoutEmpty() {
        return contentEmpty;
    }

    public void setContentLayoutEmpty(boolean empty) {
        this.contentEmpty = empty;
    }

    public void setLoadingLayoutVisibility(int visibility) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(visibility);
        }
    }

    public void setEmptyLayoutVisibility(int visibility) {
        if (emptyLayout != null) {
            emptyLayout.setVisibility(visibility);
        }
    }

    public void setContentLayoutVisibility(int visibility) {
        if (contentLayout != null) {
            contentLayout.setVisibility(visibility);
        }
    }

    public void setFailureLayoutVisibility(int visibility, TaskException e) {
        if (loadFailureLayout != null) {
            loadFailureLayout.setVisibility(visibility);

            if (e != null) {
                TextView txtLoadFailed = (TextView) loadFailureLayout.findViewById(R.id.txtLoadFailed);
                if (txtLoadFailed != null)
                    txtLoadFailed.setText(e.getMessage());
            }

            setEmptyLayoutVisibility(View.GONE);
        } else {
            setEmptyLayoutVisibility(View.VISIBLE);
        }
    }

    @Override
    public View getContentView() {
        return mContentView;
    }

    public void setPresenter(AContentPresenter presenter) {
        mPresenter = presenter;
    }

    /**
     * ContentView的ID
     *
     * @return
     */
    abstract public int contentViewResId();

}
