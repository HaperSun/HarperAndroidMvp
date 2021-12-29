package com.sun.base.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.sun.base.dialog.CommonAlertDialog;
import com.sun.common.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Harper
 * @date 2021/12/28
 * note:
 */
public class PermissionUtil {

    private static final int REQ_CODE_PERMISSION_STORAGE = 2000;

    /**
     * 申请存储权限
     *
     * @param activity activity
     * @return
     */
    public static boolean checkStoragePermission(Activity activity) {
        AtomicBoolean hasPermission = new AtomicBoolean(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                hasPermission.set(true);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQ_CODE_PERMISSION_STORAGE);
            }
        } else {
            //判断已经获取了sd卡的写入权限
            if (AndPermission.hasPermissions(activity, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)) {
                hasPermission.set(true);
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
}
