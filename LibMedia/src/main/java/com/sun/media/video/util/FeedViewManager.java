package com.sun.media.video.util;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.util.Pools;

import com.sun.media.R;
import com.sun.media.video.ui.view.FeedListItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: feedView管理
 */
public class FeedViewManager {

    private final int maxPoolSize;

    private final List<FeedListItemView>             mListItemViews = new ArrayList<>();
    private final Pools.SimplePool<FeedListItemView> mFeedViewPools;
    private final AsyncLayoutInflater                mAsyncLayoutInflater;

    private final Handler                mHandler;
    private       Context                mContext;
    private final ViewGroup.LayoutParams mLayoutParams;

    public FeedViewManager(Context context, final int listItemHeight, int maxPoolSize) {
        this.mContext = context;
        this.maxPoolSize = maxPoolSize;
        mFeedViewPools = new Pools.SimplePool<>(maxPoolSize);
        mAsyncLayoutInflater = new AsyncLayoutInflater(context);
        mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listItemHeight);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                asyncInflate();
            }
        };
        initCacheData();
    }

    private void asyncInflate() {
        mAsyncLayoutInflater.inflate(R.layout.feedview_list_item_view,
                null,
                (view, resid, parent) -> {
                    FeedListItemView feedListItemView = (FeedListItemView) view;
                    feedListItemView.setLayoutParams(mLayoutParams);
                    mFeedViewPools.release(feedListItemView);
                });
    }

    private void initCacheData() {
        int elementIndex = 0;
        while (elementIndex < maxPoolSize) {
            elementIndex++;
            mHandler.sendEmptyMessage(0);
        }
    }

    public FeedListItemView fetchFeedListItemView() {
        FeedListItemView feedListItemView = mFeedViewPools.acquire();
        if (null == feedListItemView) {
            feedListItemView = buildFeedListItemView(mContext);
        }
        mListItemViews.add(feedListItemView);
        return feedListItemView;
    }

    public void release() {
        for (FeedListItemView itemView : mListItemViews) {
            itemView.destroy();
        }
        mListItemViews.clear();
    }

    private FeedListItemView buildFeedListItemView(Context context) {
        FeedListItemView feedListItemView = new FeedListItemView(context);
        feedListItemView.setLayoutParams(mLayoutParams);
        return feedListItemView;
    }


}
