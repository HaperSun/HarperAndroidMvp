package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;

/**
 * @author: Harper
 * @date: 2022/5/24
 * @note: 视频播放
 */
public class VideoPlayerActivity extends BaseMvpActivity{


    public static void start(Context context) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}