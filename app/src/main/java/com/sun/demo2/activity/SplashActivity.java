package com.sun.demo2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sun.common.UiHandler;
import com.sun.demo2.R;

/**
 * @author: Harper
 * @date: 2021/12/28
 * @note: 引导页
 */
public class SplashActivity extends AppCompatActivity {

    private final IHandler mHandler = new IHandler(this);
    private FrameLayout mRootLayout;

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
        //为了解决有时按HOME键再回来会重启问题
        boolean isActivityTaskRoot = isActivityTaskRoot();
        if (!isActivityTaskRoot) {
            finish();
            return;
        }
        setFullScreen();
        mHandler.post(mRunnable);
//        toHomepage();
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