package com.sun.common.bean;

/**
 * @author Harper
 * @date 2022/3/21
 * note:App常量池
 */
public class Constant {

    public abstract static class Url {
        public static final String UPLOAD_PIC_FAILURE = "https://xsx-1252375120.cos.ap-nanjing.myqcloud.com/common/uploaderror.png";
        public static final String DEFAULT_INIT_PATH = "https://";
        public static final String QI_NIU = "http://qiniu.fxgkpt.com/";
    }

    public abstract static class DirName {
        /**
         * 文件夹名字：缓存
         */
        public static final String CACHE = "cache";
        /**
         * 文件夹名字：临时文件
         */
        public static final String TEMP = "temp";
        /**
         * 文件夹名字：日志
         */
        public static final String LOGGER = "logger";
    }
}
