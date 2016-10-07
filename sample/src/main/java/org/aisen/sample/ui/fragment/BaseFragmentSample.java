package org.aisen.sample.ui.fragment;

import android.app.Fragment;

import com.example.aisensample.R;

import org.aisen.sample.support.sdk.YoutubeSDK;
import org.aisen.sample.support.sdk.bean.VideoStreamsBean;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.ui.fragment.ABaseFragment;
import org.aisen.wen.ui.model.impl.AContentModel;
import org.aisen.wen.ui.view.impl.AContentView;

/**
 * Created by wangdan on 15/4/23.
 */
public class BaseFragmentSample extends ABaseFragment<AContentModel<Void, VideoStreamsBean>, AContentView, Void, VideoStreamsBean> {

    public static Fragment newInstance() {
        return new BaseFragmentSample();
    }


    @Override
    public AContentView newContentView() {
        return new AContentView() {

            @Override
            public int contentViewResId() {
                return R.layout.ui_a_base;
            }

        };
    }

    @Override
    public AContentModel<Void, VideoStreamsBean> newContentMode() {
        return new AContentModel<Void, VideoStreamsBean>() {

            @Override
            protected VideoStreamsBean workInBackground(Void... params) throws TaskException {
                return YoutubeSDK.newInstance().getVideoStreams(0l);
            }

        };
    }

}
