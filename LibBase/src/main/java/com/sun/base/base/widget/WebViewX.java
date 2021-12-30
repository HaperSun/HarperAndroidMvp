package com.sun.base.base.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewX extends WebView {

    public interface OnPageLoadProgress {

        void progressChange(int progress);

    }

    /**
     * 解决WebView第一次进来会自动调用一个onPageChange方法的bug
     */
    private boolean isFirstLoad;
    private boolean mScrollBar = false;
    private OnPageLoadProgress mProgressChangeListener = null;

    public void setOnPageLoadProgressListener(
            OnPageLoadProgress pageStartedListener) {
        mProgressChangeListener = pageStartedListener;
    }

    private WebViewClient mWebViewClientExt;

    public void setWebViewClientExt(WebViewClient client) {
        mWebViewClientExt = client;
    }

    WebViewClient mWvc = new WebViewClient() {

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mWebViewClientExt != null) {
                return mWebViewClientExt.shouldInterceptRequest(view, url);
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (mWebViewClientExt != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return mWebViewClientExt.shouldInterceptRequest(view, request);
                }
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (mWebViewClientExt != null) {
                mWebViewClientExt.doUpdateVisitedHistory(view, url, isReload);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("WebViewClient", "shouldOverrideUrlLoading");
            view.loadUrl(url);
            if (mWebViewClientExt != null) {
                mWebViewClientExt.shouldOverrideUrlLoading(view, url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setBackgroundColor(Color.TRANSPARENT);
            //显示的移除accessibility、accessibilityTraversal
            view.removeJavascriptInterface("accessibility");
            view.removeJavascriptInterface("accessibilityTraversal");
            view.removeJavascriptInterface("searchBoxJavaBridge_");
            super.onPageStarted(view, url, favicon);
            if (null != mWebViewClientExt) {
                mWebViewClientExt.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (null != mWebViewClientExt) {
                mWebViewClientExt.onPageFinished(view, url);
                isFirstLoad = true;
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (null != mWebViewClientExt) {
                mWebViewClientExt.onReceivedError(view, errorCode, description,
                        failingUrl);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 接受所有网站的证书
            handler.proceed();
//            super.onReceivedSslError(view, handler, error);
        }
    };

    WebChromeClient mWvcc = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mProgressChangeListener != null) {
                mProgressChangeListener.progressChange(newProgress);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("提示");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });

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
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("提示");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });

            builder.setNeutralButton(android.R.string.cancel,
                    new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });

            builder.setCancelable(false);
            builder.create();
            builder.show();

            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber,
                                     String sourceID) {
            Log.d("MyApplication", message + " -- From line " + lineNumber
                    + " of " + sourceID);

        }
    };

    public WebViewX(Context context) {
        super(context);
        loadParams();
    }

    public WebViewX(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadParams();
    }

    // set webview param list
    private void loadParams() {
        // Configure the webview
        WebSettings s = getSettings();
        if (null == s) {
            return;
        }
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSavePassword(false);
        s.setSaveFormData(false);
        s.setJavaScriptEnabled(true);
        s.setBuiltInZoomControls(false);
        s.setSupportZoom(false);
        /*解决混合模式下（https网址中有http图片请求）图片不显示 add by xfchen 2019/6/6 */
        s.setBlockNetworkImage(false);//不阻塞网络图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //允许混合（http，https）
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        // s.setUserAgentString(s.getUserAgentString() + "iflytek_mobile");
//		setInitialScale(100);
        setVerticalScrollBarEnabled(mScrollBar);
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);

        setWebViewClient(mWvc);
        setWebChromeClient(mWvcc);
    }

    // set scroll bar
    @Override
    public void setVerticalScrollBarEnabled(boolean hasBar) {
        mScrollBar = hasBar;
    }

    /**
     * 复写 onScrollChanged 方法，用来判断触发判断的时机
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        /*
         * l：变化后的X轴位置
         * t：变化后的Y轴的位置
         * oldl：原先的X轴的位置
         * oldt：原先的Y轴的位置
         */
        // 监听WebView的滑动位置
        if (mScrollInterface != null) {
            mScrollInterface.onSChanged(l, t, oldl, oldt);
        }
    }

    private ScrollInterface mScrollInterface;
    private boolean mIsNeedListen;

    /**
     * 定义接口
     *
     * @param mInterface ScrollInterface
     */
    public void setOnCustomScrollChangeListener(ScrollInterface mInterface) {
        mScrollInterface = mInterface;
    }

    public interface ScrollInterface {
        void onSChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

}
