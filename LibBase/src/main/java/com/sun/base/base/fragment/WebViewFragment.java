package com.sun.base.base.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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
public class WebViewFragment extends BaseMvpFragment<FragmentWebViewBinding> {

    protected WebViewX mWebViewX;
    //加载进度条
    private ProgressBar mProgressBar;
    //网页加载失败显示
    private View mWebLoadFailContainer;
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
    private OnInitViewCompleteListener mOnInitViewCompleteListener;
    private OnWebLoadCompleteListener mOnWebLoadCompleteListener;
    private OnWebScrollBottomListener mOnWebScrollBottomListener;
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
        mWebViewX = bind.webView;
        mProgressBar = bind.progressBar;
        mWebLoadFailContainer = bind.webLoadFailContainer;
        mWebLoadFailContainer.setOnClickListener(v -> refreshWebView());
        if (mOnInitViewCompleteListener != null) {
            mOnInitViewCompleteListener.onInitViewCompleted(mWebViewX);
        }
    }

    @Override
    public void initData() {
        loadUrl();
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        initWebView();
        mFailStartNum = -1;
        mFailFinishNum = -1;
        mWebViewX.loadUrl(mUrl);
        mWebViewX.addJavascriptInterface(new IJsConfig(getActivity()), "IJsSdk");
    }

    public void loadUrl() {
    }

    @Override
    public void onDestroy() {
        if (mWebViewX != null) {
            mWebViewX.destroy();
        }
        super.onDestroy();
    }

    /**
     * 做一些webView的配置
     */
    protected void initWebView() {
        if (mNeedSupportZoom) {
            WebSettings settings = mWebViewX.getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setSupportZoom(true);
        }
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        initWebViewListener();
        initWebViewClient();
    }

    private void initWebViewListener() {
        mWebViewX.setOnWebViewListener(new WebViewX.OnWebViewListener() {
            @Override
            public void loadProgress(int progress) {
                //加载进度条
                if (mShowProgressBar) {
                    mProgressBar.setProgress(progress);
                    boolean isCompleted = progress >= 100;
                    if (isCompleted) {
                        mProgressBar.setVisibility(View.GONE);
                        if (mOnWebLoadCompleteListener != null) {
                            mOnWebLoadCompleteListener.onWebLoadCompleted(mWebViewX);
                        }
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void scrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //webView滚动监听
                float mWebViewTotalHeight = mWebViewX.getContentHeight() * mWebViewX.getScale() - mWebViewX.getHeight();
                if (mOnWebScrollBottomListener != null) {
                    mOnWebScrollBottomListener.onWebLoadCompletedHeight(scrollX, scrollY, oldScrollX, oldScrollY,
                            mWebViewTotalHeight);
                }
                Log.d(TAG, "-" + mWebViewTotalHeight);
            }
        });
    }

    private void initWebViewClient() {
        mWebViewX.setWebViewClientExt(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
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
                mWebViewX.setVisibility(View.GONE);
                mWebLoadFailContainer.setVisibility(View.VISIBLE);
                mFailStartNum = mStartCount + 1;
                mFailFinishNum = mFinishCount + 1;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogHelper.d("WebViewFragment", "onPageStarted ：时间戳" + System.currentTimeMillis());
                mStartCount++;
                if (mStartCount == mFailStartNum) {
                    mWebViewX.setVisibility(View.GONE);
                    mWebLoadFailContainer.setVisibility(View.VISIBLE);
                } else if (mStartCount > mFailStartNum) {
                    mWebLoadFailContainer.setVisibility(View.GONE);
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
                    mWebViewX.setVisibility(View.VISIBLE);
                    mWebLoadFailContainer.setVisibility(View.GONE);
                }
                mHasEnterPageFinished = true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mHasEnterPageFinished) {
                    mRedirectedCount = 0; //clear count
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    /**
     * 子类想要拦截加载过程，可以复写该方法
     *
     * @param view
     * @param url
     * @return
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
        mWebViewX.reload();
    }

    public void setOnInitViewCompleteListener(OnInitViewCompleteListener onInitViewCompleteListener) {
        mOnInitViewCompleteListener = onInitViewCompleteListener;
    }

    public void setOnWebLoadCompleteListener(OnWebLoadCompleteListener onWebLoadCompleteListener) {
        mOnWebLoadCompleteListener = onWebLoadCompleteListener;
    }

    public void setOnWebScrollBottomListener(OnWebScrollBottomListener onWebLoadCompleteListener) {
        mOnWebScrollBottomListener = onWebLoadCompleteListener;
    }

    /**
     * initView完成回调监听接口
     */
    public interface OnInitViewCompleteListener {

        /**
         * initView完成回调
         *
         * @param webView 当前webView
         */
        void onInitViewCompleted(WebView webView);
    }

    /**
     * web页面加载成功（加载进度100%）监听接口
     */
    public interface OnWebLoadCompleteListener {

        /**
         * web页面加载成功（加载进度100%）回调
         *
         * @param webView 当前webView
         */
        void onWebLoadCompleted(WebView webView);
    }

    /**
     * 滑动监听
     */
    public interface OnWebScrollBottomListener {

        /**
         * webView滑动到页面最底下了
         */
        void onWebLoadCompletedHeight(int scrollX, int scrollY, int oldScrollX, int oldScrollY,
                                      float mWebViewTotalHeight);
    }
}
