package com.sun.base.base.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.sun.base.BuildConfig;
import com.sun.base.R;
import com.sun.base.base.iview.IJsConfig;
import com.sun.base.base.widget.WebViewX;
import com.sun.base.databinding.FragmentWebViewBinding;
import com.sun.base.util.LogHelper;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 默认的含有WebView的Fragment
 */
public abstract class WebViewFragment extends BaseMvpFragment<FragmentWebViewBinding> {

    //网页加载失败显示
    //收到错误消息后，onPageStart，mFinishCount加载的序号
    private int mFailStartNum = -1;
    private int mFailFinishNum = -1;
    //当前onPageStart序号，每次调用后+1
    private int mStartCount = 0;
    //当前onPageFinish序号
    private int mFinishCount = 0;
    private boolean mHasEnterPageFinished;
    //记录302重定向数量的
    private int mRedirectedCount = 0;
    private OnWebViewListener mOnWebViewListener;
    //要加载的url
    public String mUrl;
    //是否支持缩放
    public boolean mNeedSupportZoom = false;
    //是否展示进度条
    public boolean mShowProgressBar = false;

    @Override
    public int layoutId() {
        return R.layout.fragment_web_view;
    }

    @Override
    public void initView() {
        vdb.webLoadFailContainer.setOnClickListener(v -> refreshWebView());
        if (mOnWebViewListener != null) {
            mOnWebViewListener.onInitViewCompleted(vdb.webView);
        }
        initLoadUrl();
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(mUrl)) {
            showFailToast("url不能为空！");
        } else {
            mFailStartNum = -1;
            mFailFinishNum = -1;
            initWebView();
            vdb.webView.loadUrl(mUrl);
            vdb.webView.addJavascriptInterface(new IJsConfig(getActivity()), "IJsSdk");
        }
    }

    /**
     * 子类需要在该方法中给mUrl赋值
     */
    public abstract void initLoadUrl();

    @Override
    public void onDestroy() {
        vdb.webView.destroy();
        super.onDestroy();
    }

    /**
     * 做一些webView的配置
     */
    protected void initWebView() {
        if (mNeedSupportZoom) {
            WebSettings settings = vdb.webView.getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setSupportZoom(true);
        }
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        initWebViewListener();
        vdb.webView.setWebViewClient(mWvc);
    }

    private void initWebViewListener() {
        vdb.webView.setOnWebViewListener(new WebViewX.OnWebViewListener() {
            @Override
            public void loadProgress(int progress) {
                //加载进度条
                if (mShowProgressBar) {
                    vdb.progressBar.setProgress(progress);
                    boolean isCompleted = progress >= 100;
                    if (isCompleted) {
                        vdb.progressBar.setVisibility(View.GONE);
                        if (mOnWebViewListener != null) {
                            mOnWebViewListener.onWebLoadCompleted(vdb.webView);
                        }
                    } else {
                        vdb.progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void scrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //webView滚动监听
                float mWebViewTotalHeight = vdb.webView.getContentHeight() * vdb.webView.getScale() - vdb.webView.getHeight();
                if (mOnWebViewListener != null) {
                    mOnWebViewListener.onWebLoadCompletedHeight(scrollX, scrollY, oldScrollX, oldScrollY,
                            mWebViewTotalHeight);
                }
                Log.d(TAG, "-" + mWebViewTotalHeight);
            }
        });
    }

    WebViewClient mWvc = new WebViewClient(){
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //版本小于21时，回调这个方法
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                LogHelper.d(TAG, "shouldInterceptRequest url-->" + url);
                WebResourceResponse webResourceResponse = handleShouldInterceptRequest(view, url);
                if (webResourceResponse != null) {
                    LogHelper.d(TAG, "handleShouldInterceptRequest handled url -->" + url);
                    return webResourceResponse;
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //版本>=21时，回调这个方法
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String url = null;
                if (request != null) {
                    url = request.getUrl().toString();
                }
                LogHelper.d(TAG, "shouldInterceptRequest url-->" + url);
                WebResourceResponse webResourceResponse = handleShouldInterceptRequest(view, url);
                if (webResourceResponse != null) {
                    LogHelper.d(TAG, "handleShouldInterceptRequest handled url -->" + url);
                    return webResourceResponse;
                }
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (!mHasEnterPageFinished) {
                mRedirectedCount++;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogHelper.d(TAG, "onReceivedError errorCode=" + errorCode + " description=" + description + " " +
                    "failingUrl=" + failingUrl);
            vdb.webView.setVisibility(View.GONE);
            vdb.webLoadFailContainer.setVisibility(View.VISIBLE);
            mFailStartNum = mStartCount + 1;
            mFailFinishNum = mFinishCount + 1;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setBackgroundColor(Color.TRANSPARENT);
            //显示的移除accessibility、accessibilityTraversal
            view.removeJavascriptInterface("accessibility");
            view.removeJavascriptInterface("accessibilityTraversal");
            view.removeJavascriptInterface("searchBoxJavaBridge_");
            super.onPageStarted(view, url, favicon);
            LogHelper.d("WebViewFragment", "onPageStarted ：时间戳" + System.currentTimeMillis());
            mStartCount++;
            if (mStartCount == mFailStartNum) {
                vdb.webView.setVisibility(View.GONE);
                vdb.webLoadFailContainer.setVisibility(View.VISIBLE);
            } else if (mStartCount > mFailStartNum) {
                vdb.webLoadFailContainer.setVisibility(View.GONE);
            }
            mHasEnterPageFinished = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogHelper.d("WebViewFragment", "onPageFinished:时间戳" + System.currentTimeMillis());
            mFinishCount++;
            //有的手机上onPageFinished会多执行一次
            if (mFinishCount > mFailFinishNum && !mHasEnterPageFinished) {
                vdb.webView.setVisibility(View.VISIBLE);
                vdb.webLoadFailContainer.setVisibility(View.GONE);
            }
            mHasEnterPageFinished = true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("WebViewClient", "shouldOverrideUrlLoading");
            view.loadUrl(url);
            if (mHasEnterPageFinished) {
                //clear count
                mRedirectedCount = 0;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 接受所有网站的证书
            handler.proceed();
        }
    };

    /**
     * 子类想要拦截加载过程，可以复写该方法
     *
     * @param view view
     * @param url  url
     * @return WebResourceResponse
     */
    protected WebResourceResponse handleShouldInterceptRequest(WebView view, String url) {
        return null;
    }

    /**
     * 刷新当前页面
     */
    private void refreshWebView() {
        mFailStartNum = -1;
        mFailFinishNum = -1;
        vdb.webView.reload();
    }

    public void setOnWebViewListener(OnWebViewListener onWebViewListener) {
        mOnWebViewListener = onWebViewListener;
    }

    /**
     * initView完成回调监听接口
     */
    public interface OnWebViewListener {

        /**
         * initView完成回调
         *
         * @param webView 当前webView
         */
        void onInitViewCompleted(WebView webView);

        /**
         * web页面加载成功（加载进度100%）回调
         *
         * @param webView 当前webView
         */
        void onWebLoadCompleted(WebView webView);

        /**
         * webView滑动到页面最底下了
         *
         * @param scrollX             scrollX
         * @param scrollY             scrollY
         * @param oldScrollX          oldScrollX
         * @param oldScrollY          oldScrollY
         * @param mWebViewTotalHeight mWebViewTotalHeight
         */
        void onWebLoadCompletedHeight(int scrollX, int scrollY, int oldScrollX, int oldScrollY,
                                      float mWebViewTotalHeight);
    }
}
