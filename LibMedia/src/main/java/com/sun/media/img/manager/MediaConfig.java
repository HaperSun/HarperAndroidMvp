package com.sun.media.img.manager;

/**
 * @author Harper
 * @date 2022/7/27
 * note:
 */
public class MediaConfig {

    public static final int TAKE_PHOTO = 0x101;
    public static final int TAKE_VIDEO = 0x102;
    public static final int TAKE_BOTH = 0x103;
    public static final int PHOTO_ALBUM = 0X300;
    public static final int BOTTOM_DIALOG = 0X400;

    /**
     * 前置摄像头拍摄是否启用镜像 默认开启
     */
    public boolean isMirror;

    /**
     * 最大原图大小 单位MB
     */
    public int maxOriginalSize;

    /**
     * 最大选择数目
     */
    public int maxCount = 9;

    /**
     * 剩余选择数目
     */
    public int remainderCount;

    /**
     * 最大图片宽度
     */
    public int maxWidth;

    /**
     * 最大图片高度
     */
    public int maxHeight;

    /**
     * 单位：MB , 默认15MB
     */
    public int maxImageSize;

    /**
     * 视频时长，单位：毫秒 ,默认20秒
     */
    public int maxVideoLength;

    /**
     * 视频大小，单位：MB
     */
    public int maxVideoSize;

    /**
     * 操作类型
     */
    public int selectType;

    /**
     * 最大选择视频数
     */
    public int maxVideoCount;

    /**
     * 在本地显示，不用上传到网络
     */
    public boolean showLocal;

    /**
     * 可以删除
     */
    public boolean showDelete;
}
