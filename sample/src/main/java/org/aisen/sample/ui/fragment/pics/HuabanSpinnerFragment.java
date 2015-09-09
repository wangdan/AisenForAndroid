package org.aisen.sample.ui.fragment.pics;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.ui.fragment.ABaseFragment;
import org.aisen.huaban.R;
import org.aisen.sample.ui.activity.MainActivity;

/**
 * Created by wangdan on 15/9/10.
 */
public class HuabanSpinnerFragment extends ABaseFragment implements MainActivity.MainSpinnerNavigation {

    public static HuabanSpinnerFragment newInstance() {
        return new HuabanSpinnerFragment();
    }

    ABaseFragment mFragment;

    @Override
    protected int inflateContentView() {
        return R.layout.ui_find_main;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null)
            onItemSelected(null, null, initPosition(), initPosition());
    }

    @Override
    public String[] generateItems() {
        return GlobalContext.getInstance().getResources().getStringArray(R.array.categories);
    }

    @Override
    public int initPosition() {
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mFragment = HuabanFragment.newInstance(HuabanFragment.Category.beauty);

        getActivity().getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layFindMain, mFragment, "FindMainFragment")
                        .commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mFragment != null)
            getFragmentManager().beginTransaction().remove(mFragment).commit();
    }

}
