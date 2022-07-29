package com.sun.media.camera.i;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.sun.media.camera.view.CameraInterface;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note:
 */
public interface IState {

    void start(SurfaceHolder holder, float screenProp);

    void stop();

    void focus(float x, float y, CameraInterface.FocusCallback callback);

    void onSwitch(SurfaceHolder holder, float screenProp);

    void restart();

    void capture();

    void record(Surface surface, float screenProp);

    void stopRecord(boolean isShort, long time);

    void cancel(SurfaceHolder holder, float screenProp);

    void confirm();

    void zoom(float zoom, int type);

    void flash(String mode);
}
