package com.sun.demo2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.idl.facesdk.FaceTracker;
import com.sun.common.UiHandler;
import com.sun.common.toast.ToastHelper;
import com.sun.demo2.R;
import com.sun.face.exception.FaceError;
import com.sun.face.model.AccessToken;
import com.sun.face.other.FaceEnvironment;
import com.sun.face.other.FaceSDKManager;
import com.sun.face.ui.service.APIService;
import com.sun.face.util.OnResultListener;

/**
 * @author: Harper
 * @date: 2021/12/28
 * @note: 引导页
 */
public class SplashActivity extends AppCompatActivity {

    private final IHandler mHandler = new IHandler(this);
    private FrameLayout mRootLayout;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //热启动：隐藏状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        setContentView(R.layout.activity_splash);
        mRootLayout = findViewById(R.id.root_layout);
        mContext = this;
        //为了解决有时按HOME键再回来会重启问题
        boolean isActivityTaskRoot = isActivityTaskRoot();
        if (!isActivityTaskRoot) {
            finish();
            return;
        }
        setFullScreen();
        mHandler.post(mRunnable);
//        toHomepage();
        //初始化百度人脸识别SDK
        initBdFaceSdk();
    }

    private void initBdFaceSdk() {
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().init(mContext, mContext.getString(R.string.bd_license_id),
                mContext.getString(R.string.bd_license_file_name));
        FaceTracker tracker = FaceSDKManager.getInstance().getFaceTracker(this);
        //.getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整

        // 模糊度范围 (0-1) 推荐小于0.7
        tracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS);
        // 光照范围 (0-1) 推荐大于40
        tracker.set_illum_thr(FaceEnvironment.VALUE_BRIGHTNESS);
        // 裁剪人脸大小
        tracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        tracker.set_eulur_angle_thr(FaceEnvironment.VALUE_HEAD_PITCH, FaceEnvironment.VALUE_HEAD_ROLL,
                FaceEnvironment.VALUE_HEAD_YAW);

        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        tracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        //
        tracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        tracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION);
        // 是否进行质量检测
        tracker.set_isCheckQuality(true);
        // 是否进行活体校验
        tracker.set_isVerifyLive(false);

        APIService.getInstance().init(this);
        APIService.getInstance().setGroupId(mContext.getString(R.string.bd_group_id));
        // 用ak，sk获取token, 调用在线api，如：注册、识别等。为了ak、sk安全，建议放您的服务器，
        APIService.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                Log.i("wtf", "AccessToken->" + result.getAccessToken());
                mHandler.post(() -> ToastHelper.showCommonToast("启动成功"));
            }

            @Override
            public void onError(FaceError error) {
                Log.e("xx", "AccessTokenError:" + error);
                error.printStackTrace();

            }
        }, this, mContext.getString(R.string.bd_api_key), mContext.getString(R.string.bd_secret_key));

    }

    private boolean isActivityTaskRoot() {
        if (!isTaskRoot()) {
            return false;
        }
        return getIntent() == null || (getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) == 0;
    }

    private void setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setRootView(this);
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 去首页
     */
    private void toHomepage() {
        //这种延时操作会产生不合理的现象，如果立即返回后还是会出现跳转到首页的现象
        mRootLayout.postDelayed(() -> {
            HomepageActivity.start(this);
            finish();
        }, 1000);
    }

    private final Runnable mRunnable = () -> mHandler.sendEmptyMessageDelayed(0, 1000);

    private static class IHandler extends UiHandler<SplashActivity> {

        public IHandler(SplashActivity cla) {
            super(cla);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = getRef();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (0 == msg.what) {
                HomepageActivity.start(activity);
                activity.finish();
            }
        }
    }
}