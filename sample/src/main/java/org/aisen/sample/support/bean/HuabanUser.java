package org.aisen.sample.support.bean;

/**
 * Created by wangdan on 15/8/20.
 */
public class HuabanUser extends BaseBean {

    private String username;
    private int created_at;
    private int user_id;
    private String avatar;
    private String urlname;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUrlname(String urlname) {
        this.urlname = urlname;
    }

    public String getUsername() {
        return username;
    }

    public int getCreated_at() {
        return created_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUrlname() {
        return urlname;
    }

}
