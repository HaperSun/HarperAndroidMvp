package com.sun.base.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: Toast统一管理类
 */
public class ToastUtil {
    private final static String TAG = "ToastUtil";

    // Toast
    private static Toast toast;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, @StringRes int message) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, @StringRes int message) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context.getApplicationContext(), message, duration);
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, @StringRes int message, int duration) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context.getApplicationContext(), message, duration);
                // toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(message);
            }
            toast.show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Hide the toast, if any.
     */
    public static void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }
}
