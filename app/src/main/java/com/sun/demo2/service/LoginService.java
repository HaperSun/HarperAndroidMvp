package com.sun.demo2.service;

import com.sun.demo2.model.response.LoginResponse;
import com.sun.demo2.update.model.GetUpdateInfoResponse;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * @author Harper
 * @date 2021/11/12
 * note:
 */
public interface LoginService {

    /**
     * @param loginName
     * @param password
     * @return
     */
    @POST("/MobileLogin/logOn")
    Observable<Result<LoginResponse>> iGetLoginInfo(@Query("loginName") String loginName,
                                                    @Query("password") String password);

    /**
     * 获取更新接口信息
     *
     * @param url
     * @return
     */
    @Streaming
    @GET()
    Observable<Result<GetUpdateInfoResponse>> iGetAppUpdateInfo(@Url String url);
}
