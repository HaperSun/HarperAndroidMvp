package com.sun.media.model.bean;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/5/23
 * @note: 视频雪碧图信息
 */
public class TCPlayImageSpriteInfo {

    // 图片链接URL
    public List<String> imageUrls;
    // web vtt描述文件下载URL
    public String webVttUrl;

    @Override
    public String toString() {
        return "TCPlayImageSpriteInfo{" +
                "imageUrls=" + imageUrls +
                ", webVttUrl='" + webVttUrl + '\'' +
                '}';
    }
}
