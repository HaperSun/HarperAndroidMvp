package com.sun.base.service;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: IAccountService空实现
 */
public class EmptyAccountServiceImpl implements IAccountService {

    @Override
    public String getAccountId() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
