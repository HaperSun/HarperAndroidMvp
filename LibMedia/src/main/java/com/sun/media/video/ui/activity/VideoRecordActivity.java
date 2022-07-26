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
import com.sun.base.bean.TDevice;
import com.sun.base.manager.SelectionManager;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.base.util.MediaFileUtil;
import com.sun.base.util.MediaUtils;
import com.sun.base.util.PermissionUtil;
import com.sun.base.bean.MediaFile;
import com.sun.base.util.DataUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVideoRecordBinding;
import com.sun.media.video.ui.view.MovieRecorderView;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
        bind.videoRecordPhoto.setOnClickListener(this);
        bind.videoRecordSelect.setOnClickListener(this);
        bind.videoRecordStart.setOnClickListener(this);
        bind.videoRecordStart.setVisibility(TDevice.hasFrontCamera(mContext) ? View.VISIBLE : View.GONE);
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
        bind.movieRecorderView.stop();
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
            bind.movieRecorderView.changeCamera();
        } catch (Exception e) {
            LogHelper.d("reversalCamera", e.getMessage());
        }
    }

    private void stopRecord() {
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        bind.videoRecordPhoto.setVisibility(View.VISIBLE);
        bind.videoRecordStart.setBackgroundResource(R.mipmap.icon_take_photo_start);
        bind.movieRecorderView.stop();
        File file = bind.movieRecorderView.getmVecordFile();
        if (file.isFile() && file.exists()) {
            String path = bind.movieRecorderView.getmVecordFile().getPath();
            if (FileUtil.isFileExist(path)) {
                MediaUtils.getImageForVideo(path, file1 -> {
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
                List<MediaFile> mMediaFileList = new ArrayList<>();
                MediaFile mediaFile = new MediaFile();
                mediaFile.setFolderName(file.getName());
                mediaFile.setMime("mp4");
                mediaFile.setPath(path);
                long videoLength = (mTimeLength - 1) * 1000L;
                mediaFile.setDuration(videoLength);
                mMediaFileList.add(mediaFile);
                DataUtil.getInstance().setMediaData(mMediaFileList);
                //还原参数
                mTimeLength = 0;
                bind.videoRecordProgress.setProgress(0);
                //去预览页面
                if (!mNoNeedToPreview) {
                    VideoEditActivity.startForResult(this, REQUEST_CODE_RECORD, 0);
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
                    bind.videoRecordProgress.setProgress(100);
                    stopRecord();
                } else {
                    double progress = BigDecimal.valueOf((float) mTimeLength / mMaxTime)
                            .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
                    bind.videoRecordProgress.setProgress((int) progress);
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 开启录制
     */
    private void startRecord() {
        bind.videoRecordPhoto.setVisibility(View.INVISIBLE);
        bind.videoRecordStart.setBackgroundResource(R.mipmap.icon_take_photo_ing);
        bind.movieRecorderView.setVisibility(View.VISIBLE);
        bind.movieRecorderView.record(new MovieRecorderView.OnRecordStateListener() {
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
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_RECORD) {
            ArrayList<String> list = new ArrayList<>(SelectionManager.getInstance().getSelectPaths());
            Intent intent = new Intent();
            intent.putStringArrayListExtra(EXTRA_SELECT_IMAGES, list);
            if (CollectionUtil.size(list) == 1) {
                intent.putExtra(EXTRA_SELECT_VIDEO, MediaFileUtil.isVideoFileType(list.get(0)));
            }
            setResult(RESULT_OK, intent);
            //清空选中记录
            SelectionManager.getInstance().removeAll();
            finish();
        }
    }
}