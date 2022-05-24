package com.sun.picture.img;

import android.graphics.Bitmap;

/**
 * @author: Harper
 * @date: 2021/12/10
 * @note: 图片加载回调监听
 */
public interface ImageLoadListener {

    void onLoadingStarted();

    void onLoadingFailed(Exception e);

    void onLoadingComplete(Bitmap bitmap);
}
