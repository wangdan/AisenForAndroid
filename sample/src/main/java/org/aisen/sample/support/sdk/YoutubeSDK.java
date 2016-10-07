package org.aisen.sample.support.sdk;

import org.aisen.sample.support.sdk.bean.VideoStreamsBean;
import org.aisen.wen.component.network.http.Params;
import org.aisen.wen.component.network.setting.Setting;
import org.aisen.wen.component.network.task.TaskException;

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
            params.add("max_behot_time", Double.toString(maxBehotTime));
        }
        else {
            params.add("min_behot_time", Double.toString(System.currentTimeMillis() / 1000));
        }
        params.add("category", "13");
        params.add("category_parameter", "");
        params.add("count", "20");

        return doGet(action, basicParams(params), VideoStreamsBean.class);
    }

}
