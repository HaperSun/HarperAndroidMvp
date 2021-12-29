package com.sun.base.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;


import com.sun.base.net.cookie.CookiesManager;
import com.sun.base.net.interceptor.HeaderInterceptor;
import com.sun.base.net.interceptor.LogInterceptor;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note:
 */
class OkHttpUtils {

    public static final String TAG = "OkHttp3Utils";

    private static OkHttpClient mOkHttpClient;

    //设置缓存目录
    private static File cacheDirectory ;


    /**
     * 获取OkHttpClient对象
     *
     * @param context
     * @param interceptor
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient(Context context, Interceptor interceptor) {

        if(context == null){
            throw new RuntimeException("must init NetWorks first before use it!!!");
        }

        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }

        // 配置缓存
        cacheDirectory = new File(context.getCacheDir().getAbsolutePath(), "MyCache");
        Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024);

        if (null == mOkHttpClient) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cookieJar(new CookiesManager(context))
                    .addInterceptor(new HeaderInterceptor(context))
                    // 设置请求读写的超时时间
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory());
            if (interceptor != null) {
                builder.addInterceptor(interceptor);
            }
            builder.addInterceptor(new LogInterceptor());
            mOkHttpClient = builder.build();
        }
        return mOkHttpClient;
    }

    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }
        return sSLSocketFactory;
    }

    public static class TrustAllManager implements X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}