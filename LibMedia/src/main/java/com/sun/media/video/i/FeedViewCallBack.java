package com.sun.media.video.i;


import com.sun.media.video.model.VideoModel;

public interface FeedViewCallBack {


    void onLoadMore();

    void onRefresh();

    void onStartDetailPage();

    void onStopDetailPage();

    void onLoadDetailData(VideoModel videoModel);

    void onStartFullScreenPlay();

    void onStopFullScreenPlay();

}
