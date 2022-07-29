package com.sun.media.camera.state;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.sun.media.camera.i.ICameraView;
import com.sun.media.camera.i.IState;
import com.sun.media.camera.view.CameraInterface;


public class CameraMachine implements IState {

    private final Context context;
    private IState iState;
    private final ICameraView view;
    //    private CameraInterface.CameraOpenOverCallback cameraOpenOverCallback;
    //浏览状态(空闲)
    private final IState previewIState;
    //浏览图片
    private final IState borrowPictureIState;
    //浏览视频
    private final IState borrowVideoIState;

    public CameraMachine(Context context, ICameraView view, CameraInterface.CameraOpenOverCallback cameraOpenOverCallback) {
        this.context = context;
        previewIState = new PreviewState(this);
        borrowPictureIState = new BorrowPictureState(this);
        borrowVideoIState = new BorrowVideoState(this);
        //默认设置为空闲状态
        this.iState = previewIState;
//        this.cameraOpenOverCallback = cameraOpenOverCallback;
        this.view = view;
    }

    public ICameraView getView() {
        return view;
    }

    public Context getContext() {
        return context;
    }

    public void setState(IState IState) {
        this.iState = IState;
    }

    //获取浏览图片状态
    IState getBorrowPictureState() {
        return borrowPictureIState;
    }

    //获取浏览视频状态
    IState getBorrowVideoState() {
        return borrowVideoIState;
    }

    //获取空闲状态
    IState getPreviewState() {
        return previewIState;
    }

    @Override
    public void start(SurfaceHolder holder, float screenProp) {
        iState.start(holder, screenProp);
    }

    @Override
    public void stop() {
        iState.stop();
    }

    @Override
    public void focus(float x, float y, CameraInterface.FocusCallback callback) {
        iState.focus(x, y, callback);
    }

    @Override
    public void onSwitch(SurfaceHolder holder, float screenProp) {
        iState.onSwitch(holder, screenProp);
    }

    @Override
    public void restart() {
        iState.restart();
    }

    @Override
    public void capture() {
        iState.capture();
    }

    @Override
    public void record(Surface surface, float screenProp) {
        iState.record(surface, screenProp);
    }

    @Override
    public void stopRecord(boolean isShort, long time) {
        iState.stopRecord(isShort, time);
    }

    @Override
    public void cancel(SurfaceHolder holder, float screenProp) {
        iState.cancel(holder, screenProp);
    }

    @Override
    public void confirm() {
        iState.confirm();
    }

    @Override
    public void zoom(float zoom, int type) {
        iState.zoom(zoom, type);
    }

    @Override
    public void flash(String mode) {
        iState.flash(mode);
    }

    public IState getState() {
        return this.iState;
    }
}
