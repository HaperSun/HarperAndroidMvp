package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.common.bean.MagicInt;
import com.sun.demo2.R;
import com.sun.demo2.adapter.ViewPagerAdapter;
import com.sun.demo2.databinding.ActivityCustomScrollLayoutBinding;
import com.sun.demo2.fragment.CircleTurntableFragment;
import com.sun.demo2.fragment.SudokuTurnTableFragment;
import com.sun.demo2.fragment.CustomScrollLayoutFragment;
import com.sun.demo2.view.dialog.PeoplePopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/5/27
 * @note: 仿照百度地图的上层地址列表的上拉、下拉的拖动效果
 */
public class CustomScrollLayoutActivity extends BaseMvpActivity implements PeoplePopupWindow.PopupWindowListener {

    private Context mContext;
    private ActivityCustomScrollLayoutBinding bind;
    private PeoplePopupWindow mPopupWindow;
    private boolean mChecked;

    public static void start(Context context) {
        Intent intent = new Intent(context, CustomScrollLayoutActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_custom_scroll_layout;
    }

    @Override
    public void initView() {
        mContext = this;
        bind = (ActivityCustomScrollLayoutBinding) mViewDataBinding;
        bind.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> clickCheckBox(isChecked));
    }

    private void clickCheckBox(boolean isChecked) {
        mChecked = isChecked;
        if (mChecked) {
            if (mPopupWindow == null) {
                mPopupWindow = new PeoplePopupWindow(mContext);
                mPopupWindow.setPopupWindowListener(this);
            }
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(bind.cbSelect, -220, 20);
            }
        } else {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }
    }

    @Override
    public void initData() {
        initViewPager();
    }

    private void initViewPager() {
        bind.viewPager.setScroll(false);
        List<String> titles = new ArrayList<>();
        titles.add("安徽");
        titles.add("圆盘抽奖");
        titles.add("幸运转盘");
        List<BaseMvpFragment> fragments = new ArrayList<>();
        fragments.add(CustomScrollLayoutFragment.getInstance());
        fragments.add(CircleTurntableFragment.getInstance());
        fragments.add(SudokuTurnTableFragment.getInstance());
        ViewPagerAdapter adapter = ViewPagerAdapter.create(getSupportFragmentManager(), this, titles, fragments);
        bind.viewPager.setAdapter(adapter);
        bind.viewPager.setCurrentItem(MagicInt.ZERO);
        bind.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        bind.tabLayout.setupWithViewPager(bind.viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab customTab = bind.tabLayout.getTabAt(i);
            if (customTab != null) {
                customTab.setCustomView(adapter.getTabView(i));
                if (i == MagicInt.ZERO) {
                    View customView = customTab.getCustomView();
                    if (customView != null) {
                        customView.setSelected(true);
                        TextView textView = customView.findViewById(R.id.tab_title);
                        textView.setSelected(true);
                    }
                }
            }
        }

        bind.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(bind.viewPager) {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int position = tab.getPosition();
                bind.cbSelect.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                if (mPopupWindow != null) {
                    if (position == 0 && mChecked) {
                        if (!mPopupWindow.isShowing()) {
                            mPopupWindow.showAsDropDown(bind.cbSelect, -220, 20);
                        }
                    } else {
                        if (mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                        }
                    }
                }
                View customView = tab.getCustomView();
                if (customView != null) {
                    customView.setSelected(true);
                    TextView textView = customView.findViewById(R.id.tab_title);
                    textView.setSelected(true);
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                View customView = tab.getCustomView();
                if (customView != null) {
                    customView.setSelected(false);
                    TextView textView = customView.findViewById(R.id.tab_title);
                    textView.setSelected(false);
                }
            }
        });
    }

    @Override
    public void onItemClick() {

    }
}