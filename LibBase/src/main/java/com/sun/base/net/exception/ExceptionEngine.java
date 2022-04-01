package com.sun.base.net.exception;

import com.google.gson.JsonParseException;
import com.sun.base.net.vo.TokenInvalidEvent;
import com.sun.base.util.LogHelper;


import org.apache.http.conn.ConnectTimeoutException;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import retrofit2.HttpException;


/**
 * @author: Harper
 * @date:   2021/11/12
 * @note:
 */
public class ExceptionEngine {

    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    private ExceptionEngine() {}

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        String errorMsg = null;
        if (e instanceof HttpException) {             // HTTP错误
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case REQUEST_TIMEOUT:
                    errorMsg = "请求超时";
                    break;
                case UNAUTHORIZED:
                    // token 错误需要跳转登录页面时通知 application
                    EventBus.getDefault().post(new TokenInvalidEvent(((HttpException) e).code()));
                    errorMsg = "登录已过期，请重新登录";
                    break;
                case FORBIDDEN:
                case NOT_FOUND:

                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    errorMsg = "网络错误"; // 均视为网络错误
                    break;
            }
        } else if (e instanceof ServerException) {    // 服务器返回的错误
            ServerException resultException = (ServerException) e;
            int code = resultException.getCode();
            if (code == ERROR.WRONG_TOKEN) {
                // token 错误需要跳转登录页面时通知 application
                EventBus.getDefault().post(new TokenInvalidEvent(code));
                errorMsg = "登录已过期，请重新登录";
            } else {
                errorMsg = resultException.getMsg();
            }
            ex = new ApiException(resultException, code);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ApiException(e, ERROR.PARSE_ERROR);
            errorMsg = "解析错误"; // 均视为解析错误
        } else if (e instanceof ConnectException
                   || e instanceof ConnectTimeoutException
                   || e instanceof SocketTimeoutException
                   || e instanceof UnknownHostException) {
            LogHelper.e("NetWorkException", e.getMessage(),e);
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            errorMsg = "网络异常，请检查网络后重试！"; // 均视为网络错误
        } else {
            ex = new ApiException(e, ERROR.UNKNOWN);
            errorMsg = "失败！"; // 未知错误
        }
        ex.setDisplayMessage(errorMsg);
        LogHelper.e("ExceptionEngine", errorMsg, ex);
        return ex;
    }
}
