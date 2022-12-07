package com.sun.base.util;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class IntentUtil {

    private static final String TAG = IntentUtil.class.getSimpleName();

    /**
     * 确保存在相应的 activity 来处理 intent，以免发生 activity 找不到的异常。
     */
    public static void safeStartActivity(Context context, Intent intent) {
        if (null == context || null == intent) {
            return;
        }
        if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            LogHelper.w(TAG, "No activity match : " + intent.toString());
            return;
        }
        try {
            if (context instanceof Application) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogHelper.e("TAG", "ActivityNotFoundException : " + intent.toString());
        }
    }
}
