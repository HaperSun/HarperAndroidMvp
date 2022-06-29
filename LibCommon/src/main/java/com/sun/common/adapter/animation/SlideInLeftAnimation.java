package com.sun.common.adapter.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @author Harper
 * @date 2022/6/29
 * note:
 */
public class SlideInLeftAnimation implements BaseAnimation {
    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationX", -view.getRootView().getWidth(), 0)
        };
    }
}
