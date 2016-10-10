package org.aisen.sample.ui.fragment;

import android.app.Fragment;

import com.example.aisensample.R;

import org.aisen.sample.support.sdk.YoutubeSDK;
import org.aisen.sample.support.sdk.bean.VideoStreamsBean;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.fragment.AContentFragment;
import org.aisen.wen.ui.model.IContentMode;
import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.view.IContentView;
import org.aisen.wen.ui.view.impl.AContentView;

/**
 * Created by wangdan on 15/4/23.
 */
public class BaseFragmentSample extends AContentFragment<VideoStreamsBean, IContentMode<VideoStreamsBean>, IContentView> {

    public static Fragment newInstance() {
        return new BaseFragmentSample();
    }


    @Override
    public IContentView newContentView() {
        return new AContentView() {

            @Override
            public int setLayoutId() {
                return R.layout.ui_a_base;
            }
        };
    }

    @Override
    public IContentMode<VideoStreamsBean> newContentMode() {
        return new AContentModel<VideoStreamsBean>() {

            @Override
            public VideoStreamsBean workInBackground(Void... params) throws TaskException {
                return YoutubeSDK.newInstance().getVideoStreams(0l);
            }

        };
    }

}
