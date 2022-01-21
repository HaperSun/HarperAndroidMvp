package com.sun.base.net.state;

import androidx.annotation.NonNull;

/**
 * @author Harper
 * @date 2022/1/21
 * note:
 */
public class NetworkType {

    public static String NETWORK_WIFI = "WiFi";
    public static String NETWORK_4G = "4G";
    public static String NETWORK_2G = "2G";
    public static String NETWORK_3G = "3G";
    public static String NETWORK_UNKNOWN = "Unknown";
    public static String NETWORK_NO = "No network";

    private final String desc;

    NetworkType(String desc) {
        this.desc = desc;
    }

    @NonNull
    @Override
    public String toString() {
        return desc;
    }
}
