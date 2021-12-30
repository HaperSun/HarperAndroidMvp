package com.sun.demo2.update.model;


import com.sun.base.net.response.BaseResponse;

/**
 * @author: Harper
 * @date: 2021/12/30
 * @note: 获取更新接口信息返回数据结构
 */
public class GetUpdateInfoResponse extends BaseResponse {
    //没有升级信息
    public static final int CODE_NO_UPDATE_INFO = -102;
    private DataBean data;
    private long responsetime;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public long getResponsetime() {
        return responsetime;
    }

    public static class DataBean {
        //不强制升级
        public static final int FORCE_TYPE_OPTIONAL = 2;
        //强制更新
        public static final int FORCE_TYPE_FORCE_UPDATE = 1;

        private String id;
        /**
         * 升级信息
         */
        private String info;
        /**
         * 版本code，比如1.0
         */
        private double version;
        /**
         * 升级方式   1：强制更新 2：不强制升级；
         */
        private int type;
        /**
         * 更新包下载地址
         */
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public double getVersion() {
            return version;
        }

        public void setVersion(double version) {
            this.version = version;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }


        /**
         * 强制更新
         *
         * @return
         */
        public boolean isForceUpdate() {
            return type == FORCE_TYPE_FORCE_UPDATE;
        }

        /**
         * 非强制更新
         *
         * @return
         */
        public boolean isOptionalUpdate() {
            return type == FORCE_TYPE_OPTIONAL;
        }
    }
}
