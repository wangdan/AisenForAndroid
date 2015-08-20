package org.aisen.sample.support.bean;

/**
 * Created by wangdan on 15/8/20.
 */
public class HuabanBoard extends BaseBean {

    private ExtraEntity extra;
    private String category_id;
    private int board_id;
    private int deleting;
    private String title;
    private int follow_count;
    private int updated_at;
    private String description;
    private int like_count;
    private int pin_count;
    private int seq;
    private int created_at;
    private int is_private;
    private int user_id;

    public void setExtra(ExtraEntity extra) {
        this.extra = extra;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public void setDeleting(int deleting) {
        this.deleting = deleting;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public void setPin_count(int pin_count) {
        this.pin_count = pin_count;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public ExtraEntity getExtra() {
        return extra;
    }

    public String getCategory_id() {
        return category_id;
    }

    public int getBoard_id() {
        return board_id;
    }

    public int getDeleting() {
        return deleting;
    }

    public String getTitle() {
        return title;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public String getDescription() {
        return description;
    }

    public int getLike_count() {
        return like_count;
    }

    public int getPin_count() {
        return pin_count;
    }

    public int getSeq() {
        return seq;
    }

    public int getCreated_at() {
        return created_at;
    }

    public int getIs_private() {
        return is_private;
    }

    public int getUser_id() {
        return user_id;
    }

    public static class ExtraEntity extends BaseBean {
        /**
         * cover : {"pin_id":"302821470"}
         */
        private CoverEntity cover;

        public void setCover(CoverEntity cover) {
            this.cover = cover;
        }

        public CoverEntity getCover() {
            return cover;
        }

        public static class CoverEntity extends BaseBean {
            /**
             * pin_id : 302821470
             */
            private String pin_id;

            public void setPin_id(String pin_id) {
                this.pin_id = pin_id;
            }

            public String getPin_id() {
                return pin_id;
            }
        }
    }
}
