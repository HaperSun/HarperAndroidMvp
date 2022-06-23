package com.sun.video.i;


import com.sun.video.model.VideoModel;
import com.sun.video.ui.view.FeedPlayerView;

public interface FeedDetailViewCallBack {

    void onRemoveDetailView(FeedPlayerView feedPlayerView, boolean isChangeVideo);

    //实现此方法，在此方法中获取详情页面底部列表数据，feedEntity是传入详情页的视频信息
    void onLoadDetailData(VideoModel videoModel);

    void onClickSmallReturnBtn();

    void onStartDetailFullScreenPlay(int position);

    void onStopDetailFullScreenPlay();

}
