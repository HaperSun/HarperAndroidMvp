package com.sun.media.img.task;

import android.content.Context;

import com.sun.common.bean.MediaFile;
import com.sun.media.img.i.IMediaLoadCallback;
import com.sun.media.img.loader.ImageScanner;
import com.sun.media.img.loader.MediaHandler;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 媒体库扫描任务（图片）
 */
public class ImageLoadTask implements Runnable {

    private Context mContext;
    private ImageScanner mImageScanner;
    private IMediaLoadCallback mIMediaLoadCallback;

    public ImageLoadTask(Context context, IMediaLoadCallback IMediaLoadCallback) {
        this.mContext = context;
        this.mIMediaLoadCallback = IMediaLoadCallback;
        mImageScanner = new ImageScanner(context);
    }

    @Override
    public void run() {
        //存放所有照片
        ArrayList<MediaFile> imageFileList = new ArrayList<>();

        if (mImageScanner != null) {
            imageFileList = mImageScanner.queryMedia();
        }

        if (mIMediaLoadCallback != null) {
            mIMediaLoadCallback.loadMediaSuccess(MediaHandler.getImageFolder(mContext, imageFileList));
        }


    }

}
