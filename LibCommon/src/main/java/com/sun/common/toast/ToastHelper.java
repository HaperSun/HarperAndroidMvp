package com.sun.common.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.sun.common.R;


/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: showCustomToast展示带图案的Toast
 * showCommonToast展示普通的Toast
 */
public class ToastHelper {

    private static CustomToast sCustomToast;

    private static CustomToast getCustomToast(Context context) {
        try {
            if (sCustomToast == null) {
                Context applicationContext = context.getApplicationContext();
                sCustomToast = new CustomToast.Builder(applicationContext)
                        .build();
            } else {
                sCustomToast.cancel();
                Context applicationContext = context.getApplicationContext();
                sCustomToast = new CustomToast.Builder(applicationContext)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sCustomToast;
    }

    public static void showCustomToast(Context context, CharSequence msg, @CustomToast.TOAST_TYPE int type, int duration) {
        try {
            CustomToast customToast = getCustomToast(context);
            customToast.setText(msg);
            customToast.setType(type);
            customToast.setDuration(duration != 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
            customToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(Context context, @StringRes int resId) {
        try {
            showCustomToast(context, context.getString(resId), CustomToast.WARNING, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(Context context, CharSequence msg) {
        showCustomToast(context, msg, CustomToast.WARNING, Toast.LENGTH_SHORT);
    }

    public static void showCustomToast(Context context, @StringRes int resId, @CustomToast.TOAST_TYPE int type) {
        try {
            showCustomToast(context, context.getString(resId), type, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCustomToast(Context context, CharSequence msg, @CustomToast.TOAST_TYPE int type) {
        showCustomToast(context, msg, type, Toast.LENGTH_SHORT);
    }

    //----------------------------------------------------------------------------------------------

    private static Toast sToast;

    public static void showCommonToast(Context context, @StringRes int resId, int duration) {
        try {
            showCommonToast(context, context.getString(resId), duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCommonToast(Context context, String msg) {
        showCommonToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showCommonToast(Context context, @StringRes int resId) {
        try {
            showCommonToast(context, context.getString(resId), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCommonToast(Context context, String msg, int duration) {
        Context applicationContext = context.getApplicationContext();
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
