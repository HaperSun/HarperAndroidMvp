package com.sun.media.model.bean;

/**
 * @author: Harper
 * @date: 2022/5/23
 * @note: 视频关键帧信息
 */
public class TCPlayKeyFrameDescInfo {

    // 描述信息
    public String   content;
    // 关键帧时间(秒)
    public float    time;

    @Override
    public String toString() {
        return "TCPlayKeyFrameDescInfo{" +
                "content='" + content + '\'' +
                ", time=" + time +
                '}';
    }
}
