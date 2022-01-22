package com.sun.demo2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.sun.base.base.fragment.WebViewFragment;
import com.sun.img.preview.ImagePreviewActivity;

/**
 * @author Harper
 * @date 2021/12/2
 * note:
 */
public class TestWebViewFragment extends WebViewFragment {

    private static final String TAG = TestWebViewFragment.class.getName();
    private static final String ANSWER_LIST_URL = "/answersDetail";

    public static TestWebViewFragment newInstance() {
        TestWebViewFragment fragment = new TestWebViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void loadUrl() {
//        mUrl = BuildConfig.Base_URL_H5 + ANSWER_LIST_URL + "?quesId=" + "quesId";
        mUrl = "https://www.baidu.com";
        mNeedSupportZoom = true;
        mShowProgressBar = true;
    }

    @Override
    protected void initWebView() {
        super.initWebView();
        //开启LocalStorage
        mWebViewX.getSettings().setDomStorageEnabled(true);
        //Js调我
        mWebViewX.addJavascriptInterface(new JsNative(getContext()), TAG);
    }

    static class JsNative {

        private final Context mContext;

        public JsNative(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void loadingOver() {
            //TODO 加载数据结束
        }

        @JavascriptInterface
        public void onToAnswerClick() {
            //TODO 跳转到我要回答
        }

        @JavascriptInterface
        public void entryPicturePreview(String imgUrl) {
            //TODO 图片预览
            ImagePreviewActivity.actionStart(mContext, imgUrl);
        }

        @JavascriptInterface
        public void entryPersonalHomepage(String info) {
            //TODO 进入个人主页

        }

        @JavascriptInterface
        public void needLoginOut() {
            //TODO 退出登录
        }

        @JavascriptInterface
        public void changeFollowState() {
            //TODO 改变关注状态
        }
    }
}
