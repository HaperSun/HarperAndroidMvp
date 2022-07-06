package com.sun.media.video.model.entity;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 自适应码流信息
 */
public class EncryptedStreamingInfo {

    public String drmType;
    public String url;

    @Override
    public String toString() {
        return "TCEncryptedStreamingInfo{" +
                ", drmType='" + drmType + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
