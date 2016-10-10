package org.aisen.wen.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.view.View;

import org.aisen.wen.ui.activity.base.BaseActivity;

/**
 * Created by wangdan on 16/10/10.
 */
public abstract class ABaseFragment extends Fragment {

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

    abstract public View getContentView();

}
