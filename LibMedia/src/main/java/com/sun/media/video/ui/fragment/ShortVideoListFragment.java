package com.sun.media.video.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.media.R;
import com.sun.media.databinding.FragmentShortVideoListBinding;
import com.sun.media.video.model.ShortVideoBean;
import com.sun.media.video.model.MediaEvent;
import com.sun.media.video.ui.adapter.ShortVideoListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harper
 * @date 2022/6/22
 * note:
 */
public class ShortVideoListFragment extends BaseMvpFragment<FragmentShortVideoListBinding> {

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
        mShortVideoBeanList = new ArrayList<>();
    }

    @Override
    public void initData() {
        vdb.ibBack.setOnClickListener(v -> EventBus.getDefault().post(new MediaEvent.ShortVideoListBackEvent()));
    }

    public void setShortVideoClickListener(ShortVideoListAdapter.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void onLoaded(List<ShortVideoBean> shortVideoBeanList) {
        mShortVideoBeanList = shortVideoBeanList;
        mAdapter = new ShortVideoListAdapter(getContext(), mOnItemClickListener, mShortVideoBeanList);
        final RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mActivity.runOnUiThread(() -> {
            vdb.recyclerView.setLayoutManager(layoutManager);
            vdb.recyclerView.setAdapter(mAdapter);
        });
    }
}
