package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.common.util.AppUtil;
import com.sun.demo2.BuildConfig;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityWebViewBinding;
import com.sun.demo2.fragment.TestWebViewFragment;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 测试封装的webView
 */
public class WebViewActivity extends BaseMvpActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView() {
        ActivityWebViewBinding binding = (ActivityWebViewBinding) mViewDataBinding;
        binding.tvUrl.setText(AppUtil.getServerUrl());
        binding.tvH5Url.setText(BuildConfig.Base_URL_H5);
    }

    @Override
    public void initData() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TestWebViewFragment.newInstance())
                .commitAllowingStateLoss();
    }
}