package com.sun.common.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;


import com.sun.common.R;
import com.sun.common.util.ScreenUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 自定义Toast
 */
public class CustomToast extends Toast {
    
    /**
     * 警告类型，显示感叹号
     */
    public static final int WARNING = 1;
    /**
     * 信息类型，显示对勾
     */
    public static final int CORRECT = 2;

    @IntDef({WARNING, CORRECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TOAST_TYPE {
    }

    private final ImageView ivType;
    private final TextView tvTitle;

    public CustomToast(Context context) {
        super(context);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null);
        ivType = view.findViewById(R.id.custom_toast_type);
        tvTitle = view.findViewById(R.id.custom_toast_title);
        //用来解决Toast宽度不能固定的问题
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ScreenUtil.dp2px(context,140),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvTitle.setLayoutParams(lp);
        setGravity(Gravity.CENTER,0,0);
        setView(view);
    }

    public void setType(@TOAST_TYPE int type) {
        if (type == WARNING) {
            ivType.setImageResource(R.mipmap.icon_warning_toast);
        } else {
            ivType.setImageResource(R.mipmap.icon_correct_toast);
        }
    }

    @Override
    public void setText(CharSequence s) {
        tvTitle.setText(s);
    }

    @Override
    public void setText(@StringRes int resId) {
        tvTitle.setText(resId);
    }
    
    public static class Builder {

        private final CustomToast mCustomToast;

        public Builder(Context context) {
            mCustomToast = new CustomToast(context);
        }

        public Builder setGravity(int gravity, int xOffset, int yOffset) {
            mCustomToast.setGravity(gravity, xOffset, yOffset);
            return this;
        }

        public Builder setDuration(int duration) {
            mCustomToast.setDuration(duration);
            return this;
        }

        public Builder setText(CharSequence s) {
            mCustomToast.setText(s);
            return this;
        }

        public Builder setType(@TOAST_TYPE int type) {
            mCustomToast.setType(type);
            return this;
        }

        public CustomToast build() {
            return mCustomToast;
        }
    }
}
