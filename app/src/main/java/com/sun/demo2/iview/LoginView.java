package com.sun.demo2.iview;


import com.sun.base.net.exception.ApiException;
import com.sun.base.ui.IAddPresenterView;
import com.sun.demo2.model.response.LoginResponse;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 登陆
 */

public interface LoginView extends IAddPresenterView {

    /**
     * 开始请求
     */
    void onAtLoginStart();

    /**
     * 请求成功
     *
     * @param response response
     */
    void onAtLoginReturned(LoginResponse response);

    /**
     * 请求失败
     *
     * @param e e
     */
    void onAtLoginError(ApiException e);
}
