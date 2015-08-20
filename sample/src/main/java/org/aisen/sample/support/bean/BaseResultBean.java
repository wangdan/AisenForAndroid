package org.aisen.sample.support.bean;

import org.aisen.android.network.biz.IResult;

import java.io.Serializable;

/**
 * Created by wangdan on 15/8/20.
 */
public class BaseResultBean implements IResult, Serializable {

    private boolean cache;// 是否是缓存数据

    private boolean _expired;

    private boolean _noMore;

    private String[] pagingIndex = new String[0];

    @Override
    public boolean expired() {
        return _expired;
    }

    public void setExpired(boolean expired) {
        this._expired = expired;
    }

    @Override
    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override
    public boolean noMore() {
        return _noMore;
    }

    public void setNoMore(boolean noMore) {
        this._noMore = noMore;
    }

    @Override
    public String[] pagingIndex() {
        return pagingIndex;
    }

    public void setPagingIndex(String[] pagingIndex) {
        this.pagingIndex = pagingIndex;
    }

}
