package com.sun.demo2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sun.base.base.fragment.BaseFragment;
import com.sun.demo2.R;

import java.util.List;

/**
 * @author Harper
 * @date 2022/5/27
 * note:
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final List<String> mTitles;
    private final List<BaseFragment> mFragments;

    public ViewPagerAdapter(FragmentManager fm, Context context, List<String> titles, List<BaseFragment> fragments) {
        super(fm);
        mContext = context;
        mFragments = fragments;
        mTitles = titles;
    }

    public static ViewPagerAdapter create(FragmentManager fm, Context context, List<String> titles, List<BaseFragment> fragments) {
        return new ViewPagerAdapter(fm, context, titles, fragments);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public View getTabView(int position) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_view_pager, null);
        TextView tv = view.findViewById(R.id.tab_title);
        tv.setText(mTitles.get(position));
        return view;
    }
}
