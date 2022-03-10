package com.sun.demo2.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityTimerBinding;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Harper
 * @date: 2022/3/9
 * @note: 计时器
 */
public class TimerActivity extends BaseMvpActivity implements View.OnClickListener {


    private ActivityTimerBinding mBind;
    private int mCount1;
    private int mCount2;
    private Timer mTimer;
    private ScheduledExecutorService mExecutorService;
    private boolean mStopTime;
    private boolean mStopSes;

    public static void start(Context context) {
        Intent intent = new Intent(context, TimerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_timer;
    }

    @Override
    public void initView() {
        mBind = (ActivityTimerBinding) mViewDataBinding;
        mBind.tvTimer.setOnClickListener(this);
        mBind.tvSes.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mExecutorService == null) {
            mExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        }
        startTimer();
        startEse();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_timer:
                mStopTime = !mStopTime;
                if (!mStopTime){
                    mCount1 = 0;
                }
                break;
            case R.id.tv_ses:
                mStopSes = !mStopSes;
                if (!mStopSes){
                    mCount2 = 0;
                }
                break;
            default:
                break;
        }
    }

    private void startTimer() {
        if (mTimer == null){
            return;
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mStopTime){
                    mCount1++;
                    runOnUiThread(() -> mBind.tvTimer.setText(mCount1 + ""));
                }
            }
        }, 1000, 1000);
    }

    private void startEse() {
        mExecutorService.scheduleAtFixedRate(() -> {
            if (mStopSes){
                mCount2++;
                runOnUiThread(() -> mBind.tvSes.setText(mCount2 + ""));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        mCount1 = 0;
        mCount2 = 0;
    }

    private void useTimer() throws ParseException {
        Timer timer = new Timer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date firstTime = sdf.parse("2022-02-23 10:00:00");
        timer.schedule(new MyTimeTask(), firstTime, 10000);
    }

    class MyTimeTask extends TimerTask {

        @Override
        public void run() {
            System.out.print("计时器");
        }
    }
}