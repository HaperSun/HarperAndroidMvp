package com.sun.base.util;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 箭头旋转
 */
public class RotateUtils {

    //箭头向上
    public static final int CLICK_ARROW_UP=1;
    //箭头向下
    public static final int CLICK_ARROW_DOWN=0;

    /**
     * 根据当前的状态来旋转箭头。
     *
     * @param arrow 箭头view
     * @param arrowStatus
     */
    @SuppressWarnings("all")
    public static void rotateArrow(ImageView arrow, int arrowStatus) {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;

        if (arrowStatus==CLICK_ARROW_DOWN) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if(arrowStatus==CLICK_ARROW_UP){
            fromDegrees = 0f;
            toDegrees = 180f;
        }
//旋转动画效果   参数值 旋转的开始角度  旋转的结束角度  pivotX x轴伸缩值
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
                pivotX, pivotY);
//该方法用于设置动画的持续时间，以毫秒为单位
        animation.setDuration(100);
//动画终止时停留在最后一帧
        animation.setFillAfter(true);
//启动动画
        arrow.startAnimation(animation);
    }
}
