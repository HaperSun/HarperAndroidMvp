package com.sun.media.video.i;


import com.sun.media.video.model.entity.PlayImageSpriteInfo;
import com.sun.media.video.model.entity.PlayKeyFrameDescInfo;
import com.sun.media.video.model.entity.ResolutionName;
import com.sun.media.video.model.entity.VideoQuality;
import com.sun.media.video.model.protocol.PlayInfoConstant;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 视频信息协议解析接口
 */
public interface IPlayInfoParser {
    /**
     * 获取未加密视频播放url,若没有获取sampleaes url
     *
     * @return url字符串
     */
    String getURL();

    /**
     * 获取加密视频播放url
     *
     * @return url字符串
     */
    String getEncryptedURL(PlayInfoConstant.EncryptedURLType type);

    /**
     * 获取加密token
     *
     * @return token字符串
     */
    String getToken();

    /**
     * 获取视频名称
     *
     * @return 视频名称字符串
     */
    String getName();

    /**
     * 获取雪碧图信息
     *
     * @return 雪碧图信息对象
     */
    PlayImageSpriteInfo getImageSpriteInfo();

    /**
     * 获取关键帧信息
     *
     * @return 关键帧信息数组
     */
    List<PlayKeyFrameDescInfo> getKeyFrameDescInfo();

    /**
     * 获取画质信息
     *
     * @return 画质信息数组
     */
    List<VideoQuality> getVideoQualityList();

    /**
     * 获取默认画质信息
     *
     * @return 默认画质信息对象
     */
    VideoQuality getDefaultVideoQuality();

    /**
     * 获取视频画质别名列表
     *
     * @return 画质别名数组
     */
    List<ResolutionName> getResolutionNameList();

    /**
     * 获取 DRM 加密类型
     *
     * @return
     */
    String getDRMType();

}
