package com.sun.media.img.model.bean;

/**
 * @author Harper
 * @date 2022/8/24
 * note:
 */
public class AlbumIndexBean {
    private boolean isUsed;
    private String index;

    public AlbumIndexBean(String index) {
        this.index = index;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
