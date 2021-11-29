package com.sun.base.net.i;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note:
 */

public interface IDownloadProgressListener {

    int DOWNLOAD_ING = 101;
    int DOWNLOAD_FAILED = 102;
    int DOWNLOAD_SUCCESS = 103;
    int DOWNLOAD_NORMAL = 104;

    int DOWNLOAD_REQUEST_CODE = 1001;

    void onProgress(float progress);

    void onDownloadSuccess();

    void onDownloadError();
}
