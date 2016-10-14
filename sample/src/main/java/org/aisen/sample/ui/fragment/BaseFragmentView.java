package org.aisen.sample.ui.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aisensample.R;

import org.aisen.sample.support.sdk.bean.VideoStreamBean;
import org.aisen.sample.support.sdk.bean.VideoStreamsBean;
import org.aisen.wen.ui.adapter.ARecycleViewItemView;
import org.aisen.wen.ui.itemview.IITemView;
import org.aisen.wen.ui.itemview.IItemViewCreator;
import org.aisen.wen.ui.view.impl.ARecycleView;

import java.io.Serializable;

import butterknife.BindView;

/**
 * Created by wangdan on 16/10/13.
 */
public class BaseFragmentView extends ARecycleView<VideoStreamBean, VideoStreamsBean, Serializable> {

    @Override
    public IItemViewCreator<VideoStreamBean> newItemViewCreator() {
        return new IItemViewCreator<VideoStreamBean>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(R.layout.item_youtube, parent, false);
            }

            @Override
            public IITemView<VideoStreamBean> newItemView(View convertView, int viewType) {
                return new RecycleViewItemView(getViewContext(), convertView);
            }

        };
    }

    class RecycleViewItemView extends ARecycleViewItemView<VideoStreamBean> {

        @BindView(R.id.txtTitle)
        TextView txtTitle;

        public RecycleViewItemView(Activity context, View itemView) {
            super(context, itemView);
        }

        @Override
        public void onBindData(View convertView, VideoStreamBean data, int position) {
            txtTitle.setText(data.getTitle());
        }

    }

}
