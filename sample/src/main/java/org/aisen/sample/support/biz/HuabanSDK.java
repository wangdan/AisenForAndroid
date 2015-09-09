package org.aisen.sample.support.biz;

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
public class HuabanSDK extends ABizLogic {

    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.baseUrl = getSetting(Consts.BASE_URL).getValue();
        return httpConfig;
    }

    private HuabanSDK() {
        super();
    }

    private HuabanSDK(CacheMode cacheMode) {
        super(cacheMode);
    }

    public static HuabanSDK getInstance() {
        return new HuabanSDK();
    }

    public static HuabanSDK getInstance(CacheMode cacheMode) {
        return new HuabanSDK(cacheMode);
    }

    /**
     * 获取花瓣分类图片
     *
     * @param category 分类
     * @param maxId 分页id
     * @param limit 加载数量
     * @return
     * @throws TaskException
     */
    public HuabanPins getHuabanFavorite(String category, long maxId, int limit) throws TaskException {
        Params params = new Params();
        if (maxId > 0)
            params.addParameter("max", maxId + "");
        params.addParameter("limit", limit + "");

        Setting action = getSetting("getHuabanFavorite").copy();
        action.setValue(action.getValue() + category);
        action.getExtras().put("category", newSettingExtra("category", category, ""));

        return doGet(action, params, HuabanPins.class);
    }

}
