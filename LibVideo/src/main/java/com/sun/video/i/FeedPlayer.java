package com.sun.video.i;

import com.sun.video.model.VideoModel;
import com.sun.video.ui.view.FeedPlayerView;

public interface FeedPlayer {

    void preparePlayVideo(int position, VideoModel videoModel);

    void play(VideoModel videoModel);

    void resume();

    void pause();

    void reset();

    void destroy();

    boolean isPlaying();

    boolean isFullScreenPlay();

    void setWindowPlayMode();

    void setFeedPlayerCallBack(FeedPlayerView.FeedPlayerCallBack callBack);

    FeedPlayerView.FeedPlayerCallBack getFeedPlayerCallBack();
}
