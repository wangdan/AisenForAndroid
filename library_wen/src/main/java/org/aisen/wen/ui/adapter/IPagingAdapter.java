package org.aisen.wen.ui.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * APagingFragment的Adapter
 *
 * Created by wangdan on 16/1/4.
 */
public interface IPagingAdapter<Item extends Serializable> {

    int TYPE_NORMAL = 1000;

    int TYPE_FOOTER = 2000;

    ArrayList<Item> getDatas();

    void notifyDataSetChanged();

    class Utils {

        public static <Item extends Serializable> void setDatasAndRefresh(IPagingAdapter<Item> adapter, ArrayList<Item> datas) {
            adapter.getDatas().clear();
            adapter.getDatas().addAll(datas);
            
            adapter.notifyDataSetChanged();
        }

        public static <Item extends Serializable> void addItem(IPagingAdapter<Item> adapter, Item entry) {
            adapter.getDatas().add(entry);
        }

        public static <Item extends Serializable> void addItemAndRefresh(IPagingAdapter<Item> adapter, Item entry) {
            addItem(adapter, entry);
            
            adapter.notifyDataSetChanged();
        }

        public static <Item extends Serializable> void addItems(IPagingAdapter<Item> adapter, List<Item> entries) {
            adapter.getDatas().addAll(entries);
        }

        public static <Item extends Serializable> void addItemsAndRefresh(IPagingAdapter<Item> adapter, List<Item> entries) {
            addItems(adapter, entries);
            adapter.notifyDataSetChanged();
        }

        public static <Item extends Serializable> void addItem(IPagingAdapter<Item> adapter, Item entry, int to) {
            adapter.getDatas().add(to, entry);
        }

        public static <Item extends Serializable> void addItemsAndRefresh(IPagingAdapter<Item> adapter, Item entry, int to) {
            addItem(adapter, entry, to);
            
            adapter.notifyDataSetChanged();
        }

        public static <Item extends Serializable> void addItemAtFront(IPagingAdapter<Item> adapter, Item entry) {
            adapter.getDatas().add(0, entry);
        }

        public static <Item extends Serializable> void addItemAtFrontAndRefresh(IPagingAdapter<Item> adapter, Item entry) {
            addItemAtFront(adapter, entry);
            
            adapter.notifyDataSetChanged();
        }

        public static <Item extends Serializable> void addItemsAtFront(IPagingAdapter<Item> adapter, List<Item> entries) {
            for (int i = entries.size() - 1; i >= 0; i--) {
                adapter.getDatas().add(0, entries.get(i));
            }
        }

        public static <Item extends Serializable> void addItemsAtFrontAndRefresh(IPagingAdapter<Item> adapter, List<Item> entries) {
            addItemsAtFront(adapter, entries);
            
            adapter.notifyDataSetChanged();
        }
        
    }

    interface ItemTypeData {

        int itemType();

    }

}
