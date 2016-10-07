package org.aisen.sample.support.sdk;

import org.aisen.wen.component.network.biz.ABizLogic;
import org.aisen.wen.component.network.http.HttpConfig;
import org.aisen.wen.component.network.http.Params;
import org.aisen.wen.component.network.task.TaskException;

/**
 * Created by wangdan on 16/10/7.
 */
class BaseSDK extends ABizLogic {

    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig config = new HttpConfig();

        config.baseUrl = "http://i.isnssdk.com";

        return config;
    }

    /**
     * 基础参数
     *
     * @param params
     * @return
     * @throws TaskException
     */
    Params basicParams(Params params) throws TaskException {
        if (params == null)
            params = new Params();

        // http://i.isnssdk.com/api/237/stream?category=13&category_parameter=&count=20&
        // fb_large_ad=1&fb_small_ad=1&max_behot_time=1.475827499E9&loc_mode=7&loc_time=1475826578&
        // latitude=22.584909&longitude=113.876446&city=%E6%B7%B1%E5%9C%B3%E5%B8%82&country=CN&lac=9551&
        // cid=101720578&iid=6333188128764856067&device_id=6333188127042733572&openudid=6debc8b84b6e52e2&
        // ac=WIFI&channel=gp&aid=1106&app_version=2.3.7&device_platform=android&
        // device_type=TCL+950&os=android&os_api=23&os_version=6.0.1&uuid=860846030054717&
        // tz_offset=28800&tz_name=Asia%2FShanghai&sys_language=zh&sys_region=CN&
        // language=en&region=US&sim_region=cn&youtube=0&original_channel=gp
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");
        params.add("", "");

        return params;
    }

}
