package org.aisen.wen.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aisen.wen.component.bitmaploader.core.BitmapOwner;
import org.aisen.wen.ui.activity.BaseActivity;
import org.aisen.wen.ui.view.IView;

import butterknife.ButterKnife;

/**
 * Created by wangdan on 16/10/10.
 */
public abstract class ABaseFragment extends Fragment implements BitmapOwner, IView {

    private View contentView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseActivity)
            ((BaseActivity) context).addFragment(toString(), this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity() != null && getActivity() instanceof BaseActivity)
            ((BaseActivity) getActivity()).removeFragment(this.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (setLayoutId() > 0) {
            contentView = inflater.inflate(setLayoutId(), null);
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return contentView;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Action的home被点击了
     *
     * @return
     */
    public boolean onHomeClick() {
        return onBackClick();
    }

    /**
     * 返回按键被点击了
     *
     * @return
     */
    public boolean onBackClick() {
        return false;
    }

    public View findViewById(int viewId) {
        if (getContentView() == null)
            return null;

        return getContentView().findViewById(viewId);
    }

    @Override
    public void setViewContext(Activity context) {

    }

    @Override
    public Activity getViewContext() {
        return getActivity();
    }

    @Override
    public View getContentView() {
        return contentView;
    }

    @Override
    public void bindView(View contentView, Bundle savedInstanceState) {
        ButterKnife.bind(this, contentView);
    }

    @Override
    public void bindEvent(View contentView, Bundle savedInstanceState) {

    }

    @Override
    public boolean canDisplay() {
        return true;
    }

    public int setLayoutId() {
        return -1;
    }

}
