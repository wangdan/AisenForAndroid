package org.aisen.sample.support.sdk;

import org.aisen.android.common.setting.Setting;
import org.aisen.android.network.http.Params;
import org.aisen.android.network.task.TaskException;
import org.aisen.sample.support.sdk.bean.VideoStreamsBean;

/**
 * Youtube SDK
 *
 * Created by wangdan on 16/10/7.
 */
public class YoutubeSDK extends BaseSDK {

    private YoutubeSDK() {

    }

    public static YoutubeSDK newInstance() {
        return new YoutubeSDK();
    }

    public VideoStreamsBean getVideoStreams(double maxBehotTime) throws TaskException {
        Setting action = newSetting("getVideoStreams", "/api/237/stream", "获取Video列表");

        Params params = new Params();
        if (maxBehotTime > 0D) {
            params.addParameter("max_behot_time", Double.toString(maxBehotTime));
        }
        else {
            params.addParameter("min_behot_time", Double.toString(System.currentTimeMillis() / 1000));
        }
        params.addParameter("category", "13");
        params.addParameter("category_parameter", "");
        params.addParameter("count", "20");

        return doGet(action, basicParams(params), VideoStreamsBean.class);
    }

}
