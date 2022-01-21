package com.sun.base.bean;

import android.content.Context;

/**
 * @author Harper
 * @date 2021/12/6
 * note:
 */
public class BaseConfig {

    public Context ctx;
    public String packageName;
    public int versionCode;
    public String versionName;
    public String baseUrl;

    public BaseConfig(Context ctx, String packageName, int versionCode, String versionName, String baseUrl) {
        this.ctx = ctx;
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.baseUrl = baseUrl;
    }
}
