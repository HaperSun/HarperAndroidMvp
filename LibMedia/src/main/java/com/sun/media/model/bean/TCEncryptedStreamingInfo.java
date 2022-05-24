package com.sun.media.model.bean;

/**
 * @author: Harper
 * @date: 2022/5/23
 * @note: 自适应码流信息
 */
public class TCEncryptedStreamingInfo {

    public String drmType;
    public String url;

    @Override
    public String toString() {
        return "TCEncryptedStreamingInfo{" +
                ", drmType='" + drmType + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
