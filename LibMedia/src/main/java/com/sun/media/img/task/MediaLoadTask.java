package com.sun.media.img.task;

import android.content.Context;

import com.sun.base.bean.MediaFile;
import com.sun.media.img.i.IMediaLoadCallback;
import com.sun.media.img.loader.ImageScanner;
import com.sun.media.img.loader.MediaHandler;
import com.sun.media.img.loader.VideoScanner;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 媒体库扫描任务（图片、视频）
 */
public class MediaLoadTask implements Runnable {

    private final Context mContext;
    private final ImageScanner mImageScanner;
    private final VideoScanner mVideoScanner;
    private final IMediaLoadCallback mMediaLoadCallback;

    public MediaLoadTask(Context context, IMediaLoadCallback mediaLoadCallback) {
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
        mImageScanner = new ImageScanner(context);
        mVideoScanner = new VideoScanner(context);
    }

    @Override
    public void run() {
        //存放所有照片
        ArrayList<MediaFile> imageFileList = mImageScanner.queryMedia();
        //存放所有视频
        ArrayList<MediaFile> videoFileList = mVideoScanner.queryMedia();
        if (mMediaLoadCallback != null) {
            mMediaLoadCallback.loadMediaSuccess(MediaHandler.getMediaFolder(mContext, imageFileList, videoFileList));
        }
    }
}
