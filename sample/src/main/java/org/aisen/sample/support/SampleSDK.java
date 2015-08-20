package org.aisen.sample.support;

import org.aisen.android.common.setting.Setting;
import org.aisen.android.common.utils.Consts;
import org.aisen.android.network.biz.ABizLogic;
import org.aisen.android.network.http.HttpConfig;
import org.aisen.android.network.http.Params;
import org.aisen.android.network.task.TaskException;
import org.aisen.sample.support.bean.HuabanPins;

/**
 * SDK
 */
public class SampleSDK extends ABizLogic {

    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.baseUrl = getSetting(Consts.BASE_URL).getValue();
        return httpConfig;
    }

    private SampleSDK() {
        super();
    }

    private SampleSDK(CacheMode cacheMode) {
        super(cacheMode);
    }

    public static SampleSDK getInstance() {
        return new SampleSDK();
    }

    public static SampleSDK getInstance(CacheMode cacheMode) {
        return new SampleSDK(cacheMode);
    }

    public HuabanPins getHuabanFavorite(String category, long maxId, int limit) throws TaskException {
        Params params = new Params();
        if (maxId > 0)
            params.addParameter("max", maxId + "");
        params.addParameter("limit", limit + "");

        Setting action = getSetting("getHuabanFavorite").copy();
        action.setValue(action.getValue() + category);

        return doGet(configHttpConfig(), action, params, HuabanPins.class);
    }

}
