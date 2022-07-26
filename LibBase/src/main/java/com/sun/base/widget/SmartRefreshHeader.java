package com.sun.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.sun.base.R;
import com.sun.base.util.ScreenUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author: Harper
 * @date: 2022/4/29
 * @note: 经典下拉头部
 */
@SuppressWarnings("unused")
public class SmartRefreshHeader extends RelativeLayout implements RefreshHeader {

    public static String REFRESH_HEADER_LAST_TIME = "yyyy/MM/dd HH:mm:ss";
    protected String KEY_LAST_UPDATE_TIME = "上次更新";

    private final Context mContext;
    protected Date mLastTime;
    protected TextView mLastUpdateText;
    protected ImageView mArrowView;
    protected SharedPreferences mShared;
    protected RefreshKernel mRefreshKernel;
    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected DateFormat mFormat = new SimpleDateFormat(REFRESH_HEADER_LAST_TIME, Locale.CHINA);
    protected int mFinishDuration = 500;
    protected int mBackgroundColor;
    protected boolean mEnableLastTime = true;
    protected AnimationDrawable frameAnim;
    protected AnimationDrawable frameAnim2;
    private boolean mIsRefreshing = false;

    public SmartRefreshHeader(Context context) {
        this(context, null);
    }

    public SmartRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public SmartRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        frameAnim = (AnimationDrawable) ContextCompat.getDrawable(mContext, R.drawable.anim_refresh);
        frameAnim2 = (AnimationDrawable) ContextCompat.getDrawable(mContext, R.drawable.anim_refresh_end);
        LayoutInflater.from(context).inflate(R.layout.view_grass_header_layout, this);
        mLastUpdateText = findViewById(R.id.tv_update_time);
        mArrowView = findViewById(R.id.iv_animation);
        @SuppressLint("CustomViewStyleable")
        TypedArray ta = context.obtainStyledAttributes(attrs, com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader);
        mFinishDuration = ta.getInt(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlFinishDuration, mFinishDuration);
        mEnableLastTime = ta.getBoolean(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlEnableLastTime, mEnableLastTime);
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];
        mLastUpdateText.setVisibility(mEnableLastTime ? VISIBLE : GONE);
        if (ta.hasValue(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlDrawableArrow));
        } else {
            mArrowView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.loading0));
        }
        mArrowView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.loading0));
        int tvSize = ScreenUtil.isTabletDevice() ? ScreenUtil.dp2px(9) : ScreenUtil.dp2px(5);
        if (ta.hasValue(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlTextSizeTime)) {
            mLastUpdateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlTextSizeTime, tvSize));
        } else {
            mLastUpdateText.setTextSize(tvSize);
        }
        int primaryColor = ta.getColor(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlPrimaryColor, 0);
        int accentColor = ta.getColor(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlAccentColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                setPrimaryColors(primaryColor, accentColor);
            } else {
                setPrimaryColors(primaryColor);
            }
        } else if (accentColor != 0) {
            setPrimaryColors(0, accentColor);
        }
        ta.recycle();
        try {
            //try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                List<Fragment> fragments = manager.getFragments();
                if (fragments.size() > 0) {
                    setLastUpdateTime(new Date());
                    return;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        KEY_LAST_UPDATE_TIME += context.getClass().getSimpleName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (isDragging && !mIsRefreshing) {
            if (percent < 0.076) {
                mArrowView.setImageResource(R.mipmap.loading0);
            } else if (percent < 0.076 * 2) {
                mArrowView.setImageResource(R.mipmap.loading1);
            } else if (percent < 0.076 * 3) {
                mArrowView.setImageResource(R.mipmap.loading2);
            } else if (percent < 0.076 * 4) {
                mArrowView.setImageResource(R.mipmap.loading3);
            } else if (percent < 0.076 * 5) {
                mArrowView.setImageResource(R.mipmap.loading4);
            } else if (percent < 0.076 * 6) {
                mArrowView.setImageResource(R.mipmap.loading5);
            } else if (percent < 0.076 * 7) {
                mArrowView.setImageResource(R.mipmap.loading6);
            } else if (percent < 0.076 * 8) {
                mArrowView.setImageResource(R.mipmap.loading7);
            } else if (percent < 0.076 * 9) {
                mArrowView.setImageResource(R.mipmap.loading8);
            } else if (percent < 0.076 * 10) {
                mArrowView.setImageResource(R.mipmap.loading9);
            } else if (percent < 0.076 * 11) {
                mArrowView.setImageResource(R.mipmap.loading10);
            } else if (percent < 0.076 * 12) {
                mArrowView.setImageResource(R.mipmap.loading11);
            } else if (percent < 0.076 * 13) {
                mArrowView.setImageResource(R.mipmap.loading12);
            }
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int headHeight, int extendHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        //延迟500毫秒之后再弹回
        if (success) {
            setLastUpdateTime(new Date());
        }
        return mFinishDuration;
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            if (!(getBackground() instanceof BitmapDrawable)) {
                setPrimaryColor(colors[0]);
            }
            if (colors.length > 1) {
                setAccentColor(colors[1]);
            } else {
                setAccentColor(colors[0] == 0xffffffff ? 0xff666666 : 0xffffffff);
            }
        }
    }

    @Override
    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                mLastUpdateText.setVisibility(mEnableLastTime ? VISIBLE : GONE);
                frameAnim.stop();
                frameAnim2.stop();
            case PullDownToRefresh:
                mArrowView.setVisibility(VISIBLE);
                break;
            case RefreshReleased:
                mIsRefreshing = true;
                mArrowView.setImageDrawable(frameAnim);
                frameAnim.start();
                break;
            case RefreshFinish:
                mIsRefreshing = false;
                mArrowView.setImageDrawable(frameAnim2);
                frameAnim2.start();
                break;
            case ReleaseToRefresh:
                break;
            case Loading:
                mArrowView.setVisibility(GONE);
                mLastUpdateText.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    public SmartRefreshHeader setProgressBitmap(Bitmap bitmap) {
        return this;
    }

    public SmartRefreshHeader setProgressDrawable(Drawable drawable) {
        return this;
    }

    public SmartRefreshHeader setProgressResource(@DrawableRes int resId) {
        return this;
    }

    public SmartRefreshHeader setArrowBitmap(Bitmap bitmap) {
        mArrowView.setImageBitmap(bitmap);
        return this;
    }

    public SmartRefreshHeader setArrowDrawable(Drawable drawable) {
        mArrowView.setImageDrawable(drawable);
        return this;
    }

    public SmartRefreshHeader setArrowResource(@DrawableRes int resId) {
        mArrowView.setImageResource(resId);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public SmartRefreshHeader setLastUpdateTime(Date time) {
        mLastTime = time;
        mLastUpdateText.setText("上次更新 " + mFormat.format(mLastTime));
        if (mShared != null && !isInEditMode()) {
            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public SmartRefreshHeader setTimeFormat(DateFormat format) {
        mFormat = format;
        mLastUpdateText.setText(mFormat.format(mLastTime));
        return this;
    }

    public SmartRefreshHeader setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }

    public SmartRefreshHeader setPrimaryColor(@ColorInt int primaryColor) {
        setBackgroundColor(mBackgroundColor = primaryColor);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
        }
        return this;
    }

    public SmartRefreshHeader setAccentColor(@ColorInt int accentColor) {
        return this;
    }

    public SmartRefreshHeader setPrimaryColorId(@ColorRes int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public SmartRefreshHeader setAccentColorId(@ColorRes int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public SmartRefreshHeader setFinishDuration(int delay) {
        mFinishDuration = delay;
        return this;
    }

    public SmartRefreshHeader setEnableLastTime(boolean enable) {
        mEnableLastTime = enable;
        mLastUpdateText.setVisibility(enable ? VISIBLE : GONE);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SmartRefreshHeader setTextSizeTitle(float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SmartRefreshHeader setTextSizeTitle(int unit, float size) {
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SmartRefreshHeader setTextSizeTime(float size) {
        mLastUpdateText.setTextSize(size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SmartRefreshHeader setTextSizeTime(int unit, float size) {
        mLastUpdateText.setTextSize(unit, size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SmartRefreshHeader setTextTimeMarginTop(float dp) {
        return setTextTimeMarginTopPx(ScreenUtil.dp2px(dp));
    }

    public SmartRefreshHeader setTextTimeMarginTopPx(int px) {
        MarginLayoutParams lp = (MarginLayoutParams) mLastUpdateText.getLayoutParams();
        lp.topMargin = px;
        mLastUpdateText.setLayoutParams(lp);
        return this;
    }

    public SmartRefreshHeader setDrawableMarginRight(float dp) {
        return setDrawableMarginRightPx(ScreenUtil.dp2px(dp));
    }

    public SmartRefreshHeader setDrawableMarginRightPx(int px) {
        MarginLayoutParams lpArrow = (MarginLayoutParams) mArrowView.getLayoutParams();
        mArrowView.setLayoutParams(lpArrow);
        return this;
    }

    public SmartRefreshHeader setDrawableSize(float dp) {
        return setDrawableSizePx(ScreenUtil.dp2px(dp));
    }

    public SmartRefreshHeader setDrawableSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        mArrowView.setLayoutParams(lpArrow);
        return this;
    }

    public SmartRefreshHeader setDrawableArrowSize(float dp) {
        return setDrawableArrowSizePx(ScreenUtil.dp2px(dp));
    }

    public SmartRefreshHeader setDrawableArrowSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        lpArrow.width = px;
        lpArrow.height = px;
        mArrowView.setLayoutParams(lpArrow);
        return this;
    }

    public SmartRefreshHeader setDrawableProgressSize(float dp) {
        return setDrawableProgressSizePx(ScreenUtil.dp2px(dp));
    }

    public SmartRefreshHeader setDrawableProgressSizePx(int px) {
        return this;
    }

    public ImageView getArrowView() {
        return mArrowView;
    }

    public TextView getLastUpdateText() {
        return mLastUpdateText;
    }

}
