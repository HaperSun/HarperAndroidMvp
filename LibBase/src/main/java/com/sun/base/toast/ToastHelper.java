package com.sun.base.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.sun.base.R;
import com.sun.base.util.AppUtil;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: showCustomToast展示带图案的Toast
 * showCommonToast展示普通的Toast
 */
public class ToastHelper {

    //--------------------------------------------通用-----------------------------------------------

    private static Toast sToast;

    public static void showToast(@StringRes int resId, int duration) {
        try {
            showToast(AppUtil.ctx.getString(resId), duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(@StringRes int resId) {
        try {
            showToast(AppUtil.ctx.getString(resId), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(String msg, int duration) {
        Context applicationContext = AppUtil.ctx;
        if (applicationContext == null || TextUtils.isEmpty(msg)) {
            return;
        }
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = new Toast(applicationContext);
        sToast.setDuration(duration != 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        View view = LayoutInflater.from(applicationContext).inflate(R.layout.view_toast, null);
        sToast.setView(view);
        sToast.setGravity(Gravity.BOTTOM, 0, 200);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(msg);
        sToast.show();
    }

    //--------------------------------------------自定义---------------------------------------------

    @SuppressLint("StaticFieldLeak")
    private static CustomToast sCustomToast;

    public static void showCustomToast(@StringRes int resId) {
        try {
            showCustomToast(AppUtil.ctx.getString(resId), CustomToast.WARNING, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(CharSequence msg) {
        showCustomToast(msg, CustomToast.WARNING, Toast.LENGTH_SHORT);
    }

    public static void showCustomToast(@StringRes int resId, @CustomToast.TOAST_TYPE int type) {
        try {
            showCustomToast(AppUtil.ctx.getString(resId), type, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(CharSequence msg, @CustomToast.TOAST_TYPE int type) {
        showCustomToast(msg, type, Toast.LENGTH_SHORT);
    }

    public static void showCustomToast(CharSequence msg, @CustomToast.TOAST_TYPE int type, int duration) {
        try {
            if (TextUtils.isEmpty(msg)) {
                return;
            }
            CustomToast customToast = getCustomToast();
            customToast.setText(msg);
            customToast.setType(type);
            customToast.setDuration(duration != 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
            customToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CustomToast getCustomToast() {
        try {
            if (sCustomToast == null) {
                Context applicationContext = AppUtil.ctx;
                sCustomToast = new CustomToast.Builder(applicationContext)
                        .build();
            } else {
                sCustomToast.cancel();
                Context applicationContext = AppUtil.ctx;
                sCustomToast = new CustomToast.Builder(applicationContext)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sCustomToast;
    }
}
