package com.sun.base.net.exception;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public class ServerException extends RuntimeException {
    private int code;
    private String msg;
    /**
     * 原始请求返回数据
     */
    private String response;

    public ServerException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ServerException(int code, String msg, String response) {
        this.code = code;
        this.msg = msg;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getResponse() {
        return response;
    }
}
