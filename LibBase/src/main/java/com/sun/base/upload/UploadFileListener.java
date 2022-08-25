package com.sun.base.upload;

import java.util.List;

/**
 * @author: Harper
 * @date: 2022/8/2
 * @note: 腾讯云上传文件回调
 */
public interface UploadFileListener {

    /**
     * 上传开始
     */
    void onUploadFileStart();

    /**
     * 上传成功
     *
     * @param urls 上传成功返回的文件url数组，有可能是不全的，需要业务层补全
     */
    void onUploadFileSuccess(List<String> urls);

    /**
     * 上传过程回调
     *
     * @param index 第几个文件正在上传(从0开始计数)
     */
    void onUploadFileProcess(int index);

    /**
     * 上传失败
     *
     * @param e
     */
    void onUploadFileFail(Exception e);
}
