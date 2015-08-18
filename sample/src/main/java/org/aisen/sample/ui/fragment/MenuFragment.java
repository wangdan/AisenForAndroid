package org.aisen.sample.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.aisensample.R;
import org.aisen.sample.support.bean.MenuBean;
import org.aisen.sample.ui.activity.MainActivity;

import org.aisen.android.support.adapter.ABaseAdapter;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.ui.fragment.AListFragment;

import java.util.ArrayList;

/**
 * Created by wangdan on 15/4/23.
 */
public class MenuFragment extends AListFragment<MenuBean, ArrayList<MenuBean>> {

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    protected ABaseAdapter.AbstractItemView<MenuBean> newItemView() {
        return new MainItemView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity) getActivity()).onMenuSelected(getAdapterItems().get(position));
    }

    @Override
    protected void requestData(RefreshMode mode) {
        setItems(generateItems());

        onItemClick(getRefreshView(), null, 0, 0);
    }

    private ArrayList<MenuBean> generateItems() {
        ArrayList<MenuBean> items = new ArrayList<MenuBean>();

        items.add(new MenuBean(0, R.string.a_base_fragment, R.string.a_base_fragment, "0"));

        return items;
    }

    class MainItemView extends ABaseAdapter.AbstractItemView<MenuBean> {

        @ViewInject(id = R.id.txtTitle)
        TextView txtTitle;

        @Override
        public int inflateViewId() {
            return R.layout.item_main;
        }

        @Override
        public void bindingData(View convertView, MenuBean data) {
            txtTitle.setText(data.getTitleRes());
        }

    }

}
