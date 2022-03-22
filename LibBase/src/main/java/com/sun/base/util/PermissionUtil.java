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

import com.sun.base.dialog.CommonAlertDialog;
import com.sun.common.R;
import com.sun.common.bean.MagicInt;
import com.sun.common.util.AppUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final int REQ_CODE_PERMISSION_STORAGE = 2000;
    private static int mAlwaysDeny = 0;

    public static boolean checkStorage() {
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
     * 这种请求权限的方式，请求几个权限就会有几次回调
     *
     * @param activity activity
     * @param listener listener
     */
    @SuppressLint("CheckResult")
    public static void requestStorage(FragmentActivity activity, Listener listener) {
        new RxPermissions(activity).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {
            if (permission.granted) {
                // 用户已经同意该权限
                listener.permissionState(MagicInt.ONE);
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝了该权限，没有选中『不再询问』
                listener.permissionState(MagicInt.ZERO);
            } else {
                //用户拒绝权限
                listener.permissionState(MagicInt.TWO);
            }
        });
    }

    /**
     * 无论请求几个权限，onComplete只会回调一次
     *
     * @param activity activity
     * @param listener listener
     */
    public static void requestStorage2(FragmentActivity activity, Listener listener) {
        new RxPermissions(activity).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        if (checkStorage()) {
                            listener.permissionState(MagicInt.ONE);
                        }
                    }
                });
    }


    @SuppressLint("CheckResult")
    public static boolean checkStoragePermission(FragmentActivity activity) {
        AtomicBoolean hasPermission = new AtomicBoolean(false);
        //如果是android 11 及以上，且已经开启存储权限，直接返回true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            mAlwaysDeny = 0;
            return true;
        }
        new RxPermissions(activity).requestEach(Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {
            if (permission.granted) {
                // 用户已经同意该权限
                hasPermission.set(true);
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                hasPermission.set(false);
            } else {
                //用户拒绝权限后，这个方法会多次走，需要去重
                //用户是否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权
                mAlwaysDeny++;
                if (mAlwaysDeny == 1) {
                    new CommonAlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.reminder))
                            .setMessage(activity.getResources().getString(R.string.permission_tips_write_read))
                            .setNegativeText(activity.getResources().getString(R.string.cancel), view1 -> hasPermission.set(false))
                            .setPositiveText(activity.getResources().getString(R.string.confirm), view2 -> {
                                AndPermission.with(activity).runtime().setting().start(REQ_CODE_PERMISSION_STORAGE);
                            }).build().show();
                }
            }
        });
        mAlwaysDeny = 0;
        return hasPermission.get();
    }

    /**
     * 申请存储权限
     *
     * @param activity activity
     * @return
     */
    public static boolean checkStoragePermissionR(FragmentActivity activity) {
        AtomicBoolean hasPermission = new AtomicBoolean(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                return true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivityForResult(intent, REQ_CODE_PERMISSION_STORAGE);
            }
        } else {
            //判断已经获取了sd卡的写入权限
            if (AndPermission.hasPermissions(activity, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)) {
                return true;
            } else {
                //申请sd卡的写入权限
                AndPermission.with(activity)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(permissions -> {
                            //申请sd卡的写入权限通过了
                            hasPermission.set(true);
                        })
                        .onDenied(permissions -> {
                            //申请sd卡的写入权限被拒绝了
                            if (AndPermission.hasAlwaysDeniedPermission(activity, permissions)) {
                                //用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权
                                new CommonAlertDialog.Builder(activity)
                                        .setTitle(activity.getResources().getString(R.string.reminder))
                                        .setMessage(activity.getResources().getString(R.string.permission_tips_write_read))
                                        .setNegativeText(activity.getResources().getString(R.string.cancel))
                                        .setPositiveText(activity.getResources().getString(R.string.confirm), view ->
                                                AndPermission.with(activity).runtime().setting().start(REQ_CODE_PERMISSION_STORAGE))
                                        .build().show();
                            }
                        })
                        .start();
            }
        }
        return hasPermission.get();
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
         * 0 用户拒绝了该权限，没有选中『不再询问』
         * 1 用户已经同意该权限
         * 2 用户拒绝权限
         *
         * @param state 状态
         * @return int
         */
        void permissionState(int state);
    }
}
