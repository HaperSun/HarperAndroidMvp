package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityLifeStudyBinding;
import com.sun.demo2.model.MyViewModel;
import com.sun.demo2.observer.MyLocationService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: Harper
 * @date: 2022/3/26
 * @note:
 */
public class LifeStudyActivity extends BaseMvpActivity<ActivityLifeStudyBinding> {

    private MyViewModel mMyViewModel;

    public static void start(Context context) {
        Intent intent = new Intent(context, LifeStudyActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_life_study;
    }

    @Override
    public void initView() {
        getLifecycle().addObserver(vdb.chronometer);
    }

    @Override
    public void initData() {
        mMyViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MyViewModel.class);
        vdb.textView.setText(String.valueOf(mMyViewModel.number));
        vdb.textView2.setText(String.valueOf(mMyViewModel.getSecond().getValue()));
        mMyViewModel.getSecond().observe(this, integer -> vdb.textView2.setText(String.valueOf(integer)));
        startTimer();
    }

    public void startGps(View view) {
        startService(new Intent(this, MyLocationService.class));
    }

    public void stopGps(View view) {
        stopService(new Intent(this, MyLocationService.class));
    }

    public void plusNumber(View view) {
        vdb.textView.setText(String.valueOf(++mMyViewModel.number));
    }


    private void startTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //非ui线程用postValue，ui线程用setValue
                mMyViewModel.getSecond().postValue(mMyViewModel.getSecond().getValue() + 1);
            }
        }, 1000, 1000);
    }
}