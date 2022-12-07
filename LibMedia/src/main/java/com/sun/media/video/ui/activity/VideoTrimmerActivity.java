package com.sun.media.video.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.util.DataUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVideoTrimmerBinding;
import com.sun.media.video.i.VideoTrimListener;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/20
 * @note: 视频裁剪
 */
public class VideoTrimmerActivity extends BaseMvpActivity<ActivityVideoTrimmerBinding> implements VideoTrimListener {

    private MediaFile mMediaFile;
    private String mPath;
    private ProgressDialog mProgressDialog;

    /**
     * 启动录制Activity
     */
    public static void startForResult(FragmentActivity activity, int requestCode, MediaFile mediaFile) {
        Intent intent = new Intent(activity, VideoTrimmerActivity.class);
        intent.putExtra(Parameter.BEAN, mediaFile);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_trimmer;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.cl_323232;
        mTitleColor = R.color.cl_323232;
        return true;
    }

    @Override
    protected void initIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            mMediaFile = intent.getParcelableExtra(Parameter.BEAN);
            mPath = mMediaFile.path;
        }
    }

    @Override
    public void initView() {
        baseBind.title.setTitle("视频裁剪");
        baseBind.title.setOnTitleClickListener(view -> onBackPressed());
        if (!PermissionUtil.checkCamera()){
            PermissionUtil.requestCamera(this, state -> {
                vdb.trimmerView.setOnTrimVideoListener(this);
                vdb.trimmerView.initVideoByURI(Uri.parse(mPath));
            });
        }else {
            vdb.trimmerView.setOnTrimVideoListener(this);
            vdb.trimmerView.initVideoByURI(Uri.parse(mPath));
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
        ArrayList<MediaFile> mediaFiles = new ArrayList<>();
        mMediaFile.setMime("mp4");
        mMediaFile.path = videoPath;
        mediaFiles.add(mMediaFile);
        DataUtil.getInstance().setMediaData(mediaFiles);
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
        setResult(Parameter.RESULT_CODE_MEDIA, intent);
        close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vdb.trimmerView.onVideoPause();
        vdb.trimmerView.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vdb.trimmerView.onDestroy();
    }

    @Override
    public void onCancel() {
        vdb.trimmerView.onDestroy();
        close();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}