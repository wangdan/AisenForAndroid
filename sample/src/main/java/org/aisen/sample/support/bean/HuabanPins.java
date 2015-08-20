package org.aisen.sample.support.bean;

import java.util.List;

/**
 * Created by wangdan on 15/8/20.
 */
public class HuabanPins extends BaseResultBean {

    private List<HuabanPin> pins;

    public List<HuabanPin> getPins() {
        return pins;
    }

    public void setPins(List<HuabanPin> pins) {
        this.pins = pins;
    }

}
