package com.sun.base.net.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */

public interface DownloadService {

    @Streaming
    @GET
    Observable<Result<ResponseBody>> downloadWithUrl(@Url String url);
}
