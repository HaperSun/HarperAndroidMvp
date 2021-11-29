package com.sun.base.net.response;

import android.text.TextUtils;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */

public class BaseResponse {

    //code为1表示请求成功
    protected static final int CODE_OK = 200;

    public int code;
    public String msg;
    public String access_token;
    public String repCode;

    /**
     * 请求是否成功
     *
     * @return
     */
    public boolean isOK() {
        return code == CODE_OK || !TextUtils.isEmpty(access_token) || "0000".equals(repCode);
    }

}
