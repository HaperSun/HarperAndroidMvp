package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.adapter.ClickVideoPlayAdapter;
import com.sun.demo2.databinding.ActivityClickVideoPlayBinding;
import com.sun.video.model.SuperPlayerDef;
import com.sun.video.model.SuperPlayerGlobalConfig;
import com.sun.video.model.SuperPlayerModel;
import com.sun.video.model.VideoDataMgr;
import com.sun.video.model.VideoModel;
import com.sun.video.ui.view.SuperPlayerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/5/24
 * @note: 点播视频
 */
public class ClickVideoPlayActivity extends BaseMvpActivity implements SwipeRefreshLayout.OnRefreshListener,
        ClickVideoPlayAdapter.OnItemClickListener, SuperPlayerView.OnSuperPlayerViewCallback {

    //当前界面播放器view展示的宽高比，用主流的16：9
    private static final float PLAYER_VIEW_DISPLAY_RATIO = (float) 720 / 1280;
    private ActivityClickVideoPlayBinding bind;
    private ClickVideoPlayAdapter mAdapter;
    private boolean mPlayStatePause = false;
    private boolean mIsFullScreen = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, ClickVideoPlayActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_click_video_play;
    }

    @Override
    public void initView() {
        bind = (ActivityClickVideoPlayBinding) mViewDataBinding;
        bind.superPlayerView.setPlayerViewCallback(this);
        bind.refreshLayout.setOnRefreshListener(this);
        adjustSuperPlayerViewAndMaskHeight();
        initSuperVodGlobalSetting();
    }

    /**
     * 以16：9 比例显示播放器view，优先保证宽度完全填充
     */
    private void adjustSuperPlayerViewAndMaskHeight() {
        final int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams layoutParams = bind.superPlayerView.getLayoutParams();
        layoutParams.width = screenWidth;
        layoutParams.height = (int) (screenWidth * PLAYER_VIEW_DISPLAY_RATIO);
        bind.superPlayerView.setLayoutParams(layoutParams);
    }

    @Override
    public void initData() {
        mAdapter = new ClickVideoPlayAdapter();
        List<VideoModel> models = getData();
        mAdapter.setAdapterData(models);
        mAdapter.setOnItemClickListener(this);
        bind.recyclerView.setAdapter(mAdapter);
        onItemClick(models.get(0));
    }

    /**
     * 初始化超级播放器全局配置
     */
    private void initSuperVodGlobalSetting() {
        SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
        // 开启悬浮窗播放
        prefs.enableFloatWindow = true;
        // 设置悬浮窗的初始位置和宽高
        SuperPlayerGlobalConfig.TXRect rect = new SuperPlayerGlobalConfig.TXRect();
        rect.x = 0;
        rect.y = 0;
        rect.width = 810;
        rect.height = 540;
        prefs.floatViewRect = rect;
        // 播放器默认缓存个数
        prefs.maxCacheItem = 5;
        // 设置播放器渲染模式
        prefs.enableHWAcceleration = true;
        prefs.renderMode = 1;
        //需要修改为自己的时移域名
//        prefs.playShiftDomain = "liteavapp.timeshift.qcloud.com";
    }

    @Override
    public void onItemClick(VideoModel model) {
        SuperPlayerModel playerModel = new SuperPlayerModel();
        playerModel.url = model.videoURL;
        playerModel.title = model.title;
        playerModel.duration = model.duration;
        playerModel.placeholderImage = model.placeholderImage;
        bind.superPlayerView.playWithModel(playerModel);
    }

    @Override
    public void onRefresh() {
        mAdapter.clearData();
        mAdapter.setAdapterData(getData());
        mAdapter.notifyDataSetChanged();
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
        // 点击悬浮窗关闭按钮，那么结束整个播放
        bind.superPlayerView.resetPlayer();
        finish();
    }

    @Override
    public void onClickWindowCloseBtn() {
        //点击小窗模式下返回按钮
        onBackPressed();
    }

    @Override
    public void onStartFloatPlay() {

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
        }else {
            close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bind.superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING
                || bind.superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PAUSE) {
            Log.i(TAG, "onResume state :" + bind.superPlayerView.getPlayerState());
            if (!bind.superPlayerView.isShowingVipView() && !mPlayStatePause) {
                bind.superPlayerView.onResume();
            }
            if (bind.superPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FLOAT) {
                bind.superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW);
            }
        }
        if (bind.superPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FULLSCREEN) {
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
        bind.superPlayerView.setNeedToPause(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause state :" + bind.superPlayerView.getPlayerState());
        if (bind.superPlayerView.getPlayerMode() != SuperPlayerDef.PlayerMode.FLOAT) {
            // 有手动暂停
            mPlayStatePause = bind.superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PAUSE;
            bind.superPlayerView.onPause();
            bind.superPlayerView.setNeedToPause(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.superPlayerView.release();
        if (bind.superPlayerView.getPlayerMode() != SuperPlayerDef.PlayerMode.FLOAT) {
            bind.superPlayerView.resetPlayer();
        }
        VideoDataMgr.getInstance().setGetVideoInfoListListener(null);
    }

    private List<VideoModel> getData() {
        List<VideoModel> models = new ArrayList<>();
        models.add(new VideoModel("小直播直播美颜、观众评论点赞等基础功能",
                "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/28742df34564972819219071568/master_playlist.m3u8",
                "http://1252463788.vod2.myqcloud.com/e12fcc4dvodgzp1252463788/28742df34564972819219071568/4564972819209692959.jpeg",
                49));
        models.add(new VideoModel("小视频app",
                "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/68e3febf4564972819220421305/master_playlist.m3u8",
                "http://1252463788.vod2.myqcloud.com/e12fcc4dvodgzp1252463788/68e3febf4564972819220421305/4564972819213937908.jpg",
                54));
        models.add(new VideoModel("小直播app-在线直播解决方案",
                "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/287432564564972819219071679/master_playlist.m3u8",
                "http://1252463788.vod2.myqcloud.com/e12fcc4dvodgzp1252463788/287432564564972819219071679/4564972819211741129.jpeg",
                30));
        models.add(new VideoModel("腾讯云介绍",
                "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/e1ab85305285890781763144364/v.f30.mp4",
                "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/e1ab85305285890781763144364/1536584350_1812858038.100_0.jpg",
                60));
        models.add(new VideoModel("小直播app主播连麦",
                "http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/287432344564972819219071668/master_playlist.m3u8",
                "http://1252463788.vod2.myqcloud.com/e12fcc4dvodgzp1252463788/287432344564972819219071668/4564972819212551204.jpeg",
                40));
        models.add(new VideoModel("2分钟带你认识云点播",
                "http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/00eb06a88602268011437356984/adp.10.m3u8",
                "http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/00eb06a88602268011437356984/coverBySnapshot/coverBySnapshot_10_0.jpg",
                102));
        return models;
    }
}