package com.sun.common.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.sun.common.R;
import com.sun.common.util.AppUtil;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: showCustomToast展示带图案的Toast
 * showCommonToast展示普通的Toast
 */
public class ToastHelper {

    //--------------------------------------------自定义---------------------------------------------

    @SuppressLint("StaticFieldLeak")
    private static CustomToast sCustomToast;

    private static CustomToast getCustomToast() {
        try {
            if (sCustomToast == null) {
                Context applicationContext = AppUtil.getApplicationContext();
                sCustomToast = new CustomToast.Builder(applicationContext)
                        .build();
            } else {
                sCustomToast.cancel();
                Context applicationContext = AppUtil.getApplicationContext();
                sCustomToast = new CustomToast.Builder(applicationContext)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sCustomToast;
    }

    public static void showCustomToast(CharSequence msg, @CustomToast.TOAST_TYPE int type, int duration) {
        try {
            CustomToast customToast = getCustomToast();
            customToast.setText(msg);
            customToast.setType(type);
            customToast.setDuration(duration != 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
            customToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(@StringRes int resId) {
        try {
            showCustomToast(AppUtil.getApplicationContext().getString(resId), CustomToast.WARNING, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(CharSequence msg) {
        showCustomToast(msg, CustomToast.WARNING, Toast.LENGTH_SHORT);
    }

    public static void showCustomToast(@StringRes int resId, @CustomToast.TOAST_TYPE int type) {
        try {
            showCustomToast(AppUtil.getApplicationContext().getString(resId), type, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(CharSequence msg, @CustomToast.TOAST_TYPE int type) {
        showCustomToast(msg, type, Toast.LENGTH_SHORT);
    }

    //--------------------------------------------通用-----------------------------------------------

    private static Toast sToast;

    public static void showCommonToast(@StringRes int resId, int duration) {
        try {
            showCommonToast(AppUtil.getApplicationContext().getString(resId), duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCommonToast(String msg) {
        showCommonToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showCommonToast(@StringRes int resId) {
        try {
            showCommonToast(AppUtil.getApplicationContext().getString(resId), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCommonToast(String msg, int duration) {
        Context applicationContext = AppUtil.getApplicationContext();
        if (applicationContext == null) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = new Toast(applicationContext);
        sToast.setDuration(duration != 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        View view = LayoutInflater.from(applicationContext).inflate(R.layout.layout_common_toast, null);
        sToast.setView(view);
        sToast.setGravity(Gravity.BOTTOM, 0, 200);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(msg);
        sToast.show();
    }

}
