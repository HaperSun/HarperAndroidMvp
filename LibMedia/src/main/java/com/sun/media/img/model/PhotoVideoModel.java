package com.sun.media.img.model;

/**
 * @author Harper
 * @date 2022/7/22
 * note:
 */
public class PhotoVideoModel {

    /**
     * 图片的真实宽度
     */
    public int width;
    /**
     * 图片的真实高度
     */
    public int height;
    /**
     * 图片网络地址或本地路径
     */
    public String imgUrl;
    /**
     * 图片缩略图网络地址
     */
    public String thumbImgUrl;
    /**
     * 视频网络地址或本地路径
     */
    public String videoUrl;
    /**
     * 标题
     */
    public String title;
}
