package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityVideoPlayerBinding;
import com.sun.media.controller.TCControllerWindow;
import com.sun.media.model.SuperPlayerConst;
import com.sun.media.model.SuperPlayerModel;
import com.sun.media.view.SuperPlayerView;

/**
 * @author: Harper
 * @date: 2022/5/24
 * @note: 视频播放
 */
public class VideoPlayerActivity extends BaseMvpActivity implements SuperPlayerView.OnSuperPlayerViewCallback {

    private ActivityVideoPlayerBinding bind;

    public static void start(Context context) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initView() {
        bind = (ActivityVideoPlayerBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        bind.videoPlayer.setPlayerViewCallback(this);
        bind.videoPlayer.playWithModel(new SuperPlayerModel("http://qiniu.fxgkpt.com/1639724529402.mp4"),10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bind.videoPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bind.videoPlayer.onPause();
        TCControllerWindow controllerWindow = bind.videoPlayer.getControllerWindow();
        if (controllerWindow != null && bind.videoPlayer.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAYING) {
            controllerWindow.mIvPause.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.videoPlayer.resetPlayer();
        bind.videoPlayer.release();
    }

    @Override
    public void onStartFullScreenPlay() {

    }

    @Override
    public void onStopFullScreenPlay() {

    }

    @Override
    public void onClickFloatCloseBtn() {

    }

    @Override
    public void onClickSmallReturnBtn() {

    }

    @Override
    public void onStartFloatWindowPlay() {

    }

    @Override
    public void onEndPlay(int currentState) {

    }

    @Override
    public void onPlayedTime(int times, int totalTime) {

    }
}