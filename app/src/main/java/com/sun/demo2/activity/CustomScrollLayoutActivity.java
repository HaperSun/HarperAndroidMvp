package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.base.fragment.BaseFragment;
import com.sun.base.bean.MagicInt;
import com.sun.demo2.R;
import com.sun.demo2.adapter.ViewPagerAdapter;
import com.sun.demo2.databinding.ActivityCustomScrollLayoutBinding;
import com.sun.demo2.fragment.CircleTurntableFragment;
import com.sun.demo2.fragment.CustomScrollLayoutFragment;
import com.sun.demo2.fragment.SudokuTurnTableFragment;
import com.sun.demo2.view.dialog.PeoplePopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/5/27
 * @note: 仿照百度地图的上层地址列表的上拉、下拉的拖动效果
 */
public class CustomScrollLayoutActivity extends BaseMvpActivity<ActivityCustomScrollLayoutBinding>
        implements PeoplePopupWindow.PopupWindowListener {

    private Context mContext;
    private PeoplePopupWindow mPopupWindow;
    private boolean mChecked;
    private List<BaseFragment> mFragments;

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
        vdb.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> clickCheckBox(isChecked));
    }

    private void clickCheckBox(boolean isChecked) {
        mChecked = isChecked;
        if (mChecked) {
            if (mPopupWindow == null) {
                mPopupWindow = new PeoplePopupWindow(mContext);
                mPopupWindow.setPopupWindowListener(this);
            }
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(vdb.cbSelect, -220, 20);
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
        vdb.viewPager.setScroll(false);
        List<String> titles = new ArrayList<>();
        titles.add("安徽");
        titles.add("圆盘抽奖");
        titles.add("幸运转盘");
        mFragments = new ArrayList<>();
        mFragments.add(CustomScrollLayoutFragment.getInstance());
        mFragments.add(CircleTurntableFragment.getInstance());
        mFragments.add(SudokuTurnTableFragment.getInstance());
        ViewPagerAdapter adapter = ViewPagerAdapter.create(getSupportFragmentManager(), this, titles, mFragments);
        vdb.viewPager.setAdapter(adapter);
        vdb.viewPager.setCurrentItem(MagicInt.ZERO);
        vdb.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        vdb.tabLayout.setupWithViewPager(vdb.viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab customTab = vdb.tabLayout.getTabAt(i);
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

        vdb.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vdb.viewPager) {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int position = tab.getPosition();
                vdb.cbSelect.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                if (mPopupWindow != null) {
                    if (position == 0 && mChecked) {
                        if (!mPopupWindow.isShowing()) {
                            mPopupWindow.showAsDropDown(vdb.cbSelect, -220, 20);
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

    private void commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏所有Fragment
     */
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }
}