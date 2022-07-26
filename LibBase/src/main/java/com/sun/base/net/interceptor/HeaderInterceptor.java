package com.sun.base.net.interceptor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sun.base.util.AppUtil;
import com.sun.base.db.entity.UserInfo;
import com.sun.base.db.manager.UserInfoManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: http 请求头拦截器
 */
public class HeaderInterceptor implements Interceptor {

    private final Context mContext;

    public HeaderInterceptor(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String authorization = "";
        UserInfo userInfo = UserInfoManager.getInstance(mContext).getCurrentLoginUser();
        if (userInfo != null) {
            authorization = userInfo.getAccessToken();
        }
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("authorization", authorization)
                .addHeader("client", "android")
                .addHeader("deviceType", "android")
                .addHeader("appVersion", AppUtil.getVersionName());
        return chain.proceed(requestBuilder.build());

    }
}
