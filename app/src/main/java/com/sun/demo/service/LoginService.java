package com.sun.demo.service;

import com.sun.demo.model.response.LoginResponse;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * @author Harper
 * @date 2021/11/12
 * note:
 */
public interface LoginService {

    @POST("/MobileLogin/logOn")
    Observable<Result<LoginResponse>> iGetLoginInfo(@Query("loginName") String loginName,
                                                    @Query("password") String password);
}
