package com.sun.media.video.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sun.media.video.i.FeedPlayer;
import com.sun.media.video.model.FeedPlayerManager;
import com.sun.media.video.model.FeedVodListLoader;
import com.sun.media.video.model.SuperPlayerCode;
import com.sun.media.video.model.SuperPlayerDef;
import com.sun.media.video.model.SuperPlayerModel;
import com.sun.media.video.model.VideoModel;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: feed流需求的播放器控件
 */
public class FeedPlayerView extends FrameLayout implements FeedPlayer {

    private SuperPlayerView superPlayerView        = null;
    private FeedPlayerCallBack feedPlayerCallBack     = null;
    private FeedPlayerManager feedPlayerManager      = null;
    private int                position               = -1;
    private VideoModel videoModel             = null;
    private boolean            playWithModelIsSuccess = false;


    public FeedPlayerView(@NonNull Context context) {
        super(context);
        this.initViews();
    }

    public FeedPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initViews();
    }

    public FeedPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initViews();
    }

    private void initViews() {
        superPlayerView = new SuperPlayerView(getContext());
        superPlayerView.showOrHideBackBtn(false);
        superPlayerView.setPlayerViewCallback(new SuperPlayerView.OnSuperPlayerViewCallback() {
            @Override
            public void onStartFullScreenPlay() {
                if (feedPlayerCallBack != null) {
                    feedPlayerCallBack.onStartFullScreenPlay();
                }
            }

            @Override
            public void onStopFullScreenPlay() {
                if (feedPlayerCallBack != null) {
                    feedPlayerCallBack.onStopFullScreenPlay();
                }
            }

            @Override
            public void onClickFloatCloseBtn() {

            }

            @Override
            public void onClickWindowCloseBtn() {
                if (feedPlayerCallBack != null) {
                    feedPlayerCallBack.onClickSmallReturnBtn();
                }
            }

            @Override
            public void onStartFloatPlay() {

            }

            @Override
            public void onPlaying() {
                if (feedPlayerManager != null) {
                    feedPlayerManager.setPlayingFeedPlayerView(FeedPlayerView.this, position);
                }
            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onError(int code) {
                if (SuperPlayerCode.VOD_REQUEST_FILE_ID_FAIL == code) {
                    playWithModelIsSuccess = false;
                }
                if (feedPlayerManager != null) {
                    feedPlayerManager.removePlayingFeedPlayerView(position);
                }
            }
        });
        addView(superPlayerView);
    }

    /**
     * 设置播放器管理类
     *
     * @param manager
     */
    public void setFeedPlayerManager(FeedPlayerManager manager) {
        feedPlayerManager = manager;
    }

    /**
     * 设置回调接口
     *
     * @param callBack
     */
    @Override
    public void setFeedPlayerCallBack(FeedPlayerCallBack callBack) {
        feedPlayerCallBack = callBack;
    }

    @Override
    public FeedPlayerCallBack getFeedPlayerCallBack() {
        return feedPlayerCallBack;
    }


    @Override
    public void preparePlayVideo(int position, VideoModel videoModel) {
        this.position = position;
        this.videoModel = videoModel;
        SuperPlayerModel playerModel = FeedVodListLoader.conversionModel(videoModel);
        if (playerModel != null && superPlayerView != null) {
            playerModel.playAction = SuperPlayerModel.PLAY_ACTION_MANUAL_PLAY;
            playWithModelIsSuccess = false;
            superPlayerView.playWithModel(playerModel);
        }
    }

    public void preLoad() {
        if(null != videoModel && position > 0 && !playWithModelIsSuccess) {
            SuperPlayerModel playerModel = FeedVodListLoader.conversionModel(videoModel);
            playerModel.playAction = SuperPlayerModel.PLAY_ACTION_PRELOAD;
            playWithModelIsSuccess = true;
            superPlayerView.playWithModel(playerModel);
        }
    }

    @Override
    public void play(VideoModel videoModel) {
        SuperPlayerModel playerModel = FeedVodListLoader.conversionModel(videoModel);
        if (playerModel != null && superPlayerView != null) {
            playerModel.playAction = SuperPlayerModel.PLAY_ACTION_PRELOAD;
            playWithModelIsSuccess = true;
            superPlayerView.playWithModel(playerModel);
            superPlayerView.onResume();
        }
    }

    @Override
        public void resume() {
        if (superPlayerView != null) {
            if (playWithModelIsSuccess) {
                superPlayerView.onResume();
            } else {
                play(videoModel);
            }
        }
    }

    @Override
    public void pause() {
        if (superPlayerView != null) {
            superPlayerView.onPause();
        }
    }

    public void stop() {
        if (superPlayerView != null) {
            position = -1;
            superPlayerView.stopPlay();
        }
    }

    @Override
    public void reset() {
        position = -1;
        if (superPlayerView != null) {
            superPlayerView.revertUI();
        }
    }

    @Override
    public void destroy() {
        reset();
        if (superPlayerView != null) {
            superPlayerView.setPlayerViewCallback(null);
            superPlayerView.resetPlayer();
            superPlayerView.release();
        }
        superPlayerView = null;
    }

    @Override
    public boolean isPlaying() {
        return  superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING;
    }

    @Override
    public boolean isFullScreenPlay() {
        return superPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FULLSCREEN;
    }

    @Override
    public void setWindowPlayMode() {
        superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW);
    }

    public int getPosition() {
        return position;
    }

    public interface FeedPlayerCallBack {
        void onStartFullScreenPlay();

        void onStopFullScreenPlay();

        void onClickSmallReturnBtn();
    }

}
