package com.sun.media.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.MediaFile;
import com.sun.base.bean.Parameter;
import com.sun.base.util.DataUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.PermissionUtil;
import com.sun.media.R;
import com.sun.media.camera.i.ICameraListener;
import com.sun.media.camera.i.IErrorListener;
import com.sun.media.camera.view.CameraView;
import com.sun.media.databinding.ActivityCameraBinding;
import com.sun.media.img.ui.activity.ImageEditActivity;
import com.sun.media.video.ui.activity.VideoEditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note: 拍照
 */
public class CameraActivity extends BaseMvpActivity<ActivityCameraBinding> implements ICameraListener, IErrorListener {

    private boolean granted = false;
    private int mTakeType;

    public static void start(Context context, int takeType) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(Parameter.INDEX, takeType);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mTakeType = intent.getIntExtra(Parameter.INDEX, CameraView.TAKE_PHOTO);
        }
    }

    @Override
    public void initView() {
        if (!PermissionUtil.checkCamera()) {
            PermissionUtil.requestCamera(this, state -> {
                if (state) {
                    granted = true;
                } else {
                    showFailToast("请到设置-权限管理中开启相关权限~");
                    close();
                }
            });
        }
    }

    @Override
    public void initData() {
        //设置拍摄类型
        bind.cameraView.setCameraType(mTakeType);
        //设置视频保存路径
        bind.cameraView.setSaveVideoPath(FileUtil.getMediaFileName());
        //JCameraView监听
        bind.cameraView.setCameraListener(this);
        bind.cameraView.setErrorListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (granted) {
            bind.cameraView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bind.cameraView.onPause();
    }

    @Override
    public void captureSuccess(Bitmap bitmap) {
        String path = FileUtil.saveBitmapAsPicture(bitmap);
        List<MediaFile> mMediaFileList = new ArrayList<>();
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFolderName("temp");
        mediaFile.setMime("image/jpg");
        mediaFile.setPath(path);
        mMediaFileList.add(mediaFile);
        DataUtil.getInstance().setMediaData(mMediaFileList);
        ImageEditActivity.start(this, 0);
        close();
    }

    /**
     * @param url        视频本地路径
     * @param firstFrame 视频第一帧
     */
    @Override
    public void recordSuccess(String url, Bitmap firstFrame) {
        //去视频预览页面的数据
        List<MediaFile> mMediaFileList = new ArrayList<>();
        MediaFile mediaFile = new MediaFile();
        mediaFile.setMime("mp4");
        mediaFile.setPath(url);
        mediaFile.setDuration(FileUtil.getVideoDuration(url));
        mMediaFileList.add(mediaFile);
        DataUtil.getInstance().setMediaData(mMediaFileList);
        VideoEditActivity.start(this, 0);
    }

    @Override
    public void onError() {
        showFailToast("打开相机错误~");
        close();
    }

    @Override
    public void audioPermissionError() {
        showFailToast("请开启相机权限~");
        close();
    }
}