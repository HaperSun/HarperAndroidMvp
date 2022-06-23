package com.sun.video.model;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 使用腾讯云fileId播放
 */
public class SuperPlayerVideoId {

    // 腾讯云视频fileId
    public String fileId;
    // v4 开启防盗链必填
    public String pSign;
    // HLS EXT-X-KEY 加密key
    public String overlayKey;
    // HLS EXT-X-KEY 加密Iv
    public String overlayIv;

    @Override
    public String toString() {
        return "SuperPlayerVideoId{" +
                ", fileId='" + fileId + '\'' +
                ", pSign='" + pSign + '\'' +
                ", overlayKey='" + overlayKey + '\'' +
                ", overlayIv='" + overlayIv + '\'' +
                '}';
    }
}
