package com.sun.video.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.video.R;
import com.sun.video.databinding.ActivityVidoPalyerBinding;
import com.sun.video.model.SuperPlayerDef;
import com.sun.video.model.SuperPlayerModel;
import com.sun.video.ui.view.SuperPlayerView;

/**
 * @author: Harper
 * @date: 2022/6/23
 * @note:
 */
public class VideoPlayerActivity extends BaseMvpActivity implements SuperPlayerView.OnSuperPlayerViewCallback {

    private ActivityVidoPalyerBinding bind;
    private boolean mIsFullScreen = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_vido_palyer;
    }

    @Override
    public void initView() {
        bind = (ActivityVidoPalyerBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        bind.superPlayerView.setPlayerViewCallback(this);
        startPlayVideo();
    }

    private void startPlayVideo() {
        SuperPlayerModel playerModel = new SuperPlayerModel();
        playerModel.url = "http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba900387702294394228858/adp.10.m3u8";
        playerModel.placeholderImage = "http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba900387702294394228858/coverBySnapshot/coverBySnapshot_10_0.jpg";
        playerModel.title = "小直播 - 主播连麦";
        bind.superPlayerView.playWithModel(playerModel);
    }

    @Override
    public void onStartFullScreenPlay() {
        //全屏
        mIsFullScreen = true;
    }

    @Override
    public void onStopFullScreenPlay() {
        //退出全屏
        mIsFullScreen = false;
    }

    @Override
    public void onClickFloatCloseBtn() {
        //点击浮窗模式关闭按钮，那么结束整个播放
        bind.superPlayerView.resetPlayer();
        finish();
    }

    @Override
    public void onClickWindowCloseBtn() {
        //点击小窗模式下返回按钮
        close();
        //可以开始浮窗模式播放
//        showFloatWindow();
    }

    @Override
    public void onStartFloatPlay() {
        //开始悬浮播放后，直接返回到桌面，进行悬浮播放
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        IntentUtils.safeStartActivity(mContext, intent);
    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onPlayEnd() {

    }

    @Override
    public void onError(int code) {

    }

    /**
     * 悬浮窗播放
     */
    private void showFloatWindow() {
        if (bind.superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING) {
            bind.superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.FLOAT);
        } else {
            bind.superPlayerView.resetPlayer();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind.superPlayerView.onResume();
        if (mIsFullScreen){
            //隐藏虚拟按键，并且全屏
            View decorView = getWindow().getDecorView();
            if (decorView == null) {
                return;
            }
            // lower api
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                decorView.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bind.superPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.superPlayerView.release();
    }
}