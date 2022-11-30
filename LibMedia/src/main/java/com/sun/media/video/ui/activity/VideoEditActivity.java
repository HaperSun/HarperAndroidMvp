package com.sun.media.video.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.bean.TDevice;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.CollectionUtil;
import com.sun.base.util.DataUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVideoEditBinding;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.i.ImageLoadListener;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 相册选择——视频编辑
 */
public class VideoEditActivity extends BaseMvpActivity<ActivityVideoEditBinding> implements View.OnClickListener {

    public static final long MIN_CROP_DURATION = 5000;
    private static final int CODE_CROP_VIDEO_REQUEST = 2001;
    private SurfaceTexture surfaceTexture;
    private String mCurSelectPath;
    private MediaFile mCurMediaFile;
    private MediaPlayer mMediaPlayer;
    private boolean mJustCheck;

    public static void start(Context context, boolean justCheck) {
        Intent intent = new Intent(context, VideoEditActivity.class);
        intent.putExtra(Parameter.BEAN, justCheck);
        context.startActivity(intent);
    }

    /**
     * 启动Activity
     */
    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_edit;
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
        if (intent != null) {
            mJustCheck = intent.getBooleanExtra(Parameter.BEAN, false);
        }
    }

    @Override
    public void initView() {
        ArrayList<MediaFile> mediaFiles = DataUtil.getInstance().getMediaData();
        if (CollectionUtil.notEmpty(mediaFiles)) {
            mCurSelectPath = mediaFiles.get(0).getPath();
            mCurMediaFile = mediaFiles.get(0);
            initVideoSize();
        }
    }

    @Override
    public void initData() {
        baseBind.title.setTitle("视频预览");
        baseBind.title.setOnTitleClickListener(view -> onBackPressed());
        bind.bottomContainer.setVisibility(mJustCheck ? View.GONE : View.VISIBLE);
        bind.ivCropper.setOnClickListener(this);
        bind.tvActionBarCommit.setOnClickListener(this);
        bind.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;
                initMediaPlay(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void initVideoSize() {
        ImageLoader.load().loadImage(mCurSelectPath, new ImageView(this), new ImageLoadListener() {
            @Override
            public void onLoadingStarted() {

            }

            @Override
            public void onLoadingFailed(Exception e) {

            }

            @Override
            public void onLoadingComplete(Bitmap bitmap) {
                float ra = bitmap.getWidth() * 1f / bitmap.getHeight();
                ViewGroup.LayoutParams layoutParams = bind.textureView.getLayoutParams();
                layoutParams.width = TDevice.getScreenWidth();
                layoutParams.height = (int) (layoutParams.width / ra);
                bind.textureView.setLayoutParams(layoutParams);
            }
        });
    }

    private void initMediaPlay(SurfaceTexture surface) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mCurSelectPath);
            mMediaPlayer.setSurface(new Surface(surface));
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(mp -> mMediaPlayer.start());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (mCurMediaFile == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.iv_cropper) {
            if (mCurMediaFile.getDuration() >= MIN_CROP_DURATION) {
                //视频裁剪
                VideoTrimmerActivity.startForResult(this, CODE_CROP_VIDEO_REQUEST, mCurMediaFile);
            } else {
                ToastHelper.showToast(R.string.min_crop_toast);
            }
        } else if (id == R.id.tv_actionBar_commit) {
            Intent intent = new Intent();
            ArrayList<MediaFile> mediaFiles = new ArrayList<>();
            mediaFiles.add(mCurMediaFile);
            intent.putParcelableArrayListExtra(Parameter.FILE_PATH, mediaFiles);
            setResult(Parameter.RESULT_CODE_MEDIA, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            initMediaPlay(surfaceTexture);
            initVideoSize();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }
}