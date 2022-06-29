package com.sun.common.adapter.animation;

import android.animation.Animator;
import android.view.View;

/**
 * @author Harper
 * @date 2022/6/29
 * note:
 */
public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
