package com.sun.common.util;

/**
 * @author: Harper
 * @date: 2022/6/28
 * @note: 自定义异常
 */
public class InjectException extends RuntimeException {
    public InjectException() {
    }

    public InjectException(String detailMessage) {
        super(detailMessage);
    }

    public InjectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InjectException(Throwable throwable) {
        super(throwable);
    }
}
