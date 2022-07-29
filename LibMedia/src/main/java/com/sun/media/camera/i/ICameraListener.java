package com.sun.media.camera.i;

import android.graphics.Bitmap;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note:
 */
public interface ICameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
