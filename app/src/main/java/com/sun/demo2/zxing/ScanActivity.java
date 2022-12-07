package com.sun.demo2.zxing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.zxing.Result;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityScanBinding;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author: Harper
 * @date: 2022/3/22
 * @note: 二维码扫描页面
 */
public class ScanActivity extends BaseMvpActivity<ActivityScanBinding> implements SurfaceHolder.Callback {

    private static final String SCAN_TYPE = "scanType";
    private int mScanType;
    private boolean mIsHasSurface = false;
    private boolean mIsFlightOpen = false;
    private Camera mCamera;
    private InactivityTimer mInactivityTimer;
    private BeepManager mBeepManager;
    private CameraManager mCameraManager;
    private CaptureActivityHandler mHandler;
    private Rect mCropRect = null;

    public static void start(Context context, int scanType) {
        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra(SCAN_TYPE, scanType);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_scan;
    }

    @Override
    public void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mScanType = intent.getIntExtra(SCAN_TYPE, 0);
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        vdb.tvFlight.setOnClickListener(v -> {
            if (mIsFlightOpen) {
                closeLight();
                vdb.tvFlight.setSelected(false);
            } else {
                openLight();
                vdb.tvFlight.setSelected(true);
            }
            mIsFlightOpen = !mIsFlightOpen;
        });
        mInactivityTimer = new InactivityTimer(this);
        mBeepManager = new BeepManager(this);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                1.0f);
        animation.setDuration(1500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        vdb.captureScanLine.startAnimation(animation);
    }

    /**
     * 开闪光灯
     */
    private void openLight() {
        try {
            //我们先前在CameraManager类中添加的静态方法
            mCamera = CameraManager.getCamera();
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闪光灯
     */
    private void closeLight() {
        try {
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        mCameraManager = new CameraManager(getApplication());

        mHandler = null;

        if (mIsHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(vdb.surfaceView.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            vdb.surfaceView.getHolder().addCallback(this);
        }

        mInactivityTimer.onResume();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        if (mBeepManager != null) {
            mBeepManager.close();
        }
        if (mHandler != null) {
            mHandler = null;
        }
        if (mCameraManager != null) {
            mCameraManager.closeDriver();
            mCameraManager.stopPreview();
        }
        vdb.surfaceView.getHolder().removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!mIsHasSurface) {
            mIsHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        mInactivityTimer.onActivity();
        mBeepManager.playBeepSoundAndVibrate();
        //页面跳转
//        bundle.putInt("width", mCropRect.width());
//        bundle.putInt("height", mCropRect.height());
//        bundle.putString("result", rawResult.getText());
//        bundle.putInt("scanType", mScanType);
//        startActivity(new Intent(ScanActivity.this, ResultActivity.class).putExtras(bundle));
//        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, mCameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("相机打开出错，请稍后重试");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        // 获取布局中扫描框的位置信息
        int[] location = new int[2];
        vdb.captureCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = vdb.captureCropView.getWidth();
        int cropHeight = vdb.captureCropView.getHeight();

        //获取布局容器的宽高
        int containerWidth = vdb.captureCropView.getWidth();
        int containerHeight = vdb.captureCropView.getHeight();

        //计算最终截取的矩形的左上角顶点x坐标
        int x = cropLeft * cameraWidth / containerWidth;
        //计算最终截取的矩形的左上角顶点y坐标
        int y = cropTop * cameraHeight / containerHeight;

        //计算最终截取的矩形的宽度
        int width = cropWidth * cameraWidth / containerWidth;
        //计算最终截取的矩形的高度
        int height = cropHeight * cameraHeight / containerHeight;

        //生成最终的截取的矩形
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }
}