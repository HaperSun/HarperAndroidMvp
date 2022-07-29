package com.sun.media.camera.i;

import android.graphics.Bitmap;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note:
 */
public interface ICameraView {

    void resetState(int type);

    void confirmState(int type);

    void showPicture(Bitmap bitmap, boolean isVertical);

    void playVideo(Bitmap firstFrame, String url);

    void stopVideo();

    void setTip(String tip);

    void startPreviewCallback();

    boolean handlerFocus(float x, float y);
}
