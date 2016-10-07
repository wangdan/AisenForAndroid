package org.aisen.sample.support.sdk.bean;

import java.io.Serializable;

/**
 * Created by wangdan on 16/10/7.
 */
public class VideoStreamBean implements Serializable {

    private static final long serialVersionUID = 7726941201737942601L;

    private String title;

    private long display_time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDisplay_time() {
        return display_time;
    }

    public void setDisplay_time(long display_time) {
        this.display_time = display_time;
    }
}
