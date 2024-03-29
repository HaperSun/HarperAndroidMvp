package com.sun.media.camera.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.sun.base.util.FileUtil;
import com.sun.base.util.LogHelper;
import com.sun.base.util.ScreenUtil;
import com.sun.media.R;
import com.sun.media.camera.i.ICameraListener;
import com.sun.media.camera.i.ICameraView;
import com.sun.media.camera.i.ICaptureListener;
import com.sun.media.camera.i.IClickListener;
import com.sun.media.camera.i.IErrorListener;
import com.sun.media.camera.i.ITypeListener;
import com.sun.media.camera.state.CameraMachine;

import java.io.IOException;


/**
 * @author: Harper
 * @date: 2022/7/29
 * @note:
 */
public class CameraView extends FrameLayout implements CameraInterface.CameraOpenOverCallback, SurfaceHolder.Callback, ICameraView {

    //Camera状态机
    private CameraMachine machine;
    //闪关灯状态
    private static final int TYPE_FLASH_AUTO = 0x021;
    private static final int TYPE_FLASH_ON = 0x022;
    private static final int TYPE_FLASH_OFF = 0x023;
    private int typeFlash = TYPE_FLASH_OFF;
    //拍照浏览时候的类型
    public static final int TYPE_PICTURE = 0x001;
    public static final int TYPE_VIDEO = 0x002;
    public static final int TYPE_SHORT = 0x003;
    public static final int TYPE_DEFAULT = 0x004;
    //录制视频比特率
    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;
    //只能拍照
    public static final int TAKE_PHOTO = 0x101;
    //只能录像
    public static final int TAKE_VIDEO = 0x102;
    //两者都可以
    public static final int TAKE_BOTH = 0x103;
    //回调监听
    private ICameraListener cameraListener;
    private IClickListener leftClickListener;
    private IClickListener rightClickListener;
    private final Context mContext;
    private VideoView mVideoView;
    private ImageView mPhoto;
    private ImageView mSwitchCamera;
    private ImageView mFlashLamp;
    private CaptureLayout mCaptureLayout;
    private FocusView mFocusView;
    private MediaPlayer mMediaPlayer;
    private int layoutWidth;
    private float screenProp = 0f;
    //捕获的图片
    private Bitmap captureBitmap;
    //第一帧图片
    private Bitmap firstFrame;
    //视频URL
    private String videoUrl;
    //切换摄像头按钮的参数
    //图标大小
    private int iconSize = 0;
    //右上边距
    private int iconMargin = 0;
    //图标资源
    private int iconSrc = 0;
    //左图标
    private int iconLeft = 0;
    //右图标
    private int iconRight = 0;
    //录制时间
    private int duration = 0;
    //缩放梯度
    private int zoomGradient = 0;
    private boolean firstTouch = true;
    private float firstTouchLength = 0;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr, 0);
        iconSize = a.getDimensionPixelSize(R.styleable.CameraView_iconSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        iconMargin = a.getDimensionPixelSize(R.styleable.CameraView_iconMargin, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        iconSrc = a.getResourceId(R.styleable.CameraView_iconSrc, R.drawable.ic_camera);
        iconLeft = a.getResourceId(R.styleable.CameraView_iconLeft, 0);
        iconRight = a.getResourceId(R.styleable.CameraView_iconRight, 0);
        //没设置默认为10s
        duration = a.getInteger(R.styleable.CameraView_duration_max, 10 * 1000);
        a.recycle();
        initData();
        initView();
    }

    private void initData() {
        layoutWidth = ScreenUtil.getScreenWidth();
        //缩放梯度
        zoomGradient = (int) (layoutWidth / 16f);
        LogHelper.i("zoom = " + zoomGradient);
        machine = new CameraMachine(getContext(), this, this);
    }

    private void initView() {
        setWillNotDraw(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.camera_view, this);
        mVideoView = (VideoView) view.findViewById(R.id.video_preview);
        mPhoto = (ImageView) view.findViewById(R.id.image_photo);
        mSwitchCamera = (ImageView) view.findViewById(R.id.image_switch);
        mSwitchCamera.setImageResource(iconSrc);
        mFlashLamp = (ImageView) view.findViewById(R.id.image_flash);
        setFlashRes();
        mFlashLamp.setOnClickListener(v -> {
            typeFlash++;
            if (typeFlash > 0x023) {
                typeFlash = TYPE_FLASH_AUTO;
            }
            setFlashRes();
        });
        mCaptureLayout = (CaptureLayout) view.findViewById(R.id.capture_layout);
        mCaptureLayout.setDuration(duration);
        mCaptureLayout.setIconSrc(iconLeft, iconRight);
        mFocusView = (FocusView) view.findViewById(R.id.fouce_view);
        mVideoView.getHolder().addCallback(this);
        //切换摄像头
        mSwitchCamera.setOnClickListener(v -> switchCamera());
        //拍照 录像
        mCaptureLayout.setCaptureListener(new ICaptureListener() {
            @Override
            public void takePictures() {
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                machine.capture();
            }

            @Override
            public void recordStart() {
                mSwitchCamera.setVisibility(INVISIBLE);
                mFlashLamp.setVisibility(INVISIBLE);
                machine.record(mVideoView.getHolder().getSurface(), screenProp);
            }

            @Override
            public void recordShort(final long time) {
                mCaptureLayout.setTextWithAnimation("录制时间过短");
                mSwitchCamera.setVisibility(VISIBLE);
                mFlashLamp.setVisibility(VISIBLE);
                postDelayed(() -> machine.stopRecord(true, time), 1500 - time);
            }

            @Override
            public void recordEnd(long time) {
                machine.stopRecord(false, time);
            }

            @Override
            public void recordZoom(float zoom) {
                LogHelper.i("recordZoom");
                machine.zoom(zoom, CameraInterface.TYPE_RECORDER);
            }

            @Override
            public void recordError() {
                if (errorListener != null) {
                    errorListener.audioPermissionError();
                }
            }
        });
        //确认 取消
        mCaptureLayout.setTypeListener(new ITypeListener() {
            @Override
            public void cancel() {
                machine.cancel(mVideoView.getHolder(), screenProp);
            }

            @Override
            public void confirm() {
                machine.confirm();
            }
        });
        //退出
//        mCaptureLayout.setReturnLisenter(new ReturnListener() {
//            @Override
//            public void onReturn() {
//                if (jCameraLisenter != null) {
//                    jCameraLisenter.quit();
//                }
//            }
//        });
        mCaptureLayout.setLeftClickListener(() -> {
            if (leftClickListener != null) {
                leftClickListener.onClick();
            }
        });
        mCaptureLayout.setRightClickListener(() -> {
            if (rightClickListener != null) {
                rightClickListener.onClick();
            }
        });
    }

    public void switchCamera(){
        machine.onSwitch(mVideoView.getHolder(), screenProp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = mVideoView.getMeasuredWidth();
        float heightSize = mVideoView.getMeasuredHeight();
        if (screenProp == 0) {
            screenProp = heightSize / widthSize;
        }
    }

    @Override
    public void cameraHasOpened() {
        CameraInterface.getInstance().doStartPreview(mVideoView.getHolder(), screenProp);
    }

    //生命周期onResume
    public void onResume() {
        LogHelper.i("CameraView onResume");
        //重置状态
        resetState(TYPE_DEFAULT);
        CameraInterface.getInstance().registerSensorManager(mContext);
        CameraInterface.getInstance().setSwitchView(mSwitchCamera, mFlashLamp);
        machine.start(mVideoView.getHolder(), screenProp);
    }

    //生命周期onPause
    public void onPause() {
        LogHelper.i("CameraView onPause");
        stopVideo();
        resetState(TYPE_PICTURE);
        CameraInterface.getInstance().isPreview(false);
        CameraInterface.getInstance().unregisterSensorManager(mContext);
    }

    //SurfaceView生命周期
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogHelper.i("CameraView SurfaceCreated");
        new Thread() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(CameraView.this);
            }
        }.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogHelper.i("CameraView SurfaceDestroyed");
        CameraInterface.getInstance().doDestroyCamera();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    //显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2) {
                    Log.i("CJT", "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2) {
                    //第一个点
                    float point1X = event.getX(0);
                    float point1Y = event.getY(0);
                    //第二个点
                    float point2X = event.getX(1);
                    float point2Y = event.getY(1);
                    float result = (float) Math.sqrt(Math.pow(point1X - point2X, 2) + Math.pow(point1Y - point2Y, 2));
                    if (firstTouch) {
                        firstTouchLength = result;
                        firstTouch = false;
                    }
                    if ((int) (result - firstTouchLength) / zoomGradient != 0) {
                        firstTouch = true;
                        machine.zoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }
//                    Log.i("CJT", "result = " + (result - firstTouchLength));
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        return true;
    }

    //对焦框指示器动画
    private void setFocusViewWidthAnimation(float x, float y) {
        machine.focus(x, y, () -> mFocusView.setVisibility(INVISIBLE));
    }

    private void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            videoViewParam.gravity = Gravity.CENTER;
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    /**************************************************
     * 对外提供的API                     *
     **************************************************/

    public void setSaveVideoPath(String path) {
        CameraInterface.getInstance().setSaveVideoPath(path);
    }

    public void setCameraListener(ICameraListener cameraListener) {
        this.cameraListener = cameraListener;
    }

    private IErrorListener errorListener;

    //启动Camera错误回调
    public void setErrorListener(IErrorListener errorListener) {
        this.errorListener = errorListener;
        CameraInterface.getInstance().setErrorListener(errorListener);
    }

    //设置CaptureButton功能（拍照和录像）
    public void setCameraType(int state) {
        this.mCaptureLayout.setCameraType(state);
    }

    //设置录制质量
    public void setMediaQuality(int quality) {
        CameraInterface.getInstance().setMediaQuality(quality);
    }

    @Override
    public void resetState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                stopVideo();
                //停止播放
                //初始化VideoView
                FileUtil.delete(videoUrl);
                mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                machine.start(mVideoView.getHolder(), screenProp);
                break;
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                break;
            case TYPE_SHORT:
                break;
            case TYPE_DEFAULT:
                mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                break;
        }
        mSwitchCamera.setVisibility(VISIBLE);
        mFlashLamp.setVisibility(VISIBLE);
        mCaptureLayout.resetCaptureLayout();
    }

    @Override
    public void confirmState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                //停止播放
                stopVideo();
                mVideoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                machine.start(mVideoView.getHolder(), screenProp);
                if (cameraListener != null) {
                    cameraListener.recordSuccess(videoUrl, firstFrame);
                }
                break;
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                if (cameraListener != null) {
                    cameraListener.captureSuccess(captureBitmap);
                }
                break;
            case TYPE_SHORT:
                break;
            case TYPE_DEFAULT:
                break;
            default:
                break;
        }
        mCaptureLayout.resetCaptureLayout();
    }

    @Override
    public void showPicture(Bitmap bitmap, boolean isVertical) {
        if (isVertical) {
            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            mPhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        captureBitmap = bitmap;
        mPhoto.setImageBitmap(bitmap);
        mPhoto.setVisibility(VISIBLE);
        mCaptureLayout.startAlphaAnimation();
        mCaptureLayout.startTypeBtnAnimator();
    }

    @Override
    public void playVideo(Bitmap firstFrame, final String url) {
        videoUrl = url;
        CameraView.this.firstFrame = firstFrame;
        new Thread(() -> {
            try {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                } else {
                    mMediaPlayer.reset();
                }
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
                mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
                        .getVideoHeight()));
                mMediaPlayer.setOnPreparedListener(mp -> mMediaPlayer.start());
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void stopVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void setTip(String tip) {
        mCaptureLayout.setTip(tip);
    }

    @Override
    public void startPreviewCallback() {
        LogHelper.i("startPreviewCallback");
        handlerFocus(mFocusView.getWidth() / 2, mFocusView.getHeight() / 2);
    }

    @Override
    public boolean handlerFocus(float x, float y) {
        if (y > mCaptureLayout.getTop()) {
            return false;
        }
        mFocusView.setVisibility(VISIBLE);
        if (x < mFocusView.getWidth() / 2) {
            x = mFocusView.getWidth() / 2;
        }
        if (x > layoutWidth - mFocusView.getWidth() / 2) {
            x = layoutWidth - mFocusView.getWidth() / 2;
        }
        if (y < mFocusView.getWidth() / 2) {
            y = mFocusView.getWidth() / 2;
        }
        if (y > mCaptureLayout.getTop() - mFocusView.getWidth() / 2) {
            y = mCaptureLayout.getTop() - mFocusView.getWidth() / 2;
        }
        mFocusView.setX(x - mFocusView.getWidth() / 2);
        mFocusView.setY(y - mFocusView.getHeight() / 2);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.4f, 1f, 0.4f, 1f, 0.4f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
        return true;
    }

    public void setLeftClickListener(IClickListener IClickListener) {
        this.leftClickListener = IClickListener;
    }

    public void setRightClickListener(IClickListener IClickListener) {
        this.rightClickListener = IClickListener;
    }

    private void setFlashRes() {
        switch (typeFlash) {
            case TYPE_FLASH_AUTO:
                mFlashLamp.setImageResource(R.drawable.ic_flash_auto);
                machine.flash(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case TYPE_FLASH_ON:
                mFlashLamp.setImageResource(R.drawable.ic_flash_on);
                machine.flash(Camera.Parameters.FLASH_MODE_ON);
                break;
            case TYPE_FLASH_OFF:
                mFlashLamp.setImageResource(R.drawable.ic_flash_off);
                machine.flash(Camera.Parameters.FLASH_MODE_OFF);
                break;
        }
    }
}
