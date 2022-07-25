package com.sun.media.video.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sun.base.util.LogHelper;
import com.sun.media.R;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author: Harper
 * @date: 2022/7/21
 * @note: 视频录制控件
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener, OnTouchListener {

    public static final String TAG = "MovieRecorderView";

    /**
     * 默认配置
     */
    // 视频分辨率宽度
    private int mWidth = 1280;
    // 视频分辨率高度
    private int mHeight = 720;
    // 是否一开始就打开摄像头
    private boolean isOpenCamera = true;
    // 进度条精确倍数，越大越精确，需要是1000的约数
    public static int PROGRESS_FRAM = 20;
    //前置摄像头标记
    public static final int FRONT = 1;
    //后置摄像头标记
    private static final int BACK = 2;
    //当前打开的摄像头标记 默认后置摄像头
    private int currentCameraType = BACK;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Parameters mCameraParameters;
    /**
     * 录制相关
     */
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    /**
     * 开始状态回调接口
     */
    @SuppressWarnings("unused")
    private OnRecordStateListener mOnRecordStateListener;
    // 时间计数
    private int mTimeCount;
    // 计时器
    private ScheduledExecutorService mExecutorService;
    /**
     * 展示倒计时的进度条
     */
    private ProgressBar mProgressBar = null;
    /**
     * 播放相关
     */
    private MediaPlayer player = null;
    // 文件
    private File mVecordFile = null;

    public MovieRecorderView(Context context) {
        this(context, null);

    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("deprecation")
    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_movie_recorder, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        //可以不用设置
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 自定义回调
     */
    private class CustomCallBack implements Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera) {
                return;
            }
            try {
                if (player == null) {
                    initCamera();
                } else {
                    player.setDisplay(mSurfaceHolder);
                    initPlay();
                }

            } catch (IOException e) {
                e.printStackTrace();
                // 报警：相机设备没有找到或正在被使用
                return;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera) {
                return;
            }
            freeCameraResource();
        }

    }

    /**
     * 初始化摄像头
     */
    public void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
//            mCamera = Camera.open();
            mCamera = openCamera(currentCameraType);
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null) {
            return;
        }
        setCameraParams();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();
//        mCamera.lock();
        mCamera.unlock();
    }

    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }
        currentCameraType = type;
        if (type == FRONT && frontIndex != -1) {
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            return Camera.open(backIndex);
        }
        return null;
    }

    public void changeCamera() throws Exception {
        createRecordDir();
        if (mCamera != null) {
            freeCameraResource();
        }
        if (currentCameraType == FRONT) {
            mCamera = openCamera(BACK);
        } else if (currentCameraType == BACK) {
            mCamera = openCamera(FRONT);
        }
        setCameraParams();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();
//        mCamera.lock();
        mCamera.unlock();
    }

    public int getCurrentCameraType() {
        return currentCameraType;
    }

    public void setCurrentCameraType(int currentCameraType) {
        this.currentCameraType = currentCameraType;
    }

    /**
     * 设置摄像头为竖屏
     */
    private void setCameraParams() {
        try {
            if (mCamera != null) {
                mCameraParameters = mCamera.getParameters();
                mCameraParameters.set("orientation", "portrait");
                // params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mCameraParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                mCamera.setParameters(mCameraParameters);
            }
        } catch (Exception e) {
            if (mCamera != null) {
                mCameraParameters = mCamera.getParameters();
                mCameraParameters.set("orientation", "portrait");
                // params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                // params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
                mCamera.setParameters(mCameraParameters);
            }
        }
    }

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
//            mCamera.unlock();N
            mCamera.lock();
            try {
                mCamera.release();
            } catch (RuntimeException e) {
                LogHelper.e(TAG, "摄像头 ->" + e);
            }
            mCamera = null;
            mCameraParameters = null;
        }
    }

    private void createRecordDir() {
//        File sampleDir = FileUtil.getExternalFilesDir(mContext, "iflytek" + File.separator + "Video" + File.separator);
//        File sampleDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        File vecordDir = sampleDir;
        // 创建文件
        try {
//            mVecordFile = File.createTempFile("recording", AppConstants.VIDEO_FILE_SUFFIX, vecordDir);// mp4格式
            mVecordFile = File.createTempFile("recording", ".mp4", vecordDir);// mp4格式
            // mVecordFile = new
            // File(Environment.getExternalStorageDirectory().getAbsolutePath()
            // + "/video1216.mp4");
            mVecordFile.deleteOnExit();
            mVecordFile.createNewFile();
            // Log.e("path",mVecordFile.getAbsolutePath());
        } catch (IOException e) {
        }
    }

    /**
     * 初始化
     */
    private void initRecord() throws IOException {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOnErrorListener(this);
        } else {
            mMediaRecorder.reset();
        }
        if (mCamera != null) {
            //解开当前的相机 让程序也可以使用但是这里觉得没必要可能以后需要把
//            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
        }
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
        mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式
        // modify by hongyu 增加低配手机视频播放参数录制兼容
        CamcorderProfile mProfile = null;
        try {
            // 设置视频输出的格式和编码
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            } else {
                mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            }
        } catch (Exception e) {
//			LogUtil.error(TAG, "camera hardware not support detail " + e.getMessage());
//			ToastUtil.showErrorToast(mContext, "摄像头不支持");
            return;
        }

        if (mProfile.videoBitRate > 900 * 1024) {
            mMediaRecorder.setVideoEncodingBitRate(900 * 1024);
        } else {
            mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
        }
        mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率
        if (currentCameraType == BACK) {
            mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        } else {
            mMediaRecorder.setOrientationHint(270);// 输出旋转270度，保持竖屏录制
        }
        mMediaRecorder.setAudioEncodingBitRate(44100);
//        mMediaRecorder.setAudioSamplingRate(44); // 设置音频采样率为44
//        mMediaRecorder.setAudioEncodingBitRate(64); // 设置音频比特率为64
        mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);  //默认的这个25
        mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);// 音频格式 修改之前的amr格式
        // 和音频录制编码格式保持一致
        mMediaRecorder.setVideoEncoder(VideoEncoder.H264);// 视频录制格式MPEG_4_SP
        mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the mVecordFile
     */
    public File getmVecordFile() {
        return mVecordFile;
    }

    public void setmVecordFile(File mVecordFile) {
        this.mVecordFile = null;
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null) {
                mr.reset();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得总录制时间
     *
     * @return
     */
    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * 录制状态回调接口
     */
    public interface OnRecordStateListener {
        /**
         * 开始录制
         */
        public void onRecordStart();

        /**
         * 录制完成
         */
        public void onRecordFinish();
    }

    /**
     * 开始录制视频 注意这个是耗时操作
     *
     * @param onRecordStateListener 达到指定时间之后回调接口
     */
    public void record(final OnRecordStateListener onRecordStateListener, final int maxTime) {
        this.mOnRecordStateListener = onRecordStateListener;
        createRecordDir();
        try {
            if (!isOpenCamera) {
                // 如果未打开摄像头，则打开
                initCamera();
            }
            initRecord();
            // 时间计数器重新赋值
            mTimeCount = 0;
            if (mExecutorService == null) {
                mExecutorService = new ScheduledThreadPoolExecutor(1,
                        new BasicThreadFactory.Builder().namingPattern(TAG).daemon(true).build());
            }
            mExecutorService.scheduleAtFixedRate(() -> {
                mTimeCount++;
                if (mProgressBar != null) {
                    // 设置进度条
                    mProgressBar.setProgress(mTimeCount);
                }
                // 达到指定时间，停止拍摄
                if (mTimeCount >= maxTime * PROGRESS_FRAM) {
                    if (mOnRecordStateListener != null) {
                        mOnRecordStateListener.onRecordFinish();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mOnRecordStateListener != null) {
            mOnRecordStateListener.onRecordStart();
        }
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public void resumePlay() {
        try {
            mMediaRecorder.setPreviewDisplay(null);
            // 设置显示视频显示在SurfaceView上
            player.setDisplay(mSurfaceHolder);
            try {
                player.setDataSource(mVecordFile.getAbsolutePath());
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 初始化播放资源
     */
    public void initPlay() {
        if (player == null) {
            Log.d(TAG, "initPlay, player: " + player);
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置显示视频显示在SurfaceView上
            player.setDisplay(mSurfaceHolder);
        }
        try {
            player.setDataSource(mVecordFile.getAbsolutePath());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放刚才录制的视频
     */
    public void startPlay() {
        player.start();
        player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                player.stop();
                player.reset();
            }
        });
    }

    /**
     * 返回播放器状态
     *
     * @return
     */
    public boolean isPlaying() {
        if (player != null) {
            return player.isPlaying();
        }
        return false;
    }

    /**
     * 暂停
     */
    public void onPause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        player.stop();
        player.reset();
        try {
            player.setDataSource(mVecordFile.getAbsolutePath());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放播放器资源
     */
    public void releasePlay() {
        try {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 对焦
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void submitFocusAreaRect(final Rect touchRect) {
        Parameters cameraParameters = mCamera.getParameters();
        if (cameraParameters == null
                || cameraParameters.getMaxNumFocusAreas() == 0) {
            return;
        }

        // Convert from View's width and height to +/- 1000
        Rect focusArea = new Rect();
        focusArea.set(touchRect.left * 2000 / mSurfaceView.getWidth() - 1000,
                touchRect.top * 2000 / mSurfaceView.getHeight() - 1000,
                touchRect.right * 2000 / mSurfaceView.getWidth() - 1000,
                touchRect.bottom * 2000 / mSurfaceView.getHeight() - 1000);
        // Submit focus area to camera
        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(focusArea, 1000));
        cameraParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        cameraParameters.setFocusAreas(focusAreas);
        try {
            mCamera.setParameters(cameraParameters);
            mCamera.autoFocus(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            float touchMajor = event.getTouchMajor();
            float touchMinor = event.getTouchMinor();

            Rect touchRect = new Rect((int) (x - touchMajor / 2),
                    (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
                    (int) (y + touchMinor / 2));

            this.submitFocusAreaRect(touchRect);
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBitmap(Bitmap bitmap) {
        mSurfaceView.setBackground(new BitmapDrawable(bitmap));
    }

    /**
     * 开启或关闭手电筒
     *
     * @param isOn true代表开启，false代表关闭
     */
    public void switchTorch(boolean isOn) {
        if (mCamera == null) {
            return;
        }
        try {
            if (isOn) {
                mCameraParameters = mCamera.getParameters();
                mCameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(mCameraParameters);
            } else {
                mCameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(mCameraParameters);
            }

        } catch (Exception e) {
            LogHelper.e(TAG, "exception", e);
        }
    }
}