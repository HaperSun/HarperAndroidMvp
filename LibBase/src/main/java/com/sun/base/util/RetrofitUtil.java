package com.sun.base.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.sun.base.net.gson.NullString2EmptyAdapterFactory;
import com.sun.base.net.gson.ResponseConverterFactory;
import com.sun.common.util.AppUtil;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public abstract class RetrofitUtil {

    private static final String TAG = "RetrofitUtils";

    //服务器路径
    private static String API_SERVER;

    private static Retrofit mRetrofit;
    private static OkHttpClient mOkHttpClient;

    /**
     * 获取Retrofit对象
     *
     * @return
     */
    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    public static void initRetrofit() {
        initRetrofit(AppUtil.getCtx(), null);
    }

    public static void initRetrofit(Context context, Interceptor interceptor) {

        if (null == mOkHttpClient) {
            mOkHttpClient = OkHttpUtil.getOkHttpClient(context, interceptor);
        }

        API_SERVER = AppUtil.getServerUrl();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(API_SERVER)
                .addConverterFactory(ResponseConverterFactory.create(initGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }


    private static Gson initGson() {
        return new GsonBuilder()
                .registerTypeAdapter(int.class,
                        (JsonDeserializer<Integer>) (json, typeOfT, context) -> {
                            try {
                                return json.getAsInt();
                            } catch (Exception e) {
                                return -1;
                            }
                        })
                .registerTypeAdapter(String.class, (JsonDeserializer<String>) (json, typeOfT, context) -> {
                    try {
                        return json.getAsString();
                    } catch (Exception e) {
                        return "";
                    }

                })
                .registerTypeAdapterFactory(new NullString2EmptyAdapterFactory())
                .create();
    }
}