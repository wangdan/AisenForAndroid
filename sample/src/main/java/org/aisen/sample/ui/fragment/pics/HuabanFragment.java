package org.aisen.sample.ui.fragment.pics;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.aisensample.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.aisen.android.common.utils.SystemUtils;
import org.aisen.android.network.task.TaskException;
import org.aisen.android.support.adapter.ABaseAdapter;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.support.paging.IPaging;
import org.aisen.android.ui.fragment.AWaterfallSwipeRefreshFragment;
import org.aisen.android.ui.widget.pla.PLAImageView;
import org.aisen.sample.support.HuabanPaging;
import org.aisen.sample.support.SampleSDK;
import org.aisen.sample.support.bean.HuabanPin;
import org.aisen.sample.support.bean.HuabanPins;

import java.util.List;

/**
 * 花瓣图片
 */
public class HuabanFragment extends AWaterfallSwipeRefreshFragment<HuabanPin, HuabanPins> {

    public static HuabanFragment newInstance(Category category) {
        HuabanFragment fragment = new HuabanFragment();

        Bundle args  = new Bundle();
        args.putString("category", category.toString());
        fragment.setArguments(args);

        return fragment;
    }

    public enum Category {
        beauty // 美女
    }

    private Category category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            category = Category.valueOf(getArguments().getString("category"));
        }
        else {
            category = Category.valueOf(savedInstanceState.getString("category"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("category", category.toString());
    }

    @Override
    protected IPaging<HuabanPin, HuabanPins> configPaging() {
        return new HuabanPaging();
    }

    @Override
    protected ABaseAdapter.AbstractItemView<HuabanPin> newItemView() {
        return new HuabanItemView();
    }

    @Override
    protected void requestData(RefreshMode mode) {
        if (mode == RefreshMode.refresh)
            mode = RefreshMode.reset;

        new HuabanTask(mode).execute();
    }

    class HuabanItemView extends ABaseAdapter.AbstractItemView<HuabanPin> {

        @ViewInject(id = R.id.img)
        PLAImageView img;

        @Override
        public int inflateViewId() {
            return R.layout.item_huaban;
        }

        @Override
        public void bindingData(View convertView, HuabanPin data) {
            String image = String.format("http://img.hb.aicdn.com/%s_fw192w", data.getFile().getKey());

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                                                    .showImageOnLoading(R.drawable.comm_loading)
                                                    .build();
            ImageLoader.getInstance().displayImage(image, img, options);

            int origW = data.getFile().getWidth();
            int origH = data.getFile().getHeight();

            int screenW = SystemUtils.getScreenWidth();

            int imgW = screenW / 2;
            int imgH = Math.round(imgW * 1.0f / origW * origH);

            img.setImageWidth(imgW);
            img.setImageHeight(imgH);
        }

    }

    class HuabanTask extends PagingTask<Void, Void, HuabanPins> {

        public HuabanTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<HuabanPin> parseResult(HuabanPins huabanPins) {
            return huabanPins.getPins();
        }

        @Override
        protected HuabanPins workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... params) throws TaskException {
            int limit = 20;
            long max = 0;
            if (!TextUtils.isEmpty(nextPage) && mode == RefreshMode.update)
                max = Long.parseLong(nextPage);

            return SampleSDK.getInstance().getHuabanFavorite(category.toString(), max, limit);
        }

    }

}
