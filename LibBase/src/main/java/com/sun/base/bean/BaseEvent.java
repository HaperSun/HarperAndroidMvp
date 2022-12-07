package com.sun.base.bean;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: token 失效事件
 */
public final class BaseEvent {

    public static class TokenInvalidEvent{
        private final int code;

        public TokenInvalidEvent(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
