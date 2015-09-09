package org.aisen.sample.support.bean;

import org.aisen.orm.annotation.PrimaryKey;

/**
 * Created by wangdan on 15/8/20.
 */
public class HuabanPin extends BaseBean {

    @PrimaryKey(column = "pin_id")
    private long pin_id;

    private long user_id;

    private long board_id;

    private long file_id;

    private int media_type;

    private String source;

    private String link;

    private String raw_text;

    private long via;

    private long via_user_id;

    private long original;

    private long created_at;

    private long like_count;

    private long comment_count;

    private long repin_count;

    private int is_private;

    private String orig_source;

    private boolean liked;

    private HuabanFile file;

    private HuabanUser user;

    private HuabanUser via_user;

    private HuabanBoard board;

    public long getPin_id() {
        return pin_id;
    }

    public void setPin_id(long pin_id) {
        this.pin_id = pin_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(long board_id) {
        this.board_id = board_id;
    }

    public long getFile_id() {
        return file_id;
    }

    public void setFile_id(long file_id) {
        this.file_id = file_id;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public long getVia() {
        return via;
    }

    public void setVia(long via) {
        this.via = via;
    }

    public long getVia_user_id() {
        return via_user_id;
    }

    public void setVia_user_id(long via_user_id) {
        this.via_user_id = via_user_id;
    }

    public long getOriginal() {
        return original;
    }

    public void setOriginal(long original) {
        this.original = original;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getLike_count() {
        return like_count;
    }

    public void setLike_count(long like_count) {
        this.like_count = like_count;
    }

    public long getComment_count() {
        return comment_count;
    }

    public void setComment_count(long comment_count) {
        this.comment_count = comment_count;
    }

    public long getRepin_count() {
        return repin_count;
    }

    public void setRepin_count(long repin_count) {
        this.repin_count = repin_count;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public String getOrig_source() {
        return orig_source;
    }

    public void setOrig_source(String orig_source) {
        this.orig_source = orig_source;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public HuabanFile getFile() {
        return file;
    }

    public void setFile(HuabanFile file) {
        this.file = file;
    }

    public HuabanUser getUser() {
        return user;
    }

    public void setUser(HuabanUser user) {
        this.user = user;
    }

    public HuabanUser getVia_user() {
        return via_user;
    }

    public void setVia_user(HuabanUser via_user) {
        this.via_user = via_user;
    }

    public HuabanBoard getBoard() {
        return board;
    }

    public void setBoard(HuabanBoard board) {
        this.board = board;
    }

}
