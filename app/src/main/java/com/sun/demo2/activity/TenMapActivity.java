package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.demo2.R;

public class TenMapActivity extends BaseMvpActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, PiePolylineChartActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_ten_map;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}