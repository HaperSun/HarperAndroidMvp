package com.sun.demo2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sun.base.bean.Parameter;
import com.sun.base.toast.ToastHelper;
import com.sun.base.util.LogHelper;

/**
 * @author Harper
 * @date 2022/11/2
 * note:
 */
public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();

    public static void onStartService(Context context,String type){
        Intent intent = new Intent(context, MyService.class);
        intent.putExtra(Parameter.ENTRY_TYPE,type);
        context.startService(intent);
    }

    public static void onDestroyService(Context context){
        context.stopService(new Intent(context, MyService.class));
    }

    @Override
    public void onCreate() {
        LogHelper.e(TAG,"onCreate service");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogHelper.e(TAG,"onStart service");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String type = intent.getStringExtra(Parameter.ENTRY_TYPE);
            ToastHelper.showCustomToast(type);
        }
        LogHelper.e(TAG,"onStartCommand service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogHelper.e(TAG,"onBind service");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogHelper.e(TAG,"onUnbind service");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogHelper.e(TAG,"onDestroy service");
        super.onDestroy();
    }
}
