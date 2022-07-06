package com.sun.media.video.i;

import com.sun.media.video.model.entity.VideoInfo;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note:
 */
public interface GetVideoInfoListListener {
    void onGetVideoInfoList(List<VideoInfo> videoInfoList);

    void onFail(int errCode);
}
