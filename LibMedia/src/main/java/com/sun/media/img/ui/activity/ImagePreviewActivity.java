package com.sun.media.img.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.bean.Parameter;
import com.sun.media.R;
import com.sun.media.databinding.ActivityImagePreviewBinding;
import com.sun.media.img.model.bean.ImageItem;
import com.sun.media.img.ui.fragment.ImagePreviewFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 图片预览查看器
 */
public class ImagePreviewActivity extends BaseMvpActivity<ActivityImagePreviewBinding> {

    private int mPagerPosition;
    private ArrayList<ImageItem> mImgItems;
    private boolean mNeedAnim;

    @Override
    protected boolean enableDarkStatusBarAndSetTitle() {
        mStatusBarColor = R.color.black;
        return true;
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param imgUrls 图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void start(Context context, String... imgUrls) {
        start(context, true, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param needAnim 是否需要动画
     * @param imgUrls  图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void start(Context context, boolean needAnim, String... imgUrls) {
        start(context, 0, needAnim, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgUrls       图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void start(Context context, int pagerPosition, String... imgUrls) {
        start(context, pagerPosition, true, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param needAnim      是否需要动画
     * @param imgUrls       图片url 可以是本地文件路径，也可以是网络地址
     */
    public static void start(Context context, int pagerPosition, boolean needAnim, String... imgUrls) {
        List<String> imgUrlArray = new ArrayList<>();
        Collections.addAll(imgUrlArray, imgUrls);
        start(context, pagerPosition, imgUrlArray, needAnim);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param imgUrls 预览图片url数组 可以是本地文件路径，也可以是网络地址
     */
    public static void start(Context context, List<String> imgUrls) {
        start(context, 0, imgUrls);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgUrls       预览图片url数组 可以是本地文件路径，也可以是网络地址
     */
    public static void start(Context context, int pagerPosition, List<String> imgUrls) {
        start(context, pagerPosition, imgUrls, true);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgUrls       预览图片url数组 可以是本地文件路径，也可以是网络地址
     * @param needAnim      是否需要动画
     */
    public static void start(Context context, int pagerPosition, List<String> imgUrls, boolean needAnim) {
        int size = imgUrls.size();
        ImageItem[] imgItems = new ImageItem[size];
        for (int i = 0; i < size; i++) {
            imgItems[i] = new ImageItem(null, imgUrls.get(i));
        }
        startImageItem(context, pagerPosition, needAnim, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param imgItems 预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void startImageItem(Context context, ImageItem... imgItems) {
        startImageItem(context, true, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param needAnim 是否需要动画
     * @param imgItems 预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void startImageItem(Context context, boolean needAnim, ImageItem... imgItems) {
        startImageItem(context, 0, needAnim, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void startImageItem(Context context, int pagerPosition, ImageItem... imgItems) {
        startImageItem(context, pagerPosition, true, imgItems);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param needAnim      是否需要动画
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     */
    public static void startImageItem(Context context, int pagerPosition, boolean needAnim, ImageItem... imgItems) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(Parameter.INDEX, pagerPosition);
        intent.putExtra(Parameter.ITEM, imgItems);
        intent.putExtra(Parameter.NEED_ANIMATION, needAnim);
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
    public static void startImageItem(Context context, int pagerPosition, List<ImageItem> imgItems) {
        startImageItem(context, pagerPosition, imgItems, true);
    }

    /**
     * 外部启动当前页面
     *
     * @param context
     * @param pagerPosition 初始进来位置，从0开始计数
     * @param imgItems      预览图片数组 可以是本地文件路径，也可以是网络地址
     * @param needAnim      是否需要动画
     */
    public static void startImageItem(Context context, int pagerPosition, List<ImageItem> imgItems, boolean needAnim) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(Parameter.INDEX, pagerPosition);
        if (imgItems != null) {
            int size = imgItems.size();
            ImageItem[] imgItemArray = new ImageItem[size];
            imgItemArray = imgItems.toArray(imgItemArray);
            intent.putExtra(Parameter.ITEM, imgItemArray);
        }
        intent.putExtra(Parameter.NEED_ANIMATION, needAnim);
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
        close();
        //中心缩小退出
        overridePendingTransition(0, R.anim.scale_out);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_image_preview;
    }

    @Override
    public void initData() {
        try {
            Intent intent = getIntent();
            mPagerPosition = intent.getIntExtra(Parameter.INDEX, 0);
            Parcelable[] imgItems = intent.getParcelableArrayExtra(Parameter.ITEM);
            if (imgItems.length <= 0) {
                return;
            }
            mImgItems = new ArrayList<>();
            for (Parcelable imgItem : imgItems) {
                mImgItems.add((ImageItem) imgItem);
            }
            mNeedAnim = intent.getBooleanExtra(Parameter.NEED_ANIMATION, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        vdb.previewClose.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager(), mImgItems);
        vdb.pager.setAdapter(adapter);
        vdb.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //do nothing
            }

            @Override
            public void onPageSelected(int position) {
                String txt = (position + 1) + "/" + mImgItems.size();
                vdb.indicator.setText(txt);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //do nothing
            }
        });
        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(Parameter.STATE_POSITION);
        }
        String txt = (mPagerPosition + 1) + "/" + mImgItems.size();
        vdb.indicator.setText(txt);
        vdb.pager.setCurrentItem(mPagerPosition);
        //只有一张图片时不显示下标指示
        vdb.indicator.setVisibility(mImgItems.size() < 2 ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Parameter.STATE_POSITION, vdb.pager.getCurrentItem());
    }

    private static class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<ImageItem> mImgItems;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<ImageItem> imgItems) {
            super(fm);
            this.mImgItems = imgItems;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            ImageItem imgItem = mImgItems.get(position);
            return ImagePreviewFragment.newInstance(imgItem);
        }

        @Override
        public int getCount() {
            return mImgItems == null ? 0 : mImgItems.size();
        }
    }
}
