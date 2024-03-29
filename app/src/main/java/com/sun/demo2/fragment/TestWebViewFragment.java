package com.sun.demo2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.sun.base.base.fragment.WebViewFragment;
import com.sun.base.base.widget.WebViewX;
import com.sun.media.img.ui.activity.ImagePreviewActivity;

/**
 * @author Harper
 * @date 2021/12/2
 * note:
 */
public class TestWebViewFragment extends WebViewFragment {

    private static final String ANSWER_LIST_URL = "/answersDetail";
    public WebViewX mWebViewX;

    public static TestWebViewFragment newInstance() {
        TestWebViewFragment fragment = new TestWebViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initLoadUrl() {
//        mUrl = BuildConfig.Base_URL_H5 + ANSWER_LIST_URL + "?quesId=" + "quesId";
        mUrl = "https://www.baidu.com";
        mNeedSupportZoom = true;
        mShowProgressBar = true;
    }

    @Override
    protected void initWebView() {
        super.initWebView();
        mWebViewX = vdb.webView;
        //开启LocalStorage
        vdb.webView.getSettings().setDomStorageEnabled(true);
        //Js调我
        vdb.webView.addJavascriptInterface(new JsNative(getContext()), TAG);
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
        public void entryPicturePreview(String imgUrl) {
            //TODO 图片预览
            ImagePreviewActivity.start(mContext, imgUrl);
        }
    }
}
