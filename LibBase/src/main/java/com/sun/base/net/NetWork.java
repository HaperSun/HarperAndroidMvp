package com.sun.base.net;

import com.sun.base.net.exception.ExceptionEngine;
import com.sun.base.net.response.Response;
import com.sun.base.util.AppUtil;
import com.sun.base.util.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.adapter.rxjava2.Result;

/**
 * @author: Harper
 * @date: 2021/11/16
 * @note:
 */
public class NetWork extends RetrofitUtil {

    private static NetWork instance;

    public static void init() {
        instance = new NetWork();
        RetrofitUtil.initRetrofit(AppUtil.getServerUrl());
    }

    public static NetWork getInstance() {
        return instance;
    }

    public <T extends Response> Observable<Result<T>> sendRequest(Observable<Result<T>> observable) {
        return observable.map(new ServerResultFunc<>()).onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class ServerResultFunc<T extends Response> implements Function<Result<T>, Result<T>> {
        @Override
        public Result<T> apply(@NonNull Result<T> tResult) throws Exception {
            if (tResult.isError() && tResult.error() instanceof Exception) {
                throw (Exception) tResult.error();
            } else if (tResult.response() != null && !tResult.response().isSuccessful()) {
                throw new HttpException(tResult.response());
            } else if (tResult.response() == null) {
                throw new NullPointerException("Result or Response is Null");
            } else {
                return tResult;
            }
        }
    }

    private static class HttpResultFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(@NonNull Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

}
