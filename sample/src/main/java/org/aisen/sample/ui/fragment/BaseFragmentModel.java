package org.aisen.sample.ui.fragment;

import org.aisen.sample.support.sdk.YoutubeSDK;
import org.aisen.sample.support.sdk.bean.VideoStreamBean;
import org.aisen.sample.support.sdk.bean.VideoStreamsBean;
import org.aisen.wen.component.network.task.TaskException;
import org.aisen.wen.support.paging.IPaging;
import org.aisen.wen.ui.model.impl.APagingModel;
import org.aisen.wen.ui.presenter.IPagingPresenter;

import java.util.List;

/**
 * Created by wangdan on 16/10/13.
 */
public class BaseFragmentModel extends APagingModel<VideoStreamBean, VideoStreamsBean> {

    @Override
    protected VideoStreamsBean workInBackground(IPagingPresenter.RefreshMode mode, IPaging<VideoStreamBean, VideoStreamsBean> paging) throws TaskException {
        return YoutubeSDK.newInstance().getVideoStreams(0l);
    }

    @Override
    public List<VideoStreamBean> parseResult(VideoStreamsBean videoStreamsBean) {
        return videoStreamsBean.getItems();
    }

}
