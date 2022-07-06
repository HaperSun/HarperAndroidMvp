package com.sun.media.video.ui.fragment;

import android.os.Bundle;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.media.R;
import com.sun.media.databinding.FragmentShortVideoPlayBinding;
import com.sun.media.video.model.ShortVideoBean;

import java.util.List;

/**
 * @author Harper
 * @date 2022/6/22
 * note:
 */
public class ShortVideoPlayFragment extends BaseMvpFragment {

    private FragmentShortVideoPlayBinding bind;

    public static ShortVideoPlayFragment getInstance() {
        ShortVideoPlayFragment fragment = new ShortVideoPlayFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_short_video_play;
    }

    @Override
    public void initView() {
        bind = (FragmentShortVideoPlayBinding) mViewDataBinding;
    }

    @Override
    public void initData() {

    }

    /**
     * listAdapter的点击事件
     *
     * @param position 位置
     */
    public void onItemClick(final int position) {
        bind.superShortVideoView.onItemClick(position);
    }

    public void onListPageScrolled() {
        bind.superShortVideoView.onListPageScrolled();
    }

    public void onLoaded(List<ShortVideoBean> shortVideoBeanList) {
        bind.superShortVideoView.setDataSource(shortVideoBeanList);
    }

    @Override
    public void onStart() {
        super.onStart();
        bind.superShortVideoView.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.superShortVideoView.releasePlayer();
    }

}
