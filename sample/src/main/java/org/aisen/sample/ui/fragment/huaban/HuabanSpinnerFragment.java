package org.aisen.sample.ui.fragment.huaban;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.ui.fragment.ABaseFragment;
import org.aisen.huaban.R;
import org.aisen.sample.ui.activity.MainActivity;

/**
 * 花瓣的Spinner
 *
 * Created by wangdan on 15/9/10.
 */
public class HuabanSpinnerFragment extends ABaseFragment implements MainActivity.MainSpinnerNavigation {

    public static HuabanSpinnerFragment newInstance() {
        return new HuabanSpinnerFragment();
    }

    public static final HuabanFragment.Category[] HUABAN_CATEGORIES = new HuabanFragment.Category[] {
            HuabanFragment.Category.beauty, HuabanFragment.Category.home, HuabanFragment.Category.travel_places, HuabanFragment.Category.food_drink,
            HuabanFragment.Category.diy_crafts, HuabanFragment.Category.wedding_events, HuabanFragment.Category.modeling_hair, HuabanFragment.Category.men,
            HuabanFragment.Category.illustration, HuabanFragment.Category.design, HuabanFragment.Category.architecture, HuabanFragment.Category.pets,
            HuabanFragment.Category.kids, HuabanFragment.Category.photography, HuabanFragment.Category.art, HuabanFragment.Category.film_music_books,
            HuabanFragment.Category.tips
    };

    HuabanFragment mFragment;

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
        return GlobalContext.getInstance().getResources().getStringArray(R.array.huaban_categories);
    }

    @Override
    public int initPosition() {
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mFragment != null && mFragment.getCategory().equals(HUABAN_CATEGORIES[position])) {
            return;
        }

        mFragment = HuabanFragment.newInstance(HUABAN_CATEGORIES[position]);

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
