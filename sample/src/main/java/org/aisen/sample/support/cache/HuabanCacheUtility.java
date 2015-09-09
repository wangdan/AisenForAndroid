package org.aisen.sample.support.cache;

import org.aisen.android.common.setting.Setting;
import org.aisen.android.common.setting.SettingExtra;
import org.aisen.android.common.utils.CacheTimeUtils;
import org.aisen.android.common.utils.Logger;
import org.aisen.android.network.cache.ICacheUtility;
import org.aisen.android.network.http.Params;
import org.aisen.orm.extra.Extra;
import org.aisen.sample.support.bean.HuabanPin;
import org.aisen.sample.support.bean.HuabanPins;
import org.aisen.sample.support.db.HuabanDB;

import java.util.List;

/**
 * Created by wangdan on 15/8/27.
 */
public class HuabanCacheUtility implements ICacheUtility {

    static final String KEY_CACHE = "huaban_category";

    static final long KEY_CACHE_TIME = 50 * 1000;

    @Override
    public <T> Cache<T> findCacheData(Setting action, Params params, Class<T> responseCls) {
        SettingExtra categoryExtra = action.getExtras().get("category");

        String category = categoryExtra.getValue();

        List<HuabanPin> beanList = HuabanDB.getDB().select(null, HuabanPin.class);
        if (beanList.size() > 0) {
            HuabanPins beans = new HuabanPins();
            beans.setFromCache(true);
            beans.setOutofDate(CacheTimeUtils.isOutofdate(KEY_CACHE, KEY_CACHE_TIME));
            beans.setPins(beanList);

            Logger.d(Logger.TAG, "返回缓存花瓣数据%d条", beanList.size());

            return new Cache<>((T) beans, false);
        }

        return null;
    }

    @Override
    public void addCacheData(Setting action, Params params, Object responseObj) {
        SettingExtra categoryExtra = action.getExtras().get("category");

        String category = categoryExtra.getValue();

        HuabanPins beans = (HuabanPins) responseObj;

        if (!params.containsKey("max")) {
            HuabanDB.getDB().deleteAll(new Extra(null, category), HuabanPin.class);

            Logger.d(Logger.TAG, "清理花瓣数据，category[%s]", category);
        }

        HuabanDB.getDB().insert(null, beans.getPins());

        CacheTimeUtils.saveTime(KEY_CACHE);

        Logger.d(Logger.TAG, "插入花瓣数据%d条，category[%s]", beans.getPins().size(), category);
    }

}
