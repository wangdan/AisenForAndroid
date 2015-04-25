package com.example.aisensample.support.bean;

import com.m.network.biz.IResult;

import java.io.Serializable;

/**
 * Created by wangdan on 15/4/24.
 */
public class BaseBean implements IResult, Serializable {

    @Override
    public boolean expired() {
        return false;
    }

    @Override
    public boolean isCache() {
        return false;
    }

    @Override
    public boolean noMore() {
        return false;
    }

    @Override
    public String[] pagingIndex() {
        return new String[0];
    }
}
