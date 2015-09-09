package org.aisen.sample.support.bean;

import org.aisen.android.network.biz.IResult;

import java.io.Serializable;

/**
 * Created by wangdan on 15/8/20.
 */
public class BaseResultBean implements IResult, Serializable {

    private boolean fromCache;// 是否是缓存数据

    private boolean outofDate;

    private boolean endPaging;

    private String[] pagingIndex = new String[2];

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }

    public boolean isOutofDate() {
        return outofDate;
    }

    public void setOutofDate(boolean outofDate) {
        this.outofDate = outofDate;
    }

    public boolean isEndPaging() {
        return endPaging;
    }

    public void setEndPaging(boolean endPaging) {
        this.endPaging = endPaging;
    }

    public String[] getPagingIndex() {
        return pagingIndex;
    }

    public void setPagingIndex(String[] pagingIndex) {
        this.pagingIndex = pagingIndex;
    }

    @Override
    public boolean outofdate() {
        return isOutofDate();
    }

    @Override
    public boolean fromCache() {
        return isFromCache();
    }

    @Override
    public boolean endPaging() {
        return isEndPaging();
    }

    @Override
    public String[] pagingIndex() {
        return getPagingIndex();
    }

}
