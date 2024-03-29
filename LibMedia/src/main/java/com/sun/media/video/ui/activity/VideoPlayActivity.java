package com.sun.media.video.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.Parameter;
import com.sun.base.util.DeviceUtil;
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
public class VideoPlayActivity extends BaseMvpActivity<ActivityVidoPalyBinding> implements SuperPlayerView.OnSuperPlayerViewCallback {

    private boolean mIsFullScreen = false;
    private SuperPlayerModel mPlayerModel;

    public static void start(Context context, SuperPlayerModel model) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(Parameter.BEAN, model);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_vido_paly;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            mPlayerModel = (SuperPlayerModel) intent.getSerializableExtra(Parameter.BEAN);

        }
    }

    @Override
    public void initData() {
        vdb.superPlayerView.setPlayerViewCallback(this);
        startPlayVideo();
    }

    private void startPlayVideo() {
        mPlayerModel.url = "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/287432344564972819219071668/master_playlist.m3u8";
        mPlayerModel.placeholderImage = "http://1252463788.vod2.myqcloud.com/e12fcc4dvodgzp1252463788/287432344564972819219071668/4564972819212551204.jpeg";
        mPlayerModel.title = "小直播 - 主播连麦";
        vdb.superPlayerView.playWithModel(mPlayerModel);
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
        vdb.superPlayerView.resetPlayer();
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
        if (mIsFullScreen) {
            vdb.superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW);
            //当从全屏模式返回窗口模式时，SuperPlayerView会clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            //导致窗口模式的全屏失效，需要重新设置下
            DeviceUtil.setFullScreen(this);
        } else {
            close();
        }
    }

    /**
     * 悬浮窗播放
     */
    private void showFloatWindow() {
        if (vdb.superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING) {
            vdb.superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.FLOAT);
        } else {
            vdb.superPlayerView.resetPlayer();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vdb.superPlayerView.onResume();
        if (mIsFullScreen) {
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
        vdb.superPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vdb.superPlayerView.release();
    }
}