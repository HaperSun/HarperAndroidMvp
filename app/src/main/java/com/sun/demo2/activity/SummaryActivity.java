package com.sun.demo2.activity;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivitySummaryBinding;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 总结
 */
public class SummaryActivity extends BaseMvpActivity {

    private ActivitySummaryBinding mBinding;

    @Override
    public int layoutId() {
        return R.layout.activity_summary;
    }

    @Override
    public void initView() {
        mBinding = (ActivitySummaryBinding) mViewDataBinding;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initData() {
        //当ScrollView或者NestedScrollView中有EditText时，从当前页面跳转到另一个页面再返回时，页面滚动了
        // 这种原因是EditText光标抢占焦点导致的，以下方法可以解决
        mBinding.scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        mBinding.scrollView.setFocusable(true);
        mBinding.scrollView.setFocusableInTouchMode(true);
        mBinding.scrollView.setOnTouchListener((v, event) -> {
            v.requestFocusFromTouch();
            return false;
        });

        //recyclerView在NestedScrollView中使用，滑动时会抢占焦点，导致滑动卡顿，这个是是用来解决
        mBinding.recyclerView.setNestedScrollingEnabled(false);
    }
}