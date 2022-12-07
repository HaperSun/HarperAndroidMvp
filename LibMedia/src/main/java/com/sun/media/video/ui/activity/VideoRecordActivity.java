package com.sun.media.video.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.bean.TDevice;
import com.sun.base.util.DataUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.base.util.MediaUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVideoRecordBinding;
import com.sun.media.video.ui.view.MovieRecorderView;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Harper
 * @date: 2022/7/21
 * @note: 视频录制
 */
public class VideoRecordActivity extends BaseMvpActivity<ActivityVideoRecordBinding> implements View.OnClickListener {

    private static final int REQUEST_CODE_RECORD = 0x1002;
    private static final String EXTRA_RECORD_VIDEO_MAX_TIME = "extra_record_video_max_time";
    private static final String EXTRA_SELECT_IMAGES = "selectItems";
    private static final String EXTRA_SELECT_VIDEO = "selectVideo";
    /**
     * 一次拍摄最长时间
     */
    private static final int RECORD_MAX_TIME = 30;
    private Context mContext;
    private boolean mIsRecording;
    //计时器
    private ScheduledExecutorService mExecutorService;
    private int mTimeLength = 0;
    private String mRecordPath;
    private int mMaxTime;
    private boolean mNoNeedToPreview;

    /**
     * 启动录制Activity
     */
    public static void startForResult(Activity activity, int requestCode, int extraMaxTime) {
        Intent intent = new Intent(activity, VideoRecordActivity.class);
        intent.putExtra(EXTRA_RECORD_VIDEO_MAX_TIME, extraMaxTime);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_record;
    }

    @Override
    protected void initIntent() {
        mContext = VideoRecordActivity.this;
        Intent intent = getIntent();
        if (intent != null) {
            mMaxTime = intent.getIntExtra(EXTRA_RECORD_VIDEO_MAX_TIME, RECORD_MAX_TIME);
        }
    }

    @Override
    public void initView() {
        vdb.videoRecordPhoto.setOnClickListener(this);
        vdb.videoRecordSelect.setOnClickListener(this);
        vdb.videoRecordStart.setOnClickListener(this);
        vdb.videoRecordStart.setVisibility(TDevice.hasFrontCamera(mContext) ? View.VISIBLE : View.GONE);
        if (!PermissionUtil.checkCamera()) {
            PermissionUtil.requestCamera(this, state -> {

            });
        }
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        //如果正在录制按了Home键
        if (mIsRecording) {
            stopRecord();
        }
        //如果正在播放则停止播放
    }

    @Override
    protected void onDestroy() {
        vdb.movieRecorderView.stop();
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mNoNeedToPreview = true;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.video_record_photo) {
            reversalCamera();
        } else if (id == R.id.video_record_select) {
            onBackPressed();
        } else if (id == R.id.video_record_start) {
            if (mIsRecording) {
                stopRecord();
            } else {
                startRecord();
            }
            mIsRecording = !mIsRecording;
        }
    }

    /**
     * 反转摄像头
     */
    private void reversalCamera() {
        try {
            vdb.movieRecorderView.changeCamera();
        } catch (Exception e) {
            LogHelper.d("reversalCamera", e.getMessage());
        }
    }

    private void stopRecord() {
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        vdb.videoRecordPhoto.setVisibility(View.VISIBLE);
        vdb.videoRecordStart.setBackgroundResource(R.mipmap.icon_take_photo_start);
        vdb.movieRecorderView.stop();
        File file = vdb.movieRecorderView.getmVecordFile();
        if (file.isFile() && file.exists()) {
            String path = vdb.movieRecorderView.getmVecordFile().getPath();
            if (FileUtil.isFileExist(path)) {
                MediaUtil.getImageForVideo(path, file1 -> {
                    try {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        mRecordPath = file1.getAbsolutePath();
                    } catch (Exception e) {
                        LogHelper.d("stopRecord", e.getMessage());
                    }
                });

                //保存视频到相机
                ContentResolver localContentResolver = this.getContentResolver();
                ContentValues localContentValues = getVideoContentValues(new File(path), System.currentTimeMillis());
                Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));

                //去预览页面的数据
                ArrayList<MediaFile> mediaFiles = new ArrayList<>();
                MediaFile mediaFile = new MediaFile();
                mediaFile.setFolderName(file.getName());
                mediaFile.setMime("mp4");
                mediaFile.setPath(path);
                mediaFile.setItemType(MediaFile.VIDEO);
                long videoLength = (mTimeLength - 1) * 1000L;
                mediaFile.setDuration(videoLength);
                mediaFiles.add(mediaFile);
                DataUtil.getInstance().setMediaData(mediaFiles);
                //还原参数
                mTimeLength = 0;
                vdb.videoRecordProgress.setProgress(0);
                //去预览页面
                if (!mNoNeedToPreview) {
                    VideoEditActivity.startForResult(this, REQUEST_CODE_RECORD);
                }
            }
        }
    }

    public ContentValues getVideoContentValues(File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/mp4");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        localContentValues.put("duration", Long.valueOf((mTimeLength - 1) * 1000L));
        return localContentValues;
    }

    private void startTime() {
        mTimeLength = 0;
        if (mExecutorService == null) {
            mExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern(TAG).daemon(true).build());
        }
        mExecutorService.scheduleAtFixedRate(() -> {
            mTimeLength++;
            runOnUiThread(() -> {
                if (mTimeLength > mMaxTime) {
                    vdb.videoRecordProgress.setProgress(100);
                    stopRecord();
                } else {
                    double progress = BigDecimal.valueOf((float) mTimeLength / mMaxTime)
                            .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
                    vdb.videoRecordProgress.setProgress((int) progress);
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 开启录制
     */
    private void startRecord() {
        vdb.videoRecordPhoto.setVisibility(View.INVISIBLE);
        vdb.videoRecordStart.setBackgroundResource(R.mipmap.icon_take_photo_ing);
        vdb.movieRecorderView.setVisibility(View.VISIBLE);
        vdb.movieRecorderView.record(new MovieRecorderView.OnRecordStateListener() {
            @Override
            public void onRecordStart() {
                startTime();
                LogHelper.d("onRecordStart=====", "onRecordStart");
            }

            @Override
            public void onRecordFinish() {
                mIsRecording = false;
            }
        }, mMaxTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Parameter.REQUEST_CODE_MEDIA && resultCode == Parameter.RESULT_CODE_MEDIA) {
                Intent intent = new Intent();
                ArrayList<MediaFile> mediaFiles = data.getParcelableArrayListExtra(Parameter.FILE_PATH);
                intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
                setResult(Parameter.RESULT_CODE_MEDIA, intent);
                close();
            }
        }
    }
}