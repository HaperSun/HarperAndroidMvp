package com.sun.media.video.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.media.R;
import com.sun.media.databinding.ActivityShortVideoBinding;
import com.sun.media.video.model.ShortVideoBean;
import com.sun.media.video.model.MediaEvent;
import com.sun.media.video.model.ShortVideoModel;
import com.sun.media.video.ui.adapter.ShortVideoListAdapter;
import com.sun.media.video.ui.fragment.ShortVideoListFragment;
import com.sun.media.video.ui.fragment.ShortVideoPlayFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/22
 * @note: 短视频
 */
public class ShortVideoActivity extends BaseMvpActivity<ActivityShortVideoBinding> implements ShortVideoModel.IOnDataLoadFullListener,
        ViewPager.OnPageChangeListener, ShortVideoListAdapter.OnItemClickListener {

    private static final int PLAY_FRAGMENT = 0;
    private static final int LIST_FRAGMENT = 1;
    private List<BaseMvpFragment> mFragments;
    private ShortVideoPlayFragment mPlayFragment;
    private ShortVideoListFragment mListFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, ShortVideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_short_video;
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.cl_14233D;
        return true;
    }

    @Override
    public void initView() {
        mFragments = new ArrayList<>();
        mPlayFragment = ShortVideoPlayFragment.getInstance();
        mListFragment = ShortVideoListFragment.getInstance();
        mFragments.add(mPlayFragment);
        mFragments.add(mListFragment);
        mListFragment.setShortVideoClickListener(this);
        vdb.viewPager.setAdapter(new ShortVideoPageAdapter(getSupportFragmentManager()));
        vdb.viewPager.addOnPageChangeListener(this);
        vdb.viewPager.setCurrentItem(PLAY_FRAGMENT);
    }

    @Override
    public void initData() {
        ShortVideoModel.getInstance().setOnDataLoadFullListener(this);
        ShortVideoModel.getInstance().loadDefaultVideo();
        ShortVideoModel.getInstance().getVideoByFileId();
    }

    @Override
    protected boolean enableEventBus() {
        return true;
    }

    @Override
    public void onLoaded(List<ShortVideoBean> videoBeanList) {
        mListFragment.onLoaded(videoBeanList);
        mPlayFragment.onLoaded(videoBeanList);
        ShortVideoModel.getInstance().release();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == LIST_FRAGMENT) {
            mPlayFragment.onListPageScrolled();
        } else if (position == PLAY_FRAGMENT) {
            Log.i(TAG, "onPageScrolled of play fragment");
        } else {
            Log.i(TAG, "onPageScrolled other case");
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(int position) {
        vdb.viewPager.setCurrentItem(PLAY_FRAGMENT);
        mPlayFragment.onItemClick(position);
        Log.i(TAG, "from list position " + position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MediaEvent.ShortVideoListBackEvent event) {
        vdb.viewPager.setCurrentItem(PLAY_FRAGMENT);
    }

    class ShortVideoPageAdapter extends FragmentPagerAdapter {

        public ShortVideoPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}