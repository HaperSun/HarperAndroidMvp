package com.sun.demo2.activity;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivitySummaryBinding;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 总结
 */
public class SummaryActivity extends BaseMvpActivity<ActivitySummaryBinding> {


    @Override
    public int layoutId() {
        return R.layout.activity_summary;
    }

    @Override
    public void initView() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initData() {
        //当ScrollView或者NestedScrollView中有EditText时，从当前页面跳转到另一个页面再返回时，页面滚动了
        // 这种原因是EditText光标抢占焦点导致的，以下方法可以解决
        bind.scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        bind.scrollView.setFocusable(true);
        bind.scrollView.setFocusableInTouchMode(true);
        bind.scrollView.setOnTouchListener((v, event) -> {
            v.requestFocusFromTouch();
            return false;
        });

        //recyclerView在NestedScrollView中使用，滑动时会抢占焦点，导致滑动卡顿，这个是是用来解决
        bind.recyclerView.setNestedScrollingEnabled(false);

        //将scrollView滑动到顶部
        bind.scrollView.scrollTo(0, 0);
        //将scrollView滑动到底部
        bind.scrollView.postDelayed(() -> bind.scrollView.fullScroll(ScrollView.FOCUS_DOWN), 100);
    }
}