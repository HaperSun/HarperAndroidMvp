package com.sun.demo2.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.status.StatusBarUtil;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityImageSelectBinding;

/**
 * @author: Harper
 * @date: 2022/6/27
 * @note:
 */
public class ImageSelectActivity extends BaseMvpActivity<ActivityImageSelectBinding> implements
        View.OnLongClickListener {

    private Context mContext;

    public static void start(Context context) {
        Intent intent = new Intent(context, ImageSelectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        return true;
    }

    @Override
    public int layoutId() {
        return R.layout.activity_image_select;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        mContext = this;
        bind.ivMusic.setOnClickListener(v -> bind.drawerLayout.openDrawer(Gravity.END));
        bind.tvLeft.setOnClickListener(v -> bind.drawerLayout.closeDrawer(Gravity.START));
        bind.tvRight.setOnClickListener(v -> bind.drawerLayout.closeDrawer(Gravity.END));
        bind.ivMusic.setOnLongClickListener(this);
        initStatusBar();
    }

    private void initStatusBar() {
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, bind.drawerLayout, ContextCompat.getColor(mContext, R.color.lightPink));
        ViewGroup.LayoutParams layoutParams = bind.viewStatus.getLayoutParams();
        layoutParams.height = StatusBarUtil.getStatusBarHeight(mContext);
        bind.viewStatus.setLayoutParams(layoutParams);
    }

    @Override
    public void initData() {
        setSupportActionBar(bind.toolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setTitle("");
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Menu icon
        if (item.getItemId() == android.R.id.home) {
            bind.drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onLongClick(View v) {
        bind.drawerLayout.closeDrawers();
        return true;
    }
}