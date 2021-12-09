package com.sun.demo2.model.request;

import com.sun.base.request.BaseRequest;

/**
 * @author Harper
 * @date 2021/11/12
 * note:
 */
public class LoginRequest extends BaseRequest {

    private String loginName;
    private String password;

    public LoginRequest(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }
}
