package com.sun.base.bean;

/**
 * @author Harper
 * @date 2021/12/6
 * note:
 */
public class BaseConfig {

    public String packageName;
    public int versionCode;
    public String versionName;
    public String testUrl;
    public String releaseUrl;

    public BaseConfig(String packageName, int versionCode, String versionName, String testUrl, String releaseUrl) {
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.testUrl = testUrl;
        this.releaseUrl = releaseUrl;
    }
}
