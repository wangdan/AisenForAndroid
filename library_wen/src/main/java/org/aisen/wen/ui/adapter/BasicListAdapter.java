package org.aisen.wen.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.aisen.wen.R;
import org.aisen.wen.ui.itemview.IITemView;
import org.aisen.wen.ui.itemview.IItemViewCreator;
import org.aisen.wen.ui.view.IPaingView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 1、支持能够使用BaseAdapter的控件
 * 2、支持ViewType，默认是Normal Type
 *
 * @param <Item>
 */
public class BasicListAdapter<Item extends Serializable> extends BaseAdapter implements IPagingAdapter {

    private IPaingView holderFragment;
    private IItemViewCreator<Item> itemViewCreator;
    private ArrayList<Item> datas;

    public BasicListAdapter(IPaingView holderFragment, ArrayList<Item> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        this.holderFragment = holderFragment;
        this.itemViewCreator = holderFragment.newItemViewCreator();
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        Item t = getDatas().get(position);
        if (t instanceof ItemTypeData) {
            return ((ItemTypeData) t).itemType();
        }

        return IPagingAdapter.TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IITemView<Item> itemView;

        if (convertView == null) {
            int itemType = getItemViewType(position);

            convertView = itemViewCreator.newContentView(holderFragment.getContext().getLayoutInflater(), parent, itemType);

            itemView = itemViewCreator.newItemView(convertView, itemType);
            itemView.onBindView(convertView);

            convertView.setTag(R.id.itemview, itemView);
        } else {
            itemView = (IITemView<Item>) convertView.getTag(R.id.itemview);
        }

        itemView.reset(datas.size(), position);

        itemView.onBindData(convertView, datas.get(position), position);

        return convertView;
    }

    @Override
    public ArrayList<Item> getDatas() {
        return datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
