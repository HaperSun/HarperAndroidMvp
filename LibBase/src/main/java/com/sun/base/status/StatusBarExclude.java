package com.sun.base.status;

import android.os.Build;

/**
 * @author Harper
 * @date 2022/6/24
 * note:
 */
public class StatusBarExclude {
    static boolean exclude = false;

    public static void excludeIncompatibleFlyMe() {
        try {
            Build.class.getMethod("hasSmartBar");
        } catch (NoSuchMethodException e) {
            exclude |= Build.BRAND.contains("Meizu");
        }
    }
}
