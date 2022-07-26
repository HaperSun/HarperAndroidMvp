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

    private Context mContext;
    private ImageScanner mImageScanner;
    private VideoScanner mVideoScanner;
    private IMediaLoadCallback mIMediaLoadCallback;

    public MediaLoadTask(Context context, IMediaLoadCallback IMediaLoadCallback) {
        this.mContext = context;
        this.mIMediaLoadCallback = IMediaLoadCallback;
        mImageScanner = new ImageScanner(context);
        mVideoScanner = new VideoScanner(context);
    }

    @Override
    public void run() {
        //存放所有照片
        ArrayList<MediaFile> imageFileList = new ArrayList<>();
        //存放所有视频
        ArrayList<MediaFile> videoFileList = new ArrayList<>();

        if (mImageScanner != null) {
            imageFileList = mImageScanner.queryMedia();
        }
        if (mVideoScanner != null) {
            videoFileList = mVideoScanner.queryMedia();
        }

        if (mIMediaLoadCallback != null) {
            mIMediaLoadCallback.loadMediaSuccess(MediaHandler.getMediaFolder(mContext, imageFileList, videoFileList));
        }

    }

}
