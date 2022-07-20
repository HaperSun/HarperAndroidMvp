package com.sun.base.select;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.sun.base.R;

/**
 * @author: Harper
 * @date: 2022/7/7
 * @note: GitHub: https://github.com/laobie
 */
public class SelectableTextHelper {
    private static int COLOR_SELECTED = 0xFFE3EDF3;//选中后文本的颜色值
    private static int COLOR_HANDLE = 0xFF00BAFF;//文本左右竖线的颜色值
    private final static int DEFAULT_SELECTION_LENGTH = 1;//默认选中的文本长度
    private static final int DEFAULT_SHOW_DURATION = 100;

    private static SelectableTextHelper waitingHelper;
    private CursorHandle mStartHandle;
    private CursorHandle mEndHandle;
    private OperateWindow mOperateWindow;
    private SelectionInfo mSelectionInfo = new SelectionInfo();
    private OnSelectListener mSelectListener;

    private Context mContext;
    private TextView mTextView;
    private Spannable mSpannable;

    private int mTouchX;
    private int mTouchY;

    private int mSelectedColor;//选中后文本的颜色值
    private int mCursorHandleColor;//文本左右竖线的颜色值
    private int mCursorHandleSize;//暂时没用到
    private BackgroundColorSpan mSpan;
    private boolean isHideWhenScroll;
    private boolean isCurrentHide = true;

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    public static void setTextSelectable(TextView textView) {
        SelectableTextHelper selectableTextHelper = new Builder(textView)
                .setSelectedColor(COLOR_SELECTED)
                .setCursorHandleSizeInDp(11)
                .setCursorHandleColor(COLOR_HANDLE)
                //显示弹框选择
                .build(true);

        selectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {

            }
        });
    }

    public static void setWaitingHelper(TextView textView) {
        if (waitingHelper != null) {
            waitingHelper.destroy();
        }
        waitingHelper = new Builder(textView)
                .setSelectedColor(COLOR_SELECTED)
                .setCursorHandleSizeInDp(11)
                .setCursorHandleColor(COLOR_HANDLE)
                .build(false);

        waitingHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {

            }
        });
    }

    public static void showWaitingTextSelector(boolean allSelected) {
        if (waitingHelper == null) {
            return;
        }
        if (allSelected) {
            waitingHelper.showAllTextSelected();
        } else {
            waitingHelper.showSelectView(waitingHelper.mTouchX, waitingHelper.mTouchY);
        }
    }

    public static void hideShowingTextSelector() {
        if (waitingHelper == null || waitingHelper.isCurrentHide) {
            return;
        }
        waitingHelper.hideSelectView();
        waitingHelper.resetSelectionInfo();
    }

    private SelectableTextHelper(Builder builder, boolean showImmediately) {
        mTextView = builder.mTextView;
        mContext = mTextView.getContext().getApplicationContext();
        mSelectedColor = builder.mSelectedColor;
        mCursorHandleColor = builder.mCursorHandleColor;
        mCursorHandleSize = TextLayoutUtil.dp2px(mContext, builder.mCursorHandleSizeInDp);
        init(showImmediately);
    }

    private void init(boolean showImmediately) {
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        if (showImmediately) {
            mTextView.setOnLongClickListener(v -> {
                showSelectView(mTouchX, mTouchY);
                return true;
            });
            mTextView.setOnClickListener(v -> {
                if (mStartHandle == null || mEndHandle == null) {
                    return;
                }
                if (mTouchX < mStartHandle.getExtraX() || mTouchX > mEndHandle.getExtraX() + mEndHandle.getWidth()
                        || mTouchY < mStartHandle.getExtraY() || mTouchY > mEndHandle.getExtraY() + mEndHandle.getHeight()) {
                    SelectableTextHelper.hideShowingTextSelector();
                }
            });
        }

        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                return false;
            }
        });
        mTextView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mContext = mTextView.getContext().getApplicationContext();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (waitingHelper != null && waitingHelper.mTextView == mTextView) {
                    waitingHelper.mContext = null;
                    waitingHelper = null;
                }
                destroy();
            }
        });

        mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isHideWhenScroll) {
                    isHideWhenScroll = false;
                    postShowSelectView(DEFAULT_SHOW_DURATION);
                }
                return true;
            }
        };
        mTextView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);

        mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                hideShowingTextSelector();
                if (!isHideWhenScroll && !isCurrentHide) {
                    isHideWhenScroll = true;
                    if (mOperateWindow != null) {
                        mOperateWindow.dismiss();
                    }
                    if (mStartHandle != null) {
                        mStartHandle.dismiss();
                    }
                    if (mEndHandle != null) {
                        mEndHandle.dismiss();
                    }
                }
            }
        };
        mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);

        mOperateWindow = new OperateWindow(mContext);
    }

    private void postShowSelectView(int duration) {
        mTextView.removeCallbacks(mShowSelectViewRunnable);
        if (duration <= 0) {
            mShowSelectViewRunnable.run();
        } else {
            mTextView.postDelayed(mShowSelectViewRunnable, duration);
        }
    }

    private final Runnable mShowSelectViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (isCurrentHide) {
                return;
            }
            if (mOperateWindow != null) {
                mOperateWindow.show();
            }
            if (mStartHandle != null) {
                mStartHandle.showCursorHandle();
            }
            if (mEndHandle != null) {
                mEndHandle.showCursorHandle();
            }
        }
    };

    private void hideSelectView() {
        isCurrentHide = true;
        if (mStartHandle != null) {
            mStartHandle.dismiss();
        }
        if (mEndHandle != null) {
            mEndHandle.dismiss();
        }
        if (mOperateWindow != null) {
            mOperateWindow.dismiss();
        }
    }

    private void resetSelectionInfo() {
        mSelectionInfo.mSelectionContent = null;
        if (mSpannable != null && mSpan != null) {
            mSpannable.removeSpan(mSpan);
            mSpan = null;
        }
    }

    private void showSelectView(int x, int y) {
        if (waitingHelper != null) {
            waitingHelper.destroy();
        }
        hideSelectView();
        resetSelectionInfo();
        if (mStartHandle == null) {
            mStartHandle = new CursorHandle(true);
        }
        if (mEndHandle == null) {
            mEndHandle = new CursorHandle(false);
        }
        if (mOperateWindow == null) {
            mOperateWindow = new OperateWindow(mContext);
        }
        int startOffset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);
        int endOffset = startOffset + DEFAULT_SELECTION_LENGTH;
        if (mTextView.getText() instanceof Spannable) {
            mSpannable = (Spannable) mTextView.getText();
        }
        if (mSpannable == null || startOffset >= mTextView.getText().length()) {
            return;
        }
        selectText(startOffset, endOffset);
        mStartHandle.showCursorHandle();
        mEndHandle.showCursorHandle();
        mOperateWindow.show();
        isCurrentHide = false;
        waitingHelper = this;
    }

    private void showAllTextSelected() {
        hideSelectView();
        isCurrentHide = false;
        if (mStartHandle == null) {
            mStartHandle = new CursorHandle(true);
        }
        if (mEndHandle == null) {
            mEndHandle = new CursorHandle(false);
        }
        if (mOperateWindow == null) {
            mOperateWindow = new OperateWindow(mContext);
        }
        if (mSpannable == null) {
            mSpannable = (Spannable) mTextView.getText();
        }
        selectText(0, mTextView.getText().length());
        mStartHandle.showCursorHandle();
        mEndHandle.showCursorHandle();
        mOperateWindow.show();
    }

    private void selectText(int startPos, int endPos) {
        if (startPos != -1) {
            mSelectionInfo.mStart = startPos;
        }
        if (endPos != -1) {
            mSelectionInfo.mEnd = endPos;
        }
        if (mSelectionInfo.mStart > mSelectionInfo.mEnd) {
            int temp = mSelectionInfo.mStart;
            mSelectionInfo.mStart = mSelectionInfo.mEnd;
            mSelectionInfo.mEnd = temp;
        }
        if (mSpannable != null) {
            if (mSpan == null) {
                mSpan = new BackgroundColorSpan(mSelectedColor);
            }
            if (mSelectionInfo.mEnd > mSpannable.length()) {
                mSelectionInfo.mEnd = mSpannable.length();
            }
            mSelectionInfo.mSelectionContent = mSpannable.subSequence(mSelectionInfo.mStart, mSelectionInfo.mEnd).toString();
            mSpannable.setSpan(mSpan, mSelectionInfo.mStart, mSelectionInfo.mEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if (mSelectListener != null) {
                mSelectListener.onTextSelected(mSelectionInfo.mSelectionContent);
            }
        }
    }

    public void setSelectListener(OnSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    private void destroy() {
        resetSelectionInfo();
        hideSelectView();
        mStartHandle = null;
        mEndHandle = null;
        mOperateWindow = null;
    }

    /**
     * Operate windows : copy, select all
     */
    private class OperateWindow {

        private PopupWindow mWindow;
        private int[] mTempCoors = new int[2];

        private int mWidth;
        private int mHeight;

        public OperateWindow(final Context context) {
            View contentView = LayoutInflater.from(context).inflate(R.layout.layout_operate_windows, null);
            contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWidth = contentView.getMeasuredWidth();
            mHeight = contentView.getMeasuredHeight();
            mWindow =
                    new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
            mWindow.setClippingEnabled(false);
            contentView.findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setPrimaryClip(
                            ClipData.newPlainText(mSelectionInfo.mSelectionContent, mSelectionInfo.mSelectionContent));
                    if (mSelectListener != null) {
                        mSelectListener.onTextSelected(mSelectionInfo.mSelectionContent);
                    }
                    SelectableTextHelper.this.resetSelectionInfo();
                    SelectableTextHelper.this.hideSelectView();//隐藏复制全选框
                }
            });
            contentView.findViewById(R.id.tv_select_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAllTextSelected();
                }
            });
        }


        void show() {
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();
            int x = (int) layout.getPrimaryHorizontal(mSelectionInfo.mStart);
            int y = layout.getLineTop(layout.getLineForOffset(mSelectionInfo.mStart));
            int circleSize = mStartHandle.mCircleRadius * 2;
            int offsetX = mWidth / 2 - ((int) layout.getPrimaryHorizontal(mSelectionInfo.mEnd) - x) / 2;
            int offsetY = mHeight + circleSize;
            int posX = x + getExtraX() - offsetX;
            int posY = y + getExtraY() - offsetY;
            if (posX <= 0) {
                posX = circleSize;
            }
            if (posY < 0) {
                posY = circleSize;
            }
            if (posX + mWidth > TextLayoutUtil.getScreenWidth(mContext)) {
                posX = TextLayoutUtil.getScreenWidth(mContext) - mWidth - circleSize;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWindow.setElevation(8f);
            }
            mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
        }

        public void dismiss() {
            mWindow.dismiss();
        }

        public boolean isShowing() {
            return mWindow.isShowing();
        }

        public int getExtraX() {
            return mTempCoors[0] + mTextView.getPaddingLeft();
        }

        public int getExtraY() {
            return mTempCoors[1] + mTextView.getPaddingTop();
        }
    }

    /**
     * 文本左右两侧的竖线
     */
    private class CursorHandle extends View {

        private PopupWindow mPopupWindow;
        private Paint mPaint;
        private int mCircleRadius = getResources().getDimensionPixelOffset(R.dimen.dp4_5);
        private int mPadding = getResources().getDimensionPixelOffset(R.dimen.dp10);//上下左右padding值
        private int mTotalWidth = mCircleRadius * 2 + mPadding;//总宽度
        private int mTotalHeight = getResources().getDimensionPixelOffset(R.dimen.dp30) + mPadding;//总高度
        private int mLineWidth = getResources().getDimensionPixelOffset(R.dimen.dp1);//竖线的宽度
        private boolean isLeft;

        public CursorHandle(boolean isLeft) {
            super(mContext);
            this.isLeft = isLeft;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mCursorHandleColor);

            mPopupWindow = new PopupWindow(this);
            mPopupWindow.setClippingEnabled(false);
            mPopupWindow.setWidth(mTotalWidth);
            mPopupWindow.setHeight(mTotalHeight);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int circleX = mTotalWidth / 2;
            int circleY = isLeft ? mCircleRadius + mPadding : mTotalHeight - mCircleRadius - mPadding;
            canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);

            int lineLeft = mCircleRadius - (int) (mLineWidth / 1.0 * 2) + mPadding / 2;
            int lineTop = isLeft ? mCircleRadius * 2 + mPadding : 0;
            int lineRight = mCircleRadius + (int) (mLineWidth / 1.0 * 2) + mPadding / 2;
            int lineBottom = isLeft ? mTotalHeight : mTotalHeight - mCircleRadius * 2 - mPadding;
            canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, mPaint);
        }

        private int mAdjustX;
        private int mAdjustY;

        private int mBeforeDragStart;
        private int mBeforeDragEnd;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mBeforeDragStart = mSelectionInfo.mStart;
                    mBeforeDragEnd = mSelectionInfo.mEnd;
                    mAdjustX = (int) event.getX();
                    mAdjustY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mOperateWindow.show();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mOperateWindow.dismiss();
                    int rawX = (int) event.getRawX();
                    int rawY = (int) event.getRawY();
//                    update(rawX + mAdjustX - mWidth, rawY + mAdjustY - mHeight);
                    update(rawX - mCircleRadius * 2, rawY - mCircleRadius * 2);
                    break;
            }
            return true;
        }

        private void changeDirection() {
            isLeft = !isLeft;
            invalidate();
        }

        public void dismiss() {
            mPopupWindow.dismiss();
        }

        private int[] mTempCoors = new int[2];

        public void update(int x, int y) {
            mTextView.getLocationInWindow(mTempCoors);
            int oldOffset = isLeft ? mSelectionInfo.mStart : mSelectionInfo.mEnd;

            y -= mTempCoors[1];

            int offset = TextLayoutUtil.getHysteresisOffset(mTextView, x, y, oldOffset);

            if (offset != oldOffset) {
                resetSelectionInfo();
                if (isLeft) {
                    if (offset > mBeforeDragEnd) {
                        CursorHandle handle = getCursorHandle(false);
                        changeDirection();
                        handle.changeDirection();
                        mBeforeDragStart = mBeforeDragEnd;
                        selectText(mBeforeDragEnd, offset);
                        handle.showCursorHandle();
                    } else {
                        selectText(offset, -1);
                    }
                    showCursorHandle();
                } else {
                    if (offset < mBeforeDragStart) {
                        CursorHandle handle = getCursorHandle(true);
                        handle.changeDirection();
                        changeDirection();
                        mBeforeDragEnd = mBeforeDragStart;
                        selectText(offset, mBeforeDragStart);
                        handle.showCursorHandle();
                    } else {
                        selectText(mBeforeDragStart, offset);
                    }
                    showCursorHandle();
                }
            }
        }

        public void showCursorHandle() {
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();
            int offset = isLeft ? mSelectionInfo.mStart : mSelectionInfo.mEnd;
            int x = (int) layout.getPrimaryHorizontal(offset);
            int y = layout.getLineBottom(layout.getLineForOffset(offset));

            int offsetX = mCircleRadius + mPadding / 2;
            int offsetY = isLeft ? mTotalHeight : mTextView.getLineHeight();

            if (mPopupWindow.isShowing()) {
                mPopupWindow.update(x + getExtraX() - offsetX, y + getExtraY() - offsetY, -1, -1);
            } else {
                mPopupWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, x + getExtraX() - offsetX,
                        y + getExtraY() - offsetY);
            }
        }

        public int getExtraX() {
            return mTempCoors[0] + mTextView.getPaddingLeft();
        }

        public int getExtraY() {
            return mTempCoors[1] + mTextView.getPaddingTop();
        }
    }

    private CursorHandle getCursorHandle(boolean isLeft) {
        if (mStartHandle.isLeft == isLeft) {
            return mStartHandle;
        } else {
            return mEndHandle;
        }
    }

    public static class Builder {
        private TextView mTextView;
        private int mCursorHandleColor = COLOR_HANDLE;
        private int mSelectedColor = COLOR_SELECTED;
        private float mCursorHandleSizeInDp = 9;

        public Builder(TextView textView) {
            mTextView = textView;
        }

        public Builder setCursorHandleColor(@ColorInt int cursorHandleColor) {
            mCursorHandleColor = cursorHandleColor;
            return this;
        }

        public Builder setCursorHandleSizeInDp(float cursorHandleSizeInDp) {
            mCursorHandleSizeInDp = cursorHandleSizeInDp;
            return this;
        }

        public Builder setSelectedColor(@ColorInt int selectedBgColor) {
            mSelectedColor = selectedBgColor;
            return this;
        }

        public SelectableTextHelper build(boolean showImmediately) {
            return new SelectableTextHelper(this, showImmediately);
        }
    }
}


