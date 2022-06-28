package com.sun.base.net.exception;

import android.text.TextUtils;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public class ApiException extends Exception {
    private final int code;
    private String displayMessage;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        if (!TextUtils.isEmpty(displayMessage)) {
            return displayMessage;
        }
        return getMessage();
    }

    public int getCode() {
        return code;
    }
}
