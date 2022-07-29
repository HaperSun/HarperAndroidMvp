package com.sun.media.camera.state;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.sun.base.util.LogHelper;
import com.sun.media.camera.i.IState;
import com.sun.media.camera.view.CameraInterface;
import com.sun.media.camera.view.CameraView;


public class BorrowVideoState implements IState {

    private final String TAG = BorrowVideoState.class.getSimpleName();
    private final CameraMachine machine;

    public BorrowVideoState(CameraMachine machine) {
        this.machine = machine;
    }

    @Override
    public void start(SurfaceHolder holder, float screenProp) {
        CameraInterface.getInstance().doStartPreview(holder, screenProp);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void stop() {

    }

    @Override
    public void focus(float x, float y, CameraInterface.FocusCallback callback) {

    }

    @Override
    public void onSwitch(SurfaceHolder holder, float screenProp) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void capture() {

    }

    @Override
    public void record(Surface surface, float screenProp) {

    }

    @Override
    public void stopRecord(boolean isShort, long time) {

    }

    @Override
    public void cancel(SurfaceHolder holder, float screenProp) {
        machine.getView().resetState(CameraView.TYPE_VIDEO);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void confirm() {
        machine.getView().confirmState(CameraView.TYPE_VIDEO);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void zoom(float zoom, int type) {
        LogHelper.i(TAG, "zoom");
    }

    @Override
    public void flash(String mode) {

    }
}
