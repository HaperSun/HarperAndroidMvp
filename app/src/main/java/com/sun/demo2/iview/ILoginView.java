package com.sun.demo2.iview;


import com.sun.base.net.exception.ApiException;
import com.sun.base.base.iview.IAddPresenterView;
import com.sun.base.net.response.Response;
import com.sun.demo2.model.response.LoginResponse;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 登陆
 */

public interface ILoginView extends IAddPresenterView {

    /**
     * 登录成功
     *
     * @param response response
     */
    void onAtLoginReturned(LoginResponse response);

    /**
     * 登录失败
     *
     * @param e e
     */
    void onAtLoginError(ApiException e);

    /**
     * 请求成功
     *
     * @param response response
     */
    void onGetRiskListReturned(Response response);

    /**
     * 请求失败
     *
     * @param e e
     */
    void onGetRiskListError(ApiException e);
}
