package com.sun.demo2.model;

/**
 * @author: Harper
 * @date: 2022/7/21
 * @note:
 */
public class LocalVideoModel {
    private int width;
    private int height;
    private String thumbUrl;
    private String videoUrl;

    public LocalVideoModel(int width, int height, String thumbUrl, String videoUrl) {
        this.width = width;
        this.height = height;
        this.thumbUrl = thumbUrl;
        this.videoUrl = videoUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
