package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.AppUtil;
import com.sun.demo2.BuildConfig;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityWebViewBinding;
import com.sun.demo2.fragment.TestWebViewFragment;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 测试封装的webView
 */
public class WebViewActivity extends BaseMvpActivity<ActivityWebViewBinding> {

    private TestWebViewFragment mWebViewFragment;

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
        vdb.tvUrl.setText(AppUtil.mBaseUrl);
        vdb.tvH5Url.setText(BuildConfig.Base_URL_H5);
    }

    @Override
    public void initData() {
        mWebViewFragment = TestWebViewFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mWebViewFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (null != mWebViewFragment && null != mWebViewFragment.mWebViewX && mWebViewFragment.mWebViewX.canGoBack()) {
            mWebViewFragment.mWebViewX.goBack();
        } else {
            super.onBackPressed();
        }
    }
}