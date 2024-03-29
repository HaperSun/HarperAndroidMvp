/*
 * Copyright (c) 2016 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.library.emoji;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sun.library.R;
import com.sun.library.emoji.model.EmojiIconPage;
import com.sun.library.emoji.util.EmojiUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/24
 * @note:
 */
public class EmojiIconsView extends FrameLayout implements ViewPager.OnPageChangeListener {
    ViewPager mViewPager;
    private List<EmojiIconPage> mPages;
    private ViewGroup mTabsContainer;
    private View[] mTabs;
    private View mLastTab;

    public EmojiIconsView(Context context) {
        this(context, null);
    }

    public EmojiIconsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_emojicons, this);
        mViewPager = (ViewPager) findViewById(R.id.emojis_pager);
        mTabsContainer = (ViewGroup) findViewById(R.id.emojis_tab);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mViewPager.removeOnPageChangeListener(this);
    }

    public void setPages(@NonNull List<EmojiIconPage> pages) {
        this.mPages = pages;
        if (mTabs == null || mTabs.length != pages.size()) {
            mTabs = new View[pages.size()];
        } else {
            Arrays.fill(mTabs, null);
        }

        for (int i = 0; i < mTabsContainer.getChildCount() - 2; i++) {
            mTabsContainer.removeViewAt(0);
        }

        int index = 0;
        for (EmojiIconPage page : pages) {
            addTabIcon(page, index++);
            addTabDivider();
        }
        onPageSelected(0);
        mViewPager.setAdapter(new EmojiconGridViewPagerAdapter(getContext(), pages));
    }

    private void addTabDivider() {
        View divider = new View(getContext());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mViewPager.setBackgroundColor(getContext().getResources().getColor(R.color.cl_8f8f8f));
        } else {
            mViewPager.setBackgroundColor(getContext().getColor(R.color.cl_8f8f8f));
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        mTabsContainer.addView(divider, mTabsContainer.getChildCount() - 2, params);
    }

    private void addTabIcon(EmojiIconPage page, int index) {
        ImageButton icon = new ImageButton(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        icon.setBackground(null);
        icon.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon.setImageDrawable(getContext().getResources().getDrawable(page.getIcon()));
        } else {
            icon.setImageDrawable(getContext().getDrawable(page.getIcon()));
        }
        mTabsContainer.addView(icon, mTabsContainer.getChildCount() - 2, params);
        mTabs[index] = icon;
        final int indexToMove = index;
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(indexToMove, true);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mTabs == null || position >= mTabs.length) {
            return;
        }
        if (mLastTab != null) {
            mLastTab.setSelected(false);
        }
        mLastTab = mTabs[position];
        mLastTab.setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    static class EmojiconGridViewPagerAdapter extends PagerAdapter {
        private Context context;
        private final List<EmojiIconPage> pages;
        private EmojiIconGridView.SavedState[] savedStates;

        public EmojiconGridViewPagerAdapter(Context context, @NonNull List<EmojiIconPage> pages) {
            this.context = context;
            this.pages = pages;
            this.savedStates = new EmojiIconGridView.SavedState[pages.size()];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            EmojiIconPage emojiIconPage = pages.get(position);
            EmojiIconGridView emojiGridView = new EmojiIconGridView(context);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                emojiGridView.setId(View.generateViewId());
            } else {
                emojiGridView.setId(EmojiUtil.generateViewId());
            }
            container.addView(emojiGridView);
            emojiGridView.setEmojiData(emojiIconPage.getType(), emojiIconPage.getData(), emojiIconPage.isUseSystemDefaults());
            if (savedStates[position] != null) {
                SparseArray sparseArray = new SparseArray(1);
                sparseArray.put(emojiGridView.getId(), savedStates[position]);
                emojiGridView.restoreHierarchyState(sparseArray);
            }
            return emojiGridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            EmojiIconGridView view = (EmojiIconGridView) object;
            savedStates[position] = (EmojiIconGridView.SavedState) view.onSaveInstanceState();
            container.removeView((View) object);
        }

        @Override
        public Parcelable saveState() {
            Bundle state = new Bundle();
            state.putParcelableArray("states", savedStates);
            return state;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            if (state != null) {
                Bundle bundle = (Bundle) state;
                Parcelable[] states = bundle.getParcelableArray("states");
                savedStates = new EmojiIconGridView.SavedState[states.length];
                for (int i = 0; i < states.length; i++) {
                    savedStates[i] = (EmojiIconGridView.SavedState) states[i];
                }
            }
            super.restoreState(state, loader);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
