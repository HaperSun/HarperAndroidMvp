package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.base.fragment.BaseFragment;
import com.sun.base.bean.MagicInt;
import com.sun.demo2.R;
import com.sun.demo2.adapter.AddressBookPagerAdapter;
import com.sun.demo2.databinding.ActivityAddressBookBinding;
import com.sun.demo2.fragment.OtherAddressBookFragment;
import com.sun.demo2.fragment.WechatAddressBookFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/10
 * @note: 仿通讯录
 */
public class AddressBookActivity extends BaseMvpActivity<ActivityAddressBookBinding> {

    private Context mContext;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddressBookActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean enableKeepScreenBright() {
        return true;
    }

    @Override
    public int layoutId() {
        return R.layout.activity_address_book;
    }

    @Override
    public void initView() {
        mContext = this;
    }

    @Override
    public void initData() {
        List<String> titles = new ArrayList<>();
        titles.add("仿微信通讯录");
        titles.add("仿其他通讯录");
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(WechatAddressBookFragment.getInstance());
        fragments.add(OtherAddressBookFragment.getInstance());
        AddressBookPagerAdapter adapter = AddressBookPagerAdapter.create(getSupportFragmentManager(), mContext, titles, fragments);
        vdb.viewPager.setAdapter(adapter);
        vdb.viewPager.setCurrentItem(MagicInt.ZERO);
        vdb.tabLayout.setupWithViewPager(vdb.viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab customTab = vdb.tabLayout.getTabAt(i);
            if (customTab != null) {
                customTab.setCustomView(adapter.getTabView(i));
                if (i == MagicInt.ZERO) {
                    View customView = customTab.getCustomView();
                    if (customView != null) {
                        customView.setSelected(true);
                        customView.findViewById(R.id.tab_indicator).setVisibility(View.VISIBLE);
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
                View customView = tab.getCustomView();
                if (customView != null) {
                    customView.setSelected(true);
                    customView.findViewById(R.id.tab_indicator).setVisibility(View.VISIBLE);
                    TextView textView = customView.findViewById(R.id.tab_title);
                    textView.setSelected(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                View customView = tab.getCustomView();
                if (customView != null) {
                    customView.setSelected(false);
                    customView.findViewById(R.id.tab_indicator).setVisibility(View.INVISIBLE);
                    TextView textView = customView.findViewById(R.id.tab_title);
                    textView.setSelected(false);
                }
            }
        });
    }
}