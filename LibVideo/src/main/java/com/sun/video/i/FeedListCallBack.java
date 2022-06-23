package com.sun.video.i;


import com.sun.video.model.FeedPlayerManager;
import com.sun.video.model.VideoModel;
import com.sun.video.ui.view.FeedListItemView;

public interface FeedListCallBack {

    void onLoadMore();

    void onRefresh();

    void onListItemClick(FeedPlayerManager feedPlayerManager, FeedListItemView itemView, VideoModel videoModel, int position);

    void onStartFullScreenPlay();

    void onStopFullScreenPlay();

}
