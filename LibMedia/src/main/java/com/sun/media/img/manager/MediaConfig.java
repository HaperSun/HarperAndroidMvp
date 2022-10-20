package com.sun.media.img.manager;

/**
 * @author Harper
 * @date 2022/7/27
 * note:
 */
public class MediaConfig {

    /**
     * （*注*）
     * 操作类型operation：拍照，拍视频，拍照或视频，从相册中选择
     */
    public static final String TAKE_PHOTO = "1";
    public static final String TAKE_VIDEO = "2";
    public static final String TAKE_BOTH = "3";
    public static final String FROM_ALBUM = "4";

    /**
     * （*注*）
     * selectMediaType：照片、视频、照片和视频
     * 和CameraView中的是一一对应的
     */
    public static final int PHOTO = 0x101;
    public static final int VIDEO = 0x102;
    public static final int BOTH = 0x103;

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
    public int maxCount;

    /**
     * 剩余选择数目
     */
    public int selectedCount;

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
    public long maxVideoLength;

    /**
     * 视频大小，单位：MB
     */
    public int maxVideoSize;

    /**
     * 操作类型
     */
    public String operationType;

    /**
     * 注：只有 PHOTO、VIDEO、BOTH
     * 选择类型
     */
    public int mediaFileType;

    /**
     * 最大选择视频数
     */
    public int maxVideoSelectCount;

    /**
     * 在本地显示，不用上传到网络
     */
    public boolean needUploadFile;

    /**
     * 可以删除
     */
    public boolean showDelete;

    /**
     * 相册中是否可以拍照
     */
    public boolean albumCanTakePhoto;

    /**
     * 是否过滤GIF图片，默认不过滤
     */
    public boolean filterGif;

    /**
     * 旋转摄像头
     */
    public boolean switchCamera;
}
