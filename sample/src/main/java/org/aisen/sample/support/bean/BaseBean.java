package org.aisen.sample.support.bean;

import org.aisen.wen.component.network.biz.IResult;

import java.io.Serializable;

/**
 * Created by wangdan on 15/4/24.
 */
public class BaseBean implements IResult, Serializable {

    private static final long serialVersionUID = 5953570312045069353L;

    @Override
    public boolean outofdate() {
        return false;
    }

    @Override
    public boolean fromCache() {
        return false;
    }

    @Override
    public boolean endPaging() {
        return false;
    }

    @Override
    public String[] pagingIndex() {
        return new String[0];
    }

}
