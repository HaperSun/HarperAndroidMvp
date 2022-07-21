package com.sun.media.video.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.PermissionUtil;
import com.sun.common.bean.MediaFile;
import com.sun.common.bean.Parameter;
import com.sun.common.util.DataUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVideoTrimmerBinding;
import com.sun.media.video.i.VideoTrimListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/20
 * @note: 视频裁剪
 */
public class VideoTrimmerActivity extends BaseMvpActivity<ActivityVideoTrimmerBinding> implements VideoTrimListener {

    private String mPath;
    private ProgressDialog mProgressDialog;

    /**
     * 启动录制Activity
     */
    public static void startForResult(FragmentActivity activity, int requestCode, String videoPath) {
        Intent intent = new Intent(activity, VideoTrimmerActivity.class);
        intent.putExtra(Parameter.VIDEO_PATH, videoPath);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_trimmer;
    }

    @Override
    protected void initIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            mPath = intent.getStringExtra(Parameter.VIDEO_PATH);
        }
    }

    @Override
    public void initView() {
        if (!PermissionUtil.checkCamera()){
            PermissionUtil.requestCamera(this, state -> {
                bind.trimmerView.setOnTrimVideoListener(this);
                bind.trimmerView.initVideoByURI(Uri.parse(mPath));
            });
        }else {
            bind.trimmerView.setOnTrimVideoListener(this);
            bind.trimmerView.initVideoByURI(Uri.parse(mPath));
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void onFinishTrim(String videoPath) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        List<MediaFile> mMediaFileList = new ArrayList<>();
        MediaFile mediaFile = new MediaFile();
        mediaFile.setMime("mp4");
        mediaFile.setPath(videoPath);
        mMediaFileList.add(mediaFile);
        DataUtil.getInstance().setMediaData(mMediaFileList);
        Intent intent = new Intent();
        intent.putExtra(Parameter.PICTURE_PATH, videoPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bind.trimmerView.onVideoPause();
        bind.trimmerView.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.trimmerView.onDestroy();
    }

    @Override
    public void onCancel() {
        bind.trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}