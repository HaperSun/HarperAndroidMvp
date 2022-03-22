package com.sun.base.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: Harper
 * @date: 2022/3/22
 * @note: 下载工具类
 */
public class DownloadUtil {

    public static final String TAG = DownloadUtil.class.getName();

    /**
     * 下载失败
     */
    private static final int DOWNLOAD_FAIL = 0;
    /**
     * 下载进度
     */
    private static final int DOWNLOAD_PROGRESS = 1;
    /**
     * 下载成功
     */
    private static final int DOWNLOAD_SUCCESS = 2;
    /**
     * 停止下载
     */
    private static final int DOWNLOAD_STOPPED = 3;

    private final OkHttpClient okHttpClient;

    /**
     * 是否停止下载
     */
    private boolean isStopDownload = false;

    private static volatile DownloadUtil mInstance = null;

    public static DownloadUtil getInstance() {
        if (mInstance == null) {
            synchronized (DownloadUtil.class) {
                if (mInstance == null) {
                    mInstance = new DownloadUtil();
                }
            }
        }
        return mInstance;
    }

    public DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * 停止下载
     */
    public void stopDownLoad() {
        isStopDownload = true;
    }

    /**
     * 下载文件
     *
     * @param url          文件下载地址
     * @param saveFilePath 文件保存本地路径
     * @param listener     下载回调
     */
    public void download(final @NonNull String url, final @NonNull String saveFilePath,
                         final OnDownloadListener listener) {
        isStopDownload = false;
        if (listener != null) {
            listener.onDownloadStart();
        }
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //下载失败
                Message message = mHandler.obtainMessage(DOWNLOAD_FAIL, new HandlerObject(listener, e));
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(saveFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        if (isStopDownload) {
                            //停止下载的话
                            FileUtil.delete(saveFilePath);
                            Message message = mHandler.obtainMessage(DOWNLOAD_STOPPED,
                                    new HandlerObject(listener, (int) (sum * 1.0f / total * 100)));
                            mHandler.sendMessage(message);
                            return;
                        }
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中
                        if (!isStopDownload) {
                            Message message = mHandler.obtainMessage(DOWNLOAD_PROGRESS, new HandlerObject(listener, progress));
                            mHandler.sendMessage(message);
                        }
                    }
                    fos.flush();
                    //下载完成
                    if (!isStopDownload) {
                        Message message = mHandler.obtainMessage(DOWNLOAD_SUCCESS, new HandlerObject(listener, saveFilePath));
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    //下载失败
                    FileUtil.delete(saveFilePath);
                    if (!isStopDownload) {
                        Message message = mHandler.obtainMessage(DOWNLOAD_FAIL, new HandlerObject(listener, e));
                        mHandler.sendMessage(message);
                    }
                } finally {
                    FileUtil.close(is);
                    FileUtil.close(fos);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HandlerObject handlerObject = (HandlerObject) msg.obj;
            OnDownloadListener listener = handlerObject.listener;
            switch (msg.what) {
                case DOWNLOAD_PROGRESS:
                    if (listener != null && !isStopDownload) {
                        listener.onDownloading((Integer) handlerObject.obj);
                    }
                    break;
                case DOWNLOAD_FAIL:
                    if (listener != null && !isStopDownload) {
                        listener.onDownloadFailed((Exception) handlerObject.obj);
                    }
                    break;
                case DOWNLOAD_SUCCESS:
                    if (listener != null && !isStopDownload) {
                        listener.onDownloadSuccess((String) handlerObject.obj);
                    }
                    break;
                case DOWNLOAD_STOPPED:
                    if (listener != null) {
                        listener.onDownloadStopped((Integer) handlerObject.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    class HandlerObject {

        OnDownloadListener listener;
        Object obj;

        public HandlerObject(OnDownloadListener listener, Object object) {
            this.listener = listener;
            this.obj = object;
        }
    }

    public interface OnDownloadListener {
        /**
         * 开始下载
         */
        void onDownloadStart();

        /**
         * 下载成功
         *
         * @param path 文件路径
         */
        void onDownloadSuccess(String path);

        /**
         * 下载进度
         *
         * @param progress  进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         *
         * @param e 错误信息
         */
        void onDownloadFailed(Throwable e);

        /**
         * 下载停止
         *
         * @param progress 已经下载完的进度
         */
        void onDownloadStopped(int progress);
    }

}
