package com.sun.demo2.manager;

import com.sun.base.net.response.Response;
import com.sun.base.util.GetRequestUtil;
import com.sun.base.util.RetrofitUtils;
import com.sun.demo2.model.response.LoginResponse;
import com.sun.demo2.service.LoginService;
import com.sun.demo2.update.model.GetUpdateInfoResponse;
import com.sun.demo2.update.model.request.GetUpdateInfoRequest;

import java.util.Map;

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
        return getLoginService().iGetLoginInfo(loginName, password);
    }

    /**
     * 获取更新接口信息
     *
     * @param getUpdateInfoRequest 入参
     * @return
     */
    public static Observable<Result<GetUpdateInfoResponse>> getAppUpdateInfo(GetUpdateInfoRequest getUpdateInfoRequest) {
        return getLoginService().iGetAppUpdateInfo(GetRequestUtil.getRqstUrl("mobileBusiness/version/max", getUpdateInfoRequest.getParams()));
    }

    /**
     * 隐患列表多条件查询
     *
     * @param map 入参
     * @return
     */
    public static Observable<Result<Response>> getRiskList(Map<String, String> map) {
        return getLoginService().iGetRiskList(map);
    }
}
