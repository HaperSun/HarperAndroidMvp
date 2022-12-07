package com.sun.base.bean;

/**
 * @author Harper
 * @date 2022/3/21
 * note:App常量池
 */
public interface Constant {

    /**
     * 错误图
     */
    String URL_ERROR_PICTURE = "https://xsx-1252375120.cos.ap-nanjing.myqcloud.com/common/uploaderror.png";

    /**
     * 七牛云地址
     */
    String URL_QI_NIU = "http://qiniu.fxgkpt.com/";

    /**
     * 文件夹名字：缓存
     */
    String DIRECTORY_NAME_CACHE = "cache";
    /**
     * 文件夹名字：临时文件
     */
    String DIRECTORY_NAME_TEMP = "temp";
    /**
     * 文件夹名字：日志
     */
    String DIRECTORY_NAME_LOGGER = "logger";
}
