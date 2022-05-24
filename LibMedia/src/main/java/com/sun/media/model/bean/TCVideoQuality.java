package com.sun.media.model.bean;

/**
 * @author: Harper
 * @date: 2022/5/23
 * @note: 清晰度
 */
public class TCVideoQuality {

    public int index;
    public int bitrate;
    public String name;
    public String title;
    public String url;

    public TCVideoQuality() {
    }

    public TCVideoQuality(int index, String title, String url) {
        this.index = index;
        this.title = title;
        this.url = url;
    }
}
