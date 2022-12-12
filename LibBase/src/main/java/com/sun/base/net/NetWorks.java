package com.sun.base.net;

import com.sun.base.net.core.INetOut;
import com.sun.base.net.core.INetResult;
import com.sun.base.net.exception.ApiException;
import com.sun.base.net.exception.ExceptionEngine;
import com.sun.base.net.i.IDownloadProgressListener;
import com.sun.base.net.response.BaseResponse;
import com.sun.base.net.response.UploadFileResponse;
import com.sun.base.net.service.DownloadService;
import com.sun.base.net.service.UploadService;
import com.sun.base.net.vo.MultiUploadRequest;
import com.sun.base.util.AppUtil;
import com.sun.base.util.FileUtil;
import com.sun.base.util.RetrofitUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.adapter.rxjava2.Result;

/**
 * @author: Harper
 * @date: 2022/9/6
 * @note:
 */
public class NetWorks extends RetrofitUtil {

    /**
     * 设缓存有效期为1天
     */
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24;
    /**
     * 查询缓存的Cache-Control设置，使用缓存
     */
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置。不使用缓存
     */
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";
    private static NetWorks instance;

    public static void init() {
        instance = new NetWorks();
        RetrofitUtil.initRetrofit(AppUtil.mBaseUrl);
    }

    public static NetWorks getInstance() {
        return instance;
    }

    public static <T> Disposable simpleSendRequest(Observable<? extends Result<? extends INetResult<? extends T>>> observable,
                                                   INetOut<T> out) {
        return observable.map(new ServerResultFunc1<>()).onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(out::onSuccess,
                        throwable -> {
                            if (throwable instanceof ApiException) {
                                ApiException e = (ApiException) throwable;
                                out.onFailure(e.getCode(), e.getDisplayMessage());
                            } else {
                                out.onFailure(-1, throwable.getMessage());
                            }
                        });
    }


    public <T extends BaseResponse> Observable<Result<T>> commonSendRequest(Observable<Result<T>> observable) {
        return observable.map(new ServerResultFunc<>()).onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 将多个请求结果的Response转换到List中
     */
    private final Function<Object[], List<BaseResponse>> mResultZipper = objs -> {
        List<BaseResponse> responses = new ArrayList<>();
        for (Object obj : objs) {
            if (obj instanceof Result) {
                responses.add((BaseResponse) ((Result) obj).response().body());
            }
        }
        return responses;
    };

    /**
     * 同时发送多个请求
     *
     * @param sources 多个请求
     * @return 多个请求响应（extends BaseResponse）列表数据的被观察者
     */
    public <T> Observable<List<BaseResponse>> zipSendRequest(Observable<? extends T>... sources) {
        return zipSendRequest(mResultZipper, sources);
    }

    /**
     * 同时发送多个请求
     *
     * @param zipper  将多个请求返回结果转换为观察者需要的结果
     * @param sources 多个请求
     * @param <R>     结果类型
     * @return 多个请求经zipper后的结果数据被观察者
     */
    public <T, R> Observable<R> zipSendRequest(Function<? super Object[], ? extends R> zipper, Observable<? extends T>... sources) {
        ObservableSource<? extends T>[] mappingSources = new ObservableSource[sources.length];
        int i = 0;
        for (Observable<? extends T> source : sources) {
            // 需要对每个请求结果进行转换
            // 这里需要指定线程才可以同时执行多个请求
            mappingSources[i] = source.map(new ServerResultFunc()).subscribeOn(Schedulers.io());
            i++;
        }
        return Observable.zipArray(zipper, false, Observable.bufferSize(), mappingSources)
                .onErrorResumeNext(new HttpResultFunc())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Result<ResponseBody>> sendDownloadRequst(Observable<Result<ResponseBody>> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * 多文件上传  没有进度回调 <br/>
     */
    public Observable<Result<UploadFileResponse>> uploadMultiFile(Map<String, File> fileMap, String url, MultiUploadRequest request) {
        return uploadMultiFile(fileMap, url, request.getParams());
    }

    /**
     * 多文件上传  没有进度回调 <br/>
     */
    public Observable<Result<UploadFileResponse>> uploadMultiFile(Map<String, File> fileMap, String url, Map<String, String> params) {
        Set<String> keySet = fileMap.keySet();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String s : keySet) {
            // 构建要上传的文件
            File file = fileMap.get(s);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            builder.addFormDataPart(s, file.getName(), requestBody);
        }
        return commonSendRequest(getRetrofit().create(UploadService.class).uploadMultiFile(builder.build(), url, params));
    }

    /**
     * 根据url下载文件
     */
    public void downloadWithUrl(String url, final String dir, final String fileName, final IDownloadProgressListener downloadProgressListener) {
        sendDownloadRequst(getRetrofit().create(DownloadService.class).downloadWithUrl(url))
                .subscribe(new DefaultObserver<Result<ResponseBody>>() {
                    float lastProgress = 0;
                    float currProgress = 0;

                    @Override
                    public void onError(Throwable e) {
                        if (downloadProgressListener != null) {
                            downloadProgressListener.onDownloadError();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(Result<ResponseBody> responseBodyResult) {
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        File realFile = null;
                        try {
                            byte[] fileReader = new byte[1024];
                            long fileSize = responseBodyResult.response().body().contentLength();
                            long fileSizeDownloaded = 0;
                            inputStream = responseBodyResult.response().body().byteStream();
                            realFile = new File(dir + fileName);
                            File f = new File(dir);
                            if (!f.exists()) {
                                f.mkdirs();
                            }
                            if (realFile.exists()) {
                                realFile.delete();
                            }
                            realFile.createNewFile();

                            outputStream = new FileOutputStream(realFile);
                            int read = -1;
                            while ((read = inputStream.read(fileReader)) != -1) {
                                outputStream.write(fileReader, 0, read);
                                fileSizeDownloaded += read;
                                currProgress = fileSizeDownloaded * 1.0f / fileSize * 100;
                                /*判断两次差值，防止频繁更新UI导致的卡顿*/
                                if (currProgress - lastProgress >= 2) {
                                    downloadProgressListener.onProgress(currProgress);
                                    lastProgress = currProgress;
                                }
                            }
                            outputStream.flush();
                            downloadProgressListener.onDownloadSuccess();
                        } catch (Exception e) {
                            if (downloadProgressListener != null) {
                                downloadProgressListener.onDownloadError();
                            }
                            if (realFile != null) {
                                if (realFile.exists()) {
                                    realFile.delete();
                                }
                            }
                        } finally {
                            FileUtil.close(outputStream);
                        }
                    }
                });
    }

    private static class ServerResultFunc1<T> implements Function<Result<? extends INetResult<? extends T>>, T> {
        @Override
        public T apply(@NonNull Result<? extends INetResult<? extends T>> result) throws Exception {
            if (result.isError()) {
                if (result.error() instanceof Exception) {
                    throw (Exception) (result.error());
                } else {
                    throw new Exception(result.error());
                }
            } else if (result.response() != null && !result.response().isSuccessful()) {
                throw new HttpException(result.response());
            } else if (result.response() == null || result.response().body() == null) {
                throw new NullPointerException("Result or Response or Body is Null");
            } else {
                return result.response().body().getData();
            }
        }
    }

    private static class ServerResultFunc<T extends BaseResponse> implements Function<Result<T>, Result<T>> {
        @Override
        public Result<T> apply(@NonNull Result<T> tResult) throws Exception {
            if (tResult.isError() && (tResult.error() instanceof Exception)) {
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