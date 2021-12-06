package com.sun.base.bean;

/**
 * @author Harper
 * @date 2021/12/6
 * note:
 */
public class BaseConfig {

    public String packageName;
    public String versionName;
    public String testUrl;
    public String releaseUrl;

    public BaseConfig(String packageName, String versionName, String testUrl, String releaseUrl) {
        this.packageName = packageName;
        this.versionName = versionName;
        this.testUrl = testUrl;
        this.releaseUrl = releaseUrl;
    }
}
