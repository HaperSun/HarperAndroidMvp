package com.sun.base.status;

import android.view.Window;

/**
 * @author Harper
 * @date 2022/6/24
 * note:
 */
public interface IStatusBar {
    /**
     * Set the status bar color
     *
     * @param window The window to set the status bar color
     * @param color  Color value
     */
    void setStatusBarColor(Window window, int color);
}
