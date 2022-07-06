package com.sun.media.video.i;


import com.sun.media.video.model.FeedPlayerManager;
import com.sun.media.video.model.VideoModel;
import com.sun.media.video.ui.view.FeedListItemView;

public interface FeedListCallBack {

    void onLoadMore();

    void onRefresh();

    void onListItemClick(FeedPlayerManager feedPlayerManager, FeedListItemView itemView, VideoModel videoModel, int position);

    void onStartFullScreenPlay();

    void onStopFullScreenPlay();

}
