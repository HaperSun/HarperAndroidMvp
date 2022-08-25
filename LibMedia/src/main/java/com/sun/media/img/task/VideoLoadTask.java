package com.sun.media.img.task;

import android.content.Context;

import com.sun.base.bean.MediaFile;
import com.sun.media.img.i.IMediaLoadCallback;
import com.sun.media.img.loader.MediaHandler;
import com.sun.media.img.loader.VideoScanner;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 媒体库扫描任务（视频）
 */
public class VideoLoadTask implements Runnable {

    private final Context mContext;
    private final VideoScanner mVideoScanner;
    private final IMediaLoadCallback mMediaLoadCallback;

    public VideoLoadTask(Context context, IMediaLoadCallback mediaLoadCallback) {
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
        mVideoScanner = new VideoScanner(context);
    }

    @Override
    public void run() {
        //存放所有视频
        ArrayList<MediaFile> videoFileList = mVideoScanner.queryMedia();
        if (mMediaLoadCallback != null) {
            mMediaLoadCallback.loadMediaSuccess(MediaHandler.getVideoFolder(mContext, videoFileList));
        }
    }

}
