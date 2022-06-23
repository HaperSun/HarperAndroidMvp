package com.sun.video.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.video.R;
import com.sun.video.databinding.FragmentShortVideoListBinding;
import com.sun.video.model.ShortVideoBean;
import com.sun.video.model.ShortVideoListBackEvent;
import com.sun.video.ui.adapter.ShortVideoListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harper
 * @date 2022/6/22
 * note:
 */
public class ShortVideoListFragment extends BaseMvpFragment {

    private FragmentShortVideoListBinding bind;
    private List<ShortVideoBean> mShortVideoBeanList;
    private ShortVideoListAdapter mAdapter;
    private FragmentActivity mActivity;
    private ShortVideoListAdapter.OnItemClickListener mOnItemClickListener;

    public static ShortVideoListFragment getInstance() {
        ShortVideoListFragment fragment = new ShortVideoListFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_short_video_list;
    }

    @Override
    public void initView() {
        mActivity = getActivity();
        bind = (FragmentShortVideoListBinding) mViewDataBinding;
        mShortVideoBeanList = new ArrayList<>();
    }

    @Override
    public void initData() {
        bind.ibBack.setOnClickListener(v -> EventBus.getDefault().post(new ShortVideoListBackEvent()));
    }

    public void setShortVideoClickListener(ShortVideoListAdapter.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void onLoaded(List<ShortVideoBean> shortVideoBeanList) {
        mShortVideoBeanList = shortVideoBeanList;
        mAdapter = new ShortVideoListAdapter(getContext(), mOnItemClickListener, mShortVideoBeanList);
        final RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mActivity.runOnUiThread(() -> {
            bind.recyclerView.setLayoutManager(layoutManager);
            bind.recyclerView.setAdapter(mAdapter);
        });
    }
}
