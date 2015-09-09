package org.aisen.sample.support.paging;

import org.aisen.android.support.paging.IPaging;
import org.aisen.sample.support.bean.HuabanPin;
import org.aisen.sample.support.bean.HuabanPins;

/**
 * Created by wangdan on 15/8/20.
 */
public class HuabanPaging implements IPaging<HuabanPin, HuabanPins> {

    private long maxId;

    @Override
    public void processData(HuabanPins newDatas, HuabanPin firstData, HuabanPin lastData) {
        if (lastData != null)
            maxId = lastData.getPin_id();
    }

    @Override
    public String getPreviousPage() {
        return null;
    }

    @Override
    public String getNextPage() {
        return String.valueOf(maxId);
    }

}
