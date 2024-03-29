package com.sun.media.video.model.entity;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 清晰度
 */
public class VideoQuality implements Comparable<VideoQuality> {

    public int    height;
    public int    width;
    public int    index;
    public int    bitrate;
    // 清晰度列表的清晰度和dp值
    public String title;
    public String url;

    public VideoQuality() {
    }

    public VideoQuality(int index, String title, String url) {
        this.index = index;
        this.title = title;
        this.url = url;
    }

    @Override
    public int compareTo(VideoQuality o) {
        return o.bitrate - this.bitrate;
    }
}
