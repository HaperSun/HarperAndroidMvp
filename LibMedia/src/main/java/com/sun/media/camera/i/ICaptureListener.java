package com.sun.media.camera.i;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note:
 */
public interface ICaptureListener {

    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void recordZoom(float zoom);

    void recordError();
}
