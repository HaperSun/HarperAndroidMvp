package com.sun.media.video.model.entity;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 视频雪碧图信息
 */
public class PlayImageSpriteInfo {

    // 图片链接URL
    public List<String> imageUrls;
    // web vtt描述文件下载URL
    public String       webVttUrl;

    @Override
    public String toString() {
        return "TCPlayImageSpriteInfo{" +
                "imageUrls=" + imageUrls +
                ", webVttUrl='" + webVttUrl + '\'' +
                '}';
    }
}
