package com.sun.base.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.sun.common.util.AppUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Harper
 * @date 2021/12/28
 * note:申请权限，必须在Activity.onCreate()或者View.onFinishInflate()中处理。不能在onResume()中处理，
 * 因为onResume()在App的生命周期中可能执行的很频繁。如果在请求权限的时候，App重新启动了（例如屏幕旋转导致的App关闭再重新创建），
 * 那么用户的选择（允许 或者 拒绝）将无法发给App
 */
public class PermissionUtil {

    private static final String TAG = PermissionUtil.class.getSimpleName();
    private static final int REQ_CODE_PERMISSION_STORAGE = 2000;

    /**
     * 检查写入SD卡权限
     *
     * @return boolean
     */
    public static boolean checkWriteStorage() {
        //如果是android 11 及以上，且已经开启存储权限，直接返回true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            return true;
        } else if (Build.VERSION.SDK_INT >= 23) {
            Context context = AppUtil.getCtx();
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * 请求写入SD卡权限
     *
     * @param activity activity
     */
    @SuppressLint("CheckResult")
    public static void requestWriteStorage(FragmentActivity activity, Listener listener) {
        new RxPermissions(activity).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {
            //注意：同时申请多个权限时不能这样判断，因为它会在最后把多个权限的申请结果同时返回回来
            if (permission.granted) {
                //用户已经同意该权限
                listener.permissionState(true);
            } else if (permission.shouldShowRequestPermissionRationale) {
                //用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                listener.permissionState(false);
            } else {
                //用户拒绝权限后，这个方法会多次走，需要去重
                //用户是否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权
                listener.permissionState(false);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivityForResult(intent, REQ_CODE_PERMISSION_STORAGE);
            }
        });
    }

    /**
     * 检查相机相关权限
     *
     * @return boolean
     */
    public static boolean checkCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            Context context = AppUtil.getCtx();
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * 请求相机相关权限
     * 无论请求几个权限，onComplete只会回调一次
     *
     * @param activity activity
     */
    public static void requestCamera(FragmentActivity activity, Listener listener) {
        new RxPermissions(activity).requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //同时申请多个权限时，该方法只会走一次回调，即最开始的订阅回调
                        LogHelper.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Permission permission) {
                        //同时申请N个权限时，该方法就会走N次回调
                        LogHelper.e(TAG, "onNext");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogHelper.e(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        //同时申请多个权限时，该方法只会走一次回调，即最后一次回调
                        listener.permissionState(checkCamera());
                    }
                });
    }

    /**
     * 检查相机相关权限
     *
     * @return boolean
     */
    public static boolean checkLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            Context context = AppUtil.getCtx();
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * 请求相机相关权限
     * 无论请求几个权限，onComplete只会回调一次
     *
     * @param activity activity
     */
    public static void requestLocation(FragmentActivity activity, Listener listener) {
        new RxPermissions(activity).requestEach(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Observer<com.tbruyelle.rxpermissions2.Permission>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull com.tbruyelle.rxpermissions2.Permission permission) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //同时申请多个权限时，该方法只会走一次回调，即最后一次回调
                        listener.permissionState(checkLocation());
                    }
                });
    }

    @SuppressLint("CheckResult")
    public static void checkPermission(FragmentActivity activity) {
        new RxPermissions(activity)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS)
                .subscribe(permission -> {
                    if (permission.granted) {
                        // 用户已经同意该权限
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                    }
                });
    }

    public interface Listener {
        /**
         * 权限状态
         *
         * @param state 权限申请返回结果
         */
        void permissionState(boolean state);
    }
}
