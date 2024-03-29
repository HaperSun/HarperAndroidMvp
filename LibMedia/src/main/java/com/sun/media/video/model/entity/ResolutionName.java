package com.sun.media.video.model.entity;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 自适应码流视频画质别名
 */
public class ResolutionName {

    // 画质名称
    public String name;
    // 类型 可能的取值有 video 和 audio
    public String type;
    public int    width;
    public int    height;

    @Override
    public String toString() {
        return "TCResolutionName{" +
                "width='" + width + '\'' +
                "height='" + height + '\'' +
                "type='" + type + '\'' +
                ", name=" + name +
                '}';
    }
}
