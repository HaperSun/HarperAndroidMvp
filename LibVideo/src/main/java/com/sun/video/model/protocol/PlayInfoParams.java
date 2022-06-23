package com.sun.video.model.protocol;


import com.sun.video.model.SuperPlayerVideoId;
import com.sun.video.model.entity.SuperPlayerVideoIdV2;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 视频信息协议解析需要传入的参数
 */
public class PlayInfoParams {
    //必选
    // 腾讯云视频appId
    public int                  appId;
    // 腾讯云视频fileId
    public String               fileId;
    //v4 协议参数
    public SuperPlayerVideoId videoId;
    //v2 协议参数
    public SuperPlayerVideoIdV2 videoIdV2;

    public PlayInfoParams() {
    }

    @Override
    public String toString() {
        return "TCPlayInfoParams{" +
                ", appId='" + appId + '\'' +
                ", fileId='" + fileId + '\'' +
                ", v4='" + (videoId != null ? videoId.toString() : "") + '\'' +
                ", v2='" + (videoIdV2 != null ? videoIdV2.toString() : "") + '\'' +
                '}';
    }
}
