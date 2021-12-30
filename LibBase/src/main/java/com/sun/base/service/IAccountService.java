package com.sun.base.service;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 账户相关服务
 */
public interface IAccountService {
    /**
     * 获取登录用户的 AccountId
     *
     * @return
     */
    String getAccountId();

    /**
     * 获取登录用户的 Token
     *
     * @return
     */
    String getToken();

    /**
     * 获取登录用户的姓名
     *
     * @return
     */
    String getDisplayName();
}
