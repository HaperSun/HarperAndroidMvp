package com.sun.demo.manager;

import com.sun.base.util.RetrofitUtils;
import com.sun.demo.model.response.LoginResponse;
import com.sun.demo.service.LoginService;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;

/**
 * @author Harper
 * @date 2021/11/12
 * note:
 */
public class LoginManager {

    private LoginManager() {
        throw new RuntimeException("you cannot new AtLoginManager!");
    }

    private static LoginService mLoginService;

    private static LoginService getLoginService() {
        if (mLoginService == null) {
            mLoginService = RetrofitUtils.getRetrofit().create(LoginService.class);
        }
        return mLoginService;
    }

    public static Observable<Result<LoginResponse>> getLoginInfo(String loginName, String password) {
        return getLoginService().iGetLoginInfo(loginName,password);
    }
}
