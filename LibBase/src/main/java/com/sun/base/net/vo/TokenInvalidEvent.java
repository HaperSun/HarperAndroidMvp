package com.sun.base.net.vo;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: token 失效事件
 */
public final class TokenInvalidEvent {
    private int code;

    public TokenInvalidEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
