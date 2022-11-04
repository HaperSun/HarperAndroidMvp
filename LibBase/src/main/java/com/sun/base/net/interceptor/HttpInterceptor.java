package com.sun.base.net.interceptor;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sun.base.util.LogHelper;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */

public class HttpInterceptor implements Interceptor {

    public static final String TAG = HttpInterceptor.class.getSimpleName();

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        //为了方便，将POST请求体参数也拼到url后面打印出来
        StringBuilder fullUrl = new StringBuilder();
        fullUrl.append(url).append("?");
        RequestBody requestBody = request.body();
        if (requestBody instanceof FormBody) {
            //为了打印POST的表单请求参数
            StringBuilder sb = new StringBuilder();
            FormBody formBody = (FormBody) requestBody;
            int size = formBody.size();
            for (int i = 0; i < size; i++) {
                String name = formBody.encodedName(i);
                String value = formBody.encodedValue(i);
                fullUrl.append(name).append("=").append(value);
                if (i < size - 1) {
                    fullUrl.append("&");
                }
                sb.append(name).append("=").append(value).append(",");
            }
            int length = sb.length();
            if (length > 0) {
                sb.delete(length - 1, length);
            }
            LogHelper.d("Http", "| RequestParams:{" + sb.toString() + "}");
            LogHelper.d("Http", fullUrl.toString());
        }

//        //打印http的header
//        Headers headers = request.headers();
//        int size = headers.size();
//        StringBuilder headerSb = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            headerSb.append(headers.name(i)).append("=").append(headers.value(i));
//            if (i < size - 1) {
//                headerSb.append("&");
//            }
//        }
//        LogHelper.d("Http", " header-->" + headerSb);

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        okhttp3.MediaType mediaType = responseBody.contentType();
        String content = responseBody.string();
        content = TextUtils.isEmpty(content) ? " 返回结果为空" : content;
        LogHelper.d("Http", url + content);
        return response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
    }
}
