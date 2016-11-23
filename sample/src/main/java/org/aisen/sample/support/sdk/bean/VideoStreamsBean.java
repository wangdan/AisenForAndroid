package org.aisen.sample.support.sdk.bean;


import org.aisen.android.support.bean.ResultBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangdan on 16/10/7.
 */
public class VideoStreamsBean extends ResultBean implements Serializable {

    private static final long serialVersionUID = 5085818113047864156L;

    private int login_status;

    private int total_number;

    private boolean has_more;

    private String tip;

    private int duration;

    private List<VideoStreamBean> items;

    public int getLogin_status() {
        return login_status;
    }

    public void setLogin_status(int login_status) {
        this.login_status = login_status;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<VideoStreamBean> getItems() {
        return items;
    }

    public void setItems(List<VideoStreamBean> items) {
        this.items = items;
    }

}
