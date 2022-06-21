package com.sun.img.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.img.R;
import com.sun.img.databinding.ActivityPicturePreviewBinding;
import com.sun.img.fragment.ImgPreviewFragment;
import com.sun.img.model.bean.ImageItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 图片预览查看器
 */
public class ImgPreviewActivity extends BaseMvpActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    private static final String EXTRA_IMAGE_INDEX = "image_index";
    private static final String EXTRA_IMAGE_ITEMS = "image_items";
    private static final String EXTRA_NEED_ANIM = "need_anim";

    private ViewPager mViewPager;

    private TextView mIndicator;
    private int mPagerPosition;

    private ArrayList<ImageItem> mImgItems;
    private boolean mNeedAnim;

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param imgUrls 图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStart(Context context, String... imgUrls) {
        actionStart(context, true, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param needAnim 是否需要动画
     * @param imgUrls  图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStart(Context context, boolean needAnim, String... imgUrls) {
        actionStart(context, 0, needAnim, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgUrls       图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStart(Context context, int pagerPosition, String... imgUrls) {
        actionStart(context, pagerPosition, true, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param needAnim      是否需要动画
     * @param imgUrls       图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStart(Context context, int pagerPosition, boolean needAnim, String... imgUrls) {
        List<String> imgUrlArray = new ArrayList<>();
        Collections.addAll(imgUrlArray, imgUrls);
        actionStart(context, pagerPosition, imgUrlArray, needAnim);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param imgUrls 预览图片url数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStart(Context context, List<String> imgUrls) {
        actionStart(context, 0, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgUrls       预览图片url数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStart(Context context, int pagerPosition, List<String> imgUrls) {
        actionStart(context, pagerPosition, imgUrls, true);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgUrls       预览图片url数组 可以是本地文件路径，也可以是网络地址
     * @param needAnim      是否需要动画
     */
    public static void actionStart(Context context, int pagerPosition, List<String> imgUrls, boolean needAnim) {
        int size = imgUrls.size();
        ImageItem[] imgItems = new ImageItem[size];
        for (int i = 0; i < size; i++) {
            imgItems[i] = new ImageItem(null, imgUrls.get(i));
        }
        actionStartImageItem(context, pagerPosition, needAnim, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param imgItems 预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStartImageItem(Context context, ImageItem... imgItems) {
        actionStartImageItem(context, true, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param needAnim 是否需要动画
     * @param imgItems 预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStartImageItem(Context context, boolean needAnim, ImageItem... imgItems) {
        actionStartImageItem(context, 0, needAnim, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStartImageItem(Context context, int pagerPosition, ImageItem... imgItems) {
        actionStartImageItem(context, pagerPosition, true, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param needAnim      是否需要动画
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStartImageItem(Context context, int pagerPosition, boolean needAnim, ImageItem... imgItems) {
        Intent intent = new Intent(context, ImgPreviewActivity.class);
        intent.putExtra(EXTRA_IMAGE_INDEX, pagerPosition);
        intent.putExtra(EXTRA_IMAGE_ITEMS, imgItems);
        intent.putExtra(EXTRA_NEED_ANIM, needAnim);
        context.startActivity(intent);
        if (context instanceof Activity && needAnim) {
            // 第一个参数描述的是将要跳转到的activity的进入方式,第二个参数描述的是本界面退出的方式
            //“中心放大出现”
            ((Activity) context).overridePendingTransition(R.anim.scale_in, 0);
        }
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void actionStartImageItem(Context context, int pagerPosition, List<ImageItem> imgItems) {
        actionStartImageItem(context, pagerPosition, imgItems, true);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     * @param needAnim      是否需要动画
     */
    public static void actionStartImageItem(Context context, int pagerPosition, List<ImageItem> imgItems, boolean needAnim) {
        Intent intent = new Intent(context, ImgPreviewActivity.class);
        intent.putExtra(EXTRA_IMAGE_INDEX, pagerPosition);
        if (imgItems != null) {
            int size = imgItems.size();
            ImageItem[] imgItemArray = new ImageItem[size];
            imgItemArray = imgItems.toArray(imgItemArray);
            intent.putExtra(EXTRA_IMAGE_ITEMS, imgItemArray);
        }
        intent.putExtra(EXTRA_NEED_ANIM, needAnim);
        context.startActivity(intent);
        if (context instanceof Activity && needAnim) {
            // 第一个参数描述的是将要跳转到的activity的进入方式,第二个参数描述的是本界面退出的方式
            //“中心放大出现”
            ((Activity) context).overridePendingTransition(R.anim.scale_in, 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mNeedAnim) {
            super.onBackPressed();
            return;
        }
        finish();
        //中心缩小退出
        overridePendingTransition(0, R.anim.scale_out);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_picture_preview;
    }

    @Override
    public void initData() {
        try {
            Intent intent = getIntent();
            mPagerPosition = intent.getIntExtra(EXTRA_IMAGE_INDEX, 0);
            Parcelable[] imgItems = intent.getParcelableArrayExtra(EXTRA_IMAGE_ITEMS);
            if (imgItems.length <= 0) {
                return;
            }
            mImgItems = new ArrayList<>();
            for (Parcelable imgItem : imgItems) {
                mImgItems.add((ImageItem) imgItem);
            }
            mNeedAnim = intent.getBooleanExtra(EXTRA_NEED_ANIM, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        ActivityPicturePreviewBinding binding = (ActivityPicturePreviewBinding) mViewDataBinding;
        mViewPager = binding.pager;
        mIndicator = binding.indicator;
        binding.previewClose.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager(), mImgItems);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //do nothing
            }

            @Override
            public void onPageSelected(int position) {
                String txt = (position + 1) + "/" + mImgItems.size();
                mIndicator.setText(txt);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //do nothing
            }
        });
        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        String txt = (mPagerPosition + 1) + "/" + mImgItems.size();
        mIndicator.setText(txt);
        mViewPager.setCurrentItem(mPagerPosition);
        //只有一张图片时不显示下标指示
        if (mImgItems.size() < 2) {
            mIndicator.setVisibility(View.GONE);
        } else {
            mIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, mViewPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<ImageItem> mImgItems;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<ImageItem> imgItems) {
            super(fm);
            this.mImgItems = imgItems;
        }

        @Override
        public Fragment getItem(int position) {
            ImageItem imgItem = mImgItems.get(position);
            return ImgPreviewFragment.newInstance(imgItem);
        }

        @Override
        public int getCount() {
            return mImgItems == null ? 0 : mImgItems.size();
        }
    }
}
