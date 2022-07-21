package com.sun.media.video.i;

/**
 * @author: Harper
 * @date: 2022/7/21
 * @note:
 */
public interface VideoTrimListener {

    void onStartTrim();

    void onFinishTrim(String url);

    void onCancel();
}
