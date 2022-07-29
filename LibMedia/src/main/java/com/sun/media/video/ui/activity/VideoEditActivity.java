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
import com.sun.base.bean.TDevice;
import com.sun.base.manager.SelectionManager;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.DataUtil;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVideoEditBinding;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.i.ImageLoadListener;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 相册选择——视频编辑
 */
public class VideoEditActivity extends BaseMvpActivity<ActivityVideoEditBinding> implements View.OnClickListener {

    public static final long MIN_CROP_DURATION = 5000;
    private static final int CODE_CROP_VIDEO_REQUEST = 2001;
    private int mPosition = 0;
    private SurfaceTexture surfaceTexture;
    private String mCurSelectPath;
    private MediaFile mCurMediaFile;
    private MediaPlayer mMediaPlayer;

    public static void start(Context context, int index) {
        Intent intent = new Intent(context, VideoEditActivity.class);
        intent.putExtra(Parameter.INDEX, index);
        context.startActivity(intent);
    }

    /**
     * 启动Activity
     */
    public static void startForResult(Activity activity, int requestCode, int index) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        intent.putExtra(Parameter.INDEX, index);
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
            mPosition = intent.getIntExtra(Parameter.INDEX, 0);
        }
    }

    @Override
    public void initView() {
        bind.ivCropper.setOnClickListener(this);
        List<MediaFile> mMediaFileList = DataUtil.getInstance().getMediaData();
        mCurSelectPath = mMediaFileList.get(mPosition).getPath();
        mCurMediaFile = mMediaFileList.get(mPosition);
        //更新当前页面状态
        setIvPlayShow(mMediaFileList.get(mPosition));
        updateCommitButton();
        initVideoSize();
    }

    @Override
    public void initData() {
        bind.tvActionBarCommit.setOnClickListener(v -> {
            int selectCount = SelectionManager.getInstance().getSelectPaths().size();
            if (selectCount == 0) {
                //没有选时候默认点击完成给当前的图片
                SelectionManager.getInstance().removeAll();
                SelectionManager.getInstance().addImageToSelectList(mCurMediaFile.getPath());
            }
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        });
        bind.ivMainPlay.setOnClickListener(v -> {

        });
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
        ImageLoader.getInstance().loadImage(mCurSelectPath, new ImageView(this), new ImageLoadListener() {
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

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        int maxCount = SelectionManager.getInstance().getMaxCount();
        //改变确定按钮UI
        int selectCount = SelectionManager.getInstance().getSelectPaths().size();
        bind.ivCropper.setVisibility((selectCount == 1 || selectCount == 0) ? View.VISIBLE : View.GONE);
        if (selectCount == 0) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(getString(R.string.confirm));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
            return;
        }
        if (selectCount < maxCount) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
            return;
        }
        if (selectCount == maxCount) {
            bind.tvActionBarCommit.setEnabled(true);
            bind.tvActionBarCommit.setText(String.format(getString(R.string.confirm_msg), selectCount));
            bind.tvActionBarCommit.setBackgroundResource(R.drawable.shape_rec_solid_ff8c4a_radius_dp5);
            return;
        }
    }

    /**
     * 设置是否显示视频播放按钮
     *
     * @param mediaFile
     */
    private void setIvPlayShow(MediaFile mediaFile) {
        if (null == mediaFile) {
            return;
        }
        if (mediaFile.getDuration() > 0) {
            bind.ivMainPlay.setVisibility(View.VISIBLE);
        } else {
            bind.ivMainPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_cropper) {
            if (null != mCurMediaFile) {
                if (mCurMediaFile.getDuration() >= MIN_CROP_DURATION) {
                    //视频裁剪
                    VideoTrimmerActivity.startForResult(this, CODE_CROP_VIDEO_REQUEST, mCurMediaFile.getPath());
                } else {
                    ToastHelper.showToast(R.string.min_crop_toast);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CODE_CROP_VIDEO_REQUEST) {
            if (null != data) {
                //单张裁剪过后点击裁剪的完成按钮，多张图暂时不支持裁剪
                String mCropImgPath = data.getStringExtra(Parameter.PICTURE_PATH);
                SelectionManager.getInstance().removeAll();
                SelectionManager.getInstance().addImageToSelectList(mCropImgPath);
                setResult(Activity.RESULT_OK, new Intent());
                finish();
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