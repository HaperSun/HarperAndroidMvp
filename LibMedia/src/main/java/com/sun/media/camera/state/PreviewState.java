package com.sun.media.camera.state;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.sun.base.util.LogHelper;
import com.sun.media.camera.i.IState;
import com.sun.media.camera.view.CameraInterface;
import com.sun.media.camera.view.CameraView;

/**
 * @author: Harper
 * @date: 2022/7/29
 * @note: 空闲状态
 */
class PreviewState implements IState {

    public static final String TAG = PreviewState.class.getSimpleName();

    private final CameraMachine machine;

    PreviewState(CameraMachine machine) {
        this.machine = machine;
    }

    @Override
    public void start(SurfaceHolder holder, float screenProp) {
        CameraInterface.getInstance().doStartPreview(holder, screenProp);
    }

    @Override
    public void stop() {
        CameraInterface.getInstance().doStopPreview();
    }


    @Override
    public void focus(float x, float y, CameraInterface.FocusCallback callback) {
        LogHelper.i("preview state foucs");
        if (machine.getView().handlerFocus(x, y)) {
            CameraInterface.getInstance().handleFocus(machine.getContext(), x, y, callback);
        }
    }

    @Override
    public void onSwitch(SurfaceHolder holder, float screenProp) {
        CameraInterface.getInstance().switchCamera(holder, screenProp);
    }

    @Override
    public void restart() {

    }

    @Override
    public void capture() {
        CameraInterface.getInstance().takePicture((bitmap, isVertical) -> {
            machine.getView().showPicture(bitmap, isVertical);
            machine.setState(machine.getBorrowPictureState());
            LogHelper.i("capture");
        });
    }

    @Override
    public void record(Surface surface, float screenProp) {
        CameraInterface.getInstance().startRecord(surface, screenProp, null);
    }

    @Override
    public void stopRecord(final boolean isShort, long time) {
        CameraInterface.getInstance().stopRecord(isShort, (url, firstFrame) -> {
            if (isShort) {
                machine.getView().resetState(CameraView.TYPE_SHORT);
            } else {
                machine.getView().playVideo(firstFrame, url);
                machine.setState(machine.getBorrowVideoState());
            }
        });
    }

    @Override
    public void cancel(SurfaceHolder holder, float screenProp) {
        LogHelper.i("浏览状态下,没有 cancle 事件");
    }

    @Override
    public void confirm() {
        LogHelper.i("浏览状态下,没有 confirm 事件");
    }

    @Override
    public void zoom(float zoom, int type) {
        LogHelper.i(TAG, "zoom");
        CameraInterface.getInstance().setZoom(zoom, type);
    }

    @Override
    public void flash(String mode) {
        CameraInterface.getInstance().setFlashMode(mode);
    }
}
