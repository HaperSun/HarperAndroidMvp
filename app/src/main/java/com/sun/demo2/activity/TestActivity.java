package com.sun.demo2.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.Parameter;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityTestBinding;
import com.sun.demo2.service.MyBroadcastReceiver;
import com.sun.demo2.service.MyService;

/**
 * @author: Harper
 * @date: 2022/11/2
 * @note: 测试
 */
public class TestActivity extends BaseMvpActivity<ActivityTestBinding> {

    private MyBroadcastReceiver myBroadcastReceiver;

    public static void start(Context context) {
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        //动态注册广播
        // 动态注册不能放到activity中，因为动态注册必须要在activity消亡的时候调用unregisterReceiver，
        // 会随着activity的解锁消失而不能再接收广播。一般的办法是在activity起来后马上start一个service,
        // 这个service里动态注册一个broadcastreceiver，service必须常驻在系统内
        //有序广播
        //多个接收器处理有序广播的规则为：
        // 1、优先级越大的接收器，越早收到有序广播。
        // 2、优先级相同时，越早注册的接收器越早收到广播。
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.sun.demo3");
        //设置广播的优先级
        filter.setPriority(10);
        registerReceiver(myBroadcastReceiver,filter);
    }

    /**
     * service 启动方式  start
     *
     * @param view view
     */
    public void onStartService(View view) {
        MyService.onStartService(this,"onStartService");
    }

    public void onDestroyService(View view) {
        MyService.onDestroyService(this);
    }

    /**
     * service 启动方式 onBind
     *
     * @param view view
     */
    public void onBindService(View view) {
        bindService(new Intent(this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onUnbindService(View view) {
        unbindService(serviceConnection);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void onSendStaticReceiver(View view) {
        //静态广播注册
        // 从android 8.0（API26）开始，对清单文件中静态注册广播接收者增加了限制，需要设置ComponentName
        Intent intent = new Intent("io.rong.push");
        intent.setComponent(new ComponentName(this,"com.sun.demo2.service.MyBroadcastReceiver"));
        intent.putExtra(Parameter.TYPE,"发送静态广播");
        sendBroadcast(intent);
    }

    public void onSendReceiver(View view) {
        Intent intent = new Intent("com.sun.demo3");
        intent.putExtra(Parameter.TYPE,"发送动态广播");
        sendOrderedBroadcast(intent,null);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }
}