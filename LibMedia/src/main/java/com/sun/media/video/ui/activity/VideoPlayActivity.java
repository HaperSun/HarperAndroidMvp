package com.sun.media.video.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.TDevice;
import com.sun.media.R;
import com.sun.media.databinding.ActivityVidoPalyBinding;
import com.sun.media.video.model.SuperPlayerDef;
import com.sun.media.video.model.SuperPlayerModel;
import com.sun.media.video.ui.view.SuperPlayerView;

/**
 * @author: Harper
 * @date: 2022/6/23
 * @note: 视频播放
 */
public class VideoPlayActivity extends BaseMvpActivity implements SuperPlayerView.OnSuperPlayerViewCallback {

    private ActivityVidoPalyBinding bind;
    private boolean mIsFullScreen = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_vido_paly;
    }

    @Override
    public void initView() {
        bind = (ActivityVidoPalyBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        bind.superPlayerView.setPlayerViewCallback(this);
        startPlayVideo();
    }

    private void startPlayVideo() {
        SuperPlayerModel playerModel = new SuperPlayerModel();
        playerModel.url = "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/287432344564972819219071668/master_playlist.m3u8";
        playerModel.placeholderImage = "http://1252463788.vod2.myqcloud.com/e12fcc4dvodgzp1252463788/287432344564972819219071668/4564972819212551204.jpeg";
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
        onBackPressed();
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

    @Override
    public void onBackPressed() {
        if (mIsFullScreen){
            bind.superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW);
            //当从全屏模式返回窗口模式时，SuperPlayerView会clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            //导致窗口模式的全屏失效，需要重新设置下
            TDevice.setFullScreen(this);
        }else {
            close();
        }
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