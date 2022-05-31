package com.sun.demo2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.demo2.R;
import com.sun.demo2.databinding.FragmentCircleTurntableBinding;

import java.util.Random;

/**
 * @author Harper
 * @date 2022/5/31
 * note:
 */
public class CircleTurntableFragment extends BaseMvpFragment {

    private Context mContext;
    private FragmentCircleTurntableBinding bind;
    private Animation mStartAnimation;
    private Animation mEndAnimation;
    private boolean isRunning;
    //奖品级别，0代表没有
    private int mPrizeGrade = 6;
    private int mItemCount = 3;
    //奖品在转盘中的位置(到达一等奖的距离)
    private final int[] mPrizePosition = {0, 4, 2, 1, 5, 3};

    public static CircleTurntableFragment getInstance() {
        CircleTurntableFragment fragment = new CircleTurntableFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_circle_turntable;
    }

    @Override
    public void initView() {
        mContext = getContext();
        bind = (FragmentCircleTurntableBinding) mViewDataBinding;
        bind.idStartBtn.setOnClickListener(v -> {
            // 未抽过奖并有抽奖的机会
            if (!isRunning) {
                isRunning = true;
                mStartAnimation.reset();
                bind.idLuckyTurntable.startAnimation(mStartAnimation);
                if (mEndAnimation != null) {
                    mEndAnimation.cancel();
                }
                new Handler().postDelayed(() -> endAnimation(), 2000);
            }
        });
    }

    @Override
    public void initData() {
        mStartAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
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
        mEndAnimation = new RotateAnimation(0, toDegreeMin, Animation.RELATIVE_TO_SELF,
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
                isRunning = false;
                showToast("富光350ml水杯");
                stopAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bind.idLuckyTurntable.startAnimation(mEndAnimation);
        mStartAnimation.cancel();
    }


    /**
     * 停止动画（异常情况，没有奖品）
     */
    private void stopAnimation() {
        //转盘停止回到初始状态
        if (isRunning) {
            mStartAnimation.cancel();
            bind.idLuckyTurntable.clearAnimation();
            isRunning = false;
        }
    }
}
