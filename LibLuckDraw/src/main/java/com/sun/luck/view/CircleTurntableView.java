package com.sun.luck.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sun.common.toast.ToastHelper;
import com.sun.luck.R;

import java.util.Random;

/**
 * @author Harper
 * @date 2022/6/28
 * note:
 */
public class CircleTurntableView extends FrameLayout {

    private ImageView ivBg;
    private Animation mStartAnimation;
    private Animation mEndAnimation;
    private boolean isRunning;
    //奖品：1 iphone，2 小米电视，3 机器人，4 200元京东豆，5  水杯，6 5元京东豆
    private int mPrizeGrade;
    private int mItemCount;
    //奖品在转盘中的位置(到达一等奖的距离)
    private final int[] mPrizePosition = {0, 4, 2, 1, 5, 3};
    private OnResultListener mOnResultListener;

    public CircleTurntableView(@NonNull Context context) {
        this(context,null);
    }

    public CircleTurntableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleTurntableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_circle_turntable, this);
        initView(context);
    }

    private void initView(Context context) {
        ivBg = findViewById(R.id.iv_lucky_turntable);
        findViewById(R.id.iv_start_btn).setOnClickListener(v -> {
            if (mItemCount == 0){
                ToastHelper.showToast("请设置奖品内容");
                return;
            }
            if (mPrizeGrade == 0){
                ToastHelper.showToast("请设置奖品结果");
                return;
            }
            //如果正在抽奖则不可以进入
            if (!isRunning) {
                isRunning = true;
                mStartAnimation.reset();
                ivBg.startAnimation(mStartAnimation);
                if (mEndAnimation != null) {
                    mEndAnimation.cancel();
                }
                new Handler().postDelayed(this::endAnimation, 2000);
            }
        });
        mStartAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
        mStartAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void initData(int prizeGrade,int itemCount){
        mPrizeGrade = prizeGrade;
        mItemCount = itemCount;
    }

    /**
     * 结束动画，慢慢停止转动，抽中的奖品定格在指针指向的位置
     */
    private void endAnimation() {
        int position = mPrizePosition[mPrizeGrade - 1];
        float toDegreeMin = 360 / mItemCount * (position - 0.5f) + 1;
        Random random = new Random();
        int randomInt = random.nextInt(360 / mItemCount - 1);
        //5周 + 偏移量
        float toDegree = toDegreeMin + randomInt + 360 * 5;
        // 按中心点旋转 toDegree度
        // 参数：旋转的开始角度、旋转的结束角度、X轴的伸缩模式、X坐标的伸缩值、Y轴的伸缩模式、Y坐标的伸缩值
        mEndAnimation = new RotateAnimation(0, toDegree, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // 设置旋转时间
        mEndAnimation.setDuration(3000);
        // 设置重复次数
        mEndAnimation.setRepeatCount(0);
        // 动画执行完后是否停留在执行完的状态
        mEndAnimation.setFillAfter(true);
        // 动画播放的速度
        mEndAnimation.setInterpolator(new DecelerateInterpolator());
        mEndAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mOnResultListener != null){
                    mOnResultListener.onResult(mPrizeGrade);
                }
                isRunning = false;
                stopAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivBg.startAnimation(mEndAnimation);
        mStartAnimation.cancel();
    }


    /**
     * 停止动画（异常情况，没有奖品）
     */
    private void stopAnimation() {
        //转盘停止回到初始状态
        if (isRunning) {
            mStartAnimation.cancel();
            ivBg.clearAnimation();
            isRunning = false;
        }
    }


    public void setOnResultListener(OnResultListener onResultListener){
        mOnResultListener = onResultListener;
    }

    public interface OnResultListener{
        void onResult(int position);
    }
}
