package com.sun.video.model.entity;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 视频关键帧信息
 */
public class PlayKeyFrameDescInfo {

    // 描述信息
    public String content;
    // 关键帧时间(秒)
    public float  time;

    @Override
    public String toString() {
        return "TCPlayKeyFrameDescInfo{" +
                "content='" + content + '\'' +
                ", time=" + time +
                '}';
    }
}
