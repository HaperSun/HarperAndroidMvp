package com.sun.media.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.sun.media.img.MediaSelector;
import com.sun.media.img.ui.activity.ImageEditActivity;
import com.sun.media.video.ui.activity.VideoEditActivity;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note: 拍照
 */
public class CameraActivity extends BaseMvpActivity<ActivityCameraBinding> implements ICameraListener, IErrorListener {

    private int mTakeType;
    private boolean mSwitchCamera;

    /**
     * 通过Activity启动
     *
     * @param activity    activity
     * @param requestCode requestCode
     * @param takeType    takeType
     */
    public static void startActivityResult(Activity activity, int requestCode, int takeType) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(Parameter.TYPE, takeType);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 通过Fragment启动
     *
     * @param fragment    fragment
     * @param requestCode requestCode
     * @param takeType    takeType
     */
    public static void startActivityResult(Fragment fragment, int requestCode, int takeType) {
        Intent intent = new Intent(fragment.getContext(), CameraActivity.class);
        intent.putExtra(Parameter.TYPE, takeType);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mTakeType = intent.getIntExtra(Parameter.TYPE, CameraView.TAKE_PHOTO);
        }
        mSwitchCamera = MediaSelector.getInstance().config.switchCamera;
    }

    @Override
    public void initView() {
        if (!PermissionUtil.checkCamera()) {
            PermissionUtil.requestCamera(this, state -> {
                if (!state) {
                    showFailToast("请到设置-权限管理中开启相关权限~");
                    close();
                }
            });
        }
    }

    @Override
    public void initData() {
        //设置拍摄类型
        vdb.cameraView.setCameraType(mTakeType);
        //设置视频保存路径
        vdb.cameraView.setSaveVideoPath(FileUtil.getMediaFileName());
        //JCameraView监听
        vdb.cameraView.setCameraListener(this);
        vdb.cameraView.setErrorListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSwitchCamera) {
            vdb.cameraView.switchCamera();
        }
        vdb.cameraView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mSwitchCamera){
            vdb.cameraView.switchCamera();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vdb.cameraView.onPause();
    }

    @Override
    public void captureSuccess(Bitmap bitmap) {
        String path = FileUtil.saveBitmapAsPicture(bitmap);
        ArrayList<MediaFile> mediaFiles = new ArrayList<>();
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFolderName("temp");
        mediaFile.setMime("image/jpg");
        mediaFile.setPath(path);
        mediaFile.setWidth(bitmap.getWidth());
        mediaFile.setHeight(bitmap.getHeight());
        bitmap.recycle();
        mediaFile.setItemType(MediaFile.PHOTO);
        mediaFiles.add(mediaFile);
        DataUtil.getInstance().setMediaData(mediaFiles);
        ImageEditActivity.startForResult(this, Parameter.REQUEST_CODE_MEDIA, 0);
    }

    /**
     * @param url        视频本地路径
     * @param firstFrame 视频第一帧
     */
    @Override
    public void recordSuccess(String url, Bitmap firstFrame) {
        //去视频预览页面的数据
        ArrayList<MediaFile> mediaFiles = new ArrayList<>();
        MediaFile mediaFile = new MediaFile();
        mediaFile.setMime("mp4");
        mediaFile.setPath(url);
        mediaFile.setDuration(FileUtil.getVideoDuration(url));
        mediaFile.setItemType(MediaFile.VIDEO);
        mediaFiles.add(mediaFile);
        DataUtil.getInstance().setMediaData(mediaFiles);
        VideoEditActivity.startForResult(this, Parameter.REQUEST_CODE_MEDIA);
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
}