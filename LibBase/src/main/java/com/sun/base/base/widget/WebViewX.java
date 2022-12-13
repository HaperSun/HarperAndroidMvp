package com.sun.base.base.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author: Harper
 * @date: 2022/1/18
 * @note: 封装 WebView
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewX extends WebView {

    public WebViewX(@NonNull Context context) {
        this(context, null);
    }

    public WebViewX(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebViewX(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadParams();
    }

    /**
     * Configure the webview
     */
    private void loadParams() {
        WebSettings s = getSettings();
        if (null != s) {
            s.setCacheMode(WebSettings.LOAD_NO_CACHE);
            s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            s.setUseWideViewPort(true);
            s.setLoadWithOverviewMode(true);
            s.setSavePassword(false);
            s.setSaveFormData(false);
            s.setJavaScriptEnabled(true);
            s.setBuiltInZoomControls(false);
            s.setSupportZoom(false);
            /*解决混合模式下（https网址中有http图片请求）图片不显示*/
            //不阻塞网络图片
            s.setBlockNetworkImage(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //允许混合（http，https）
                s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
//            setInitialScale(100);
            setVerticalScrollBarEnabled(mScrollBar);
            setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
            setWebChromeClient(mWcc);
        }
    }

    private boolean mScrollBar = false;

    WebChromeClient mWcc = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mWebViewListener != null) {
                mWebViewListener.loadProgress(newProgress);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("提示");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("提示");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
            builder.setNeutralButton(android.R.string.cancel, (dialog, which) -> result.cancel());
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            Log.d("MyApplication", message + " -- From line " + lineNumber + " of " + sourceID);
        }
    };


    @Override
    public void setVerticalScrollBarEnabled(boolean hasBar) {
        //是否有竖直的滚动条
        mScrollBar = hasBar;
    }

    /**
     * 复写 onScrollChanged 方法，用来判断触发判断的时机
     */
    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        // 监听WebView的滑动位置
        if (mWebViewListener != null) {
            mWebViewListener.scrollChanged(x, y, oldX, oldY);
        }
    }

    private OnWebViewListener mWebViewListener;

    public void setOnWebViewListener(OnWebViewListener webViewListener) {
        mWebViewListener = webViewListener;
    }

    public interface OnWebViewListener {

        /**
         * 加载进度条
         *
         * @param progress 进度
         */
        void loadProgress(int progress);

        /**
         * webView滚动监听
         *
         * @param x    滚动后x轴
         * @param y    滚动后y轴
         * @param oldX 滚动前x轴
         * @param oldY 滚动前y轴
         */
        void scrollChanged(int x, int y, int oldX, int oldY);
    }
}
