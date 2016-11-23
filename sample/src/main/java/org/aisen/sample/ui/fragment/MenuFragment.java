package org.aisen.sample.ui.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.aisensample.R;

import org.aisen.android.ui.fragment.AListFragment;
import org.aisen.android.ui.fragment.adapter.ARecycleViewItemView;
import org.aisen.android.ui.fragment.itemview.IITemView;
import org.aisen.android.ui.fragment.itemview.IItemViewCreator;
import org.aisen.sample.support.bean.MenuBean;
import org.aisen.sample.ui.activity.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by wangdan on 15/4/23.
 */
public class MenuFragment extends AListFragment<MenuBean, ArrayList<MenuBean>, Serializable> {

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    protected void setupRefreshConfig(RefreshConfig config) {
        super.setupRefreshConfig(config);

        config.footerMoreEnable = false;
    }

    @Override
    public IItemViewCreator<MenuBean> configItemViewCreator() {
        return new IItemViewCreator<MenuBean>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(R.layout.item_main, parent, false);
            }

            @Override
            public IITemView<MenuBean> newItemView(View convertView, int viewType) {
                return new MainItemView(getActivity(), convertView);
            }

        };
    }

    @Override
    public void requestData(RefreshMode mode) {
        setItems(generateItems());

        onItemClick(getRefreshView(), null, 0, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity) getActivity()).onMenuSelected(getAdapterItems().get(position));
    }

    private ArrayList<MenuBean> generateItems() {
        ArrayList<MenuBean> items = new ArrayList<MenuBean>();

        items.add(new MenuBean(0, R.string.a_base_fragment, R.string.a_base_fragment, "0"));

        return items;
    }

    class MainItemView extends ARecycleViewItemView<MenuBean> {

        @BindView(R.id.txtTitle)
        TextView txtTitle;

        public MainItemView(Activity context, View itemView) {
            super(context, itemView);
        }

        @Override
        public void onBindData(View convertView, MenuBean data, int position) {
            txtTitle.setText(data.getTitleRes());
        }

    }

}
