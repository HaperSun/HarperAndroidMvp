package com.sun.media.video.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.media.R;
import com.sun.media.databinding.ActivityFeedVideoBinding;
import com.sun.media.video.i.FeedViewCallBack;
import com.sun.media.video.model.FeedVodListLoader;
import com.sun.media.video.model.VideoModel;

import java.util.List;


/**
 * @author: Harper
 * @date: 2022/6/23
 * @note: 流播放
 */
public class FeedVideoActivity extends BaseMvpActivity<ActivityFeedVideoBinding> implements FeedViewCallBack {

    private int mPage = 0;
    private FeedVodListLoader mFeedVodListLoader;
    private boolean mIsFullScreen = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, FeedVideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_feed_video;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.cl_14233D;
        mTitleColor = R.color.cl_14233D;
        return true;
    }

    @Override
    public void initView() {
        mFeedVodListLoader = new FeedVodListLoader();
        loadData(false);
    }

    @Override
    public void initData() {
        baseBind.title.setTitle(R.string.app_feed_title);
        baseBind.title.setOnTitleClickListener(view -> onBackPressed());
        vdb.feedView.setFeedViewCallBack(this);
    }

    @Override
    public void onLoadMore() {
        loadMore();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onStartDetailPage() {
        baseBind.title.setTitle(R.string.app_feed_detail_title);
    }

    @Override
    public void onStopDetailPage() {
        baseBind.title.setTitle(R.string.app_feed_title);
    }

    @Override
    public void onLoadDetailData(VideoModel videoModel) {
        loadDetailData();
    }

    @Override
    public void onStartFullScreenPlay() {
        mIsFullScreen = true;
        baseBind.title.setVisibility(View.GONE);
    }

    @Override
    public void onStopFullScreenPlay() {
        mIsFullScreen = false;
        baseBind.title.setVisibility(View.VISIBLE);
    }

    /**
     * 加载更多数据
     */
    private void loadMore() {
        mFeedVodListLoader.loadListData(mPage + 1, new FeedVodListLoader.LoadDataCallBack() {
            @Override
            public void onLoadedData(List<VideoModel> videoModels) {
                if (isDestroyed()) {
                    return;
                }
                mPage++;
                vdb.feedView.addData(videoModels, false);
                vdb.feedView.finishLoadMore(true, false);
            }

            @Override
            public void onError(int errorCode) {
                if (isDestroyed()) {
                    return;
                }
                vdb.feedView.finishLoadMore(false, true);
            }
        });
    }

    private void loadData(final boolean isRefresh) {
        mPage = 0;
        mFeedVodListLoader.loadListData(mPage, new FeedVodListLoader.LoadDataCallBack() {
            @Override
            public void onLoadedData(List<VideoModel> videoModels) {
                if (isDestroyed()) {
                    return;
                }
                vdb.feedView.addData(videoModels, true);
                if (isRefresh) {
                    vdb.feedView.finishRefresh(true);
                }
            }

            @Override
            public void onError(int errorCode) {
                if (isDestroyed()) {
                    return;
                }
                showToast("暂未获取到数据");
            }
        });
    }

    private void loadDetailData() {
        mFeedVodListLoader.loadListData(mPage, new FeedVodListLoader.LoadDataCallBack() {
            @Override
            public void onLoadedData(List<VideoModel> videoModels) {
                if (isDestroyed()) {
                    return;
                }
                vdb.feedView.addDetailListData(videoModels);
            }

            @Override
            public void onError(int errorCode) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!vdb.feedView.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vdb.feedView.onResume();
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
        vdb.feedView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vdb.feedView.onDestroy();
    }
}