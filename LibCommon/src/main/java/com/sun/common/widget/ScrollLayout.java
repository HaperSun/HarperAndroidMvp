package com.sun.common.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.sun.common.util.ScreenUtil;

/**
 * @author Harper
 * @date 2022/5/27
 * note:
 */
public class ScrollLayout extends ViewGroup {

    public final static int STATUS_DEFAULT = 0;
    public final static int STATUS_EXTEND = 1;
    public final static int STATUS_CLOSE = 2;
    //滑动中
    public final static int STATUS_ING = 3;

    private int touchSlop;
    private int slideSlop;
    private int offsetB;
    private ValueAnimator animator;

    private int width;
    private int height;
    private int offsetY;
    private int offsetExtend;
    //是负数
    private int offsetClose;
    private int offsetDefault;
    private int child_default_height;
    //TouchEvent_ACTION_DOWN坐标(dX,dY)
    private float dX, dY;
    //TouchEvent最后一次坐标(lastX,lastY)
    private float lastY;
    //本次touch事件是否有效
    private boolean isEventValid = true;
    private boolean isMoveValid;
    private int status;
    private int curY, finalY;
    private float factor;

    public ScrollLayout(Context context) {
        this(context, null);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        slideSlop = ScreenUtil.dp2px(context, 45);
        offsetB = ScreenUtil.dp2px(context, 40);
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            factor = (float) animation.getAnimatedValue();
            //这里打印会先打印出：1，然后再从0-1打印，因为每次在start时，都会先调用stop
            //curY可为负数或正数
            float scrollY = curY + (finalY - curY) * factor;
            scrollTo(0, (int) scrollY);
            postInvalidate();
            if (listener != null) {
                listener.onScrollChange(STATUS_ING);
                //大于设置的展开距离后才设置颜色渐变
                if (getScrollY() > offsetB) {
                    listener.onScrollProgress(255 * getScrollY() / offsetY);
                } else {
                    listener.onScrollProgress(0);
                }
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //动画全部更新完成后再走此方法：addUpdateListener->onAnimationEnd
            @Override
            public void onAnimationEnd(Animator animation) {
                //容错处理，正常情况下addUpdateListener执行完成后factor是1
                factor = 1;
                if (listener != null) {
                    listener.onScrollChange(status);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        //界面下边显示出来的子View的高度
        int childDefaultHeight = ScreenUtil.dp2px(getContext(), 210);
        offsetY = height - childDefaultHeight;
        offsetExtend = offsetY - offsetB;
        //负数 向下移动到剩下40dp的高度
        offsetClose = offsetY + offsetB - height;
        offsetDefault = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count > 0) {
            //第一个View距顶高度
            int top = offsetY;
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                int childHeight = child.getMeasuredHeight();
                //必须有三个孩子，多了不显示
                switch (i) {
                    case 0:
                        child.layout(0, top, width, top + childHeight);
                        top += childHeight;
                        break;
                    case 1:
                        //下：需要用屏幕高度-状态栏的高度(40dp)-第一个孩子的高度
                        child.layout(0, top, width, top + height - offsetB - getChildAt(0).getMeasuredHeight());
                        top += childHeight;
                        break;
                    case 2:
                        child.layout(0, offsetY, width, offsetY + childHeight);
                        break;
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //手势坐标系：下正右正
        float eX = ev.getX();
        float eY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = eX;
                lastY = dY = eY;
                isMoveValid = false;
                isEventValid = true;
                //首次进入getScrollY为0，当eY点击区域在offsetY距离下边，则进入
                //当点击距离在offsetY上边则进入else
                if (getScrollY() + eY > offsetY) {
                    //当动画正在执行时，事件不往View中传递
                    if (!(factor == 0 || factor == 1)) {
                        isEventValid = false;
                    } else {
                        //当子View为非滑动事件如TextView,LinearLayoutd等会调用它们的onTouchEvent的所有事件
                        //当子View为滑动事件如ListView,RecyclerView等不会调用onTouchEvent的Down事件，而是从Move事件开始嗲用
                        super.dispatchTouchEvent(ev);
                    }
                    return true;
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                //当动画正在执行时，点击滑动不起作用
                if (!isEventValid) {
                    return false;
                }
                //下滑offset就是负数，上滑就是正数
                int offset = (int) (lastY - eY);
                lastY = eY;
                if ((status == STATUS_EXTEND
                        || status == STATUS_CLOSE)
                        && super.dispatchTouchEvent(ev)) {
                    return true;
                }
                if (!isMoveValid
                        && Math.abs(eY - dY) > touchSlop
                        && Math.abs(eY - dY) > Math.abs(eX - dX)) {
                    isMoveValid = true;
                }
                if (isMoveValid) {
                    if (getScrollY() + offset <= offsetClose) {
                        scrollTo(0, offsetClose);
                        status = STATUS_CLOSE;
                        if (listener != null) {
                            listener.onScrollChange(status);
                        }
                    } else if (getScrollY() + offset >= offsetExtend) {
                        scrollTo(0, offsetExtend);
                        status = STATUS_EXTEND;
                        setRecyclerViewLastY(true);
                        if (listener != null) {
                            listener.onScrollChange(status);
                        }
                    } else {
                        //偏移量坐标  左正上正
                        scrollBy(0, offset);
                        if (listener != null) {
                            if (getScrollY() > offsetB) {
                                listener.onScrollProgress(255 * getScrollY() / offsetY);
                            } else {
                                listener.onScrollProgress(0);
                            }
                            listener.onScrollChange(STATUS_ING);
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isEventValid) {
                    return false;
                }
                if (isMoveValid
                        && getScrollY() > offsetClose
                        && getScrollY() < offsetExtend) {
                    dealUp(getScrollY());
                    isMoveValid = false;
                    return true;
                }
                setRecyclerViewLastY(status);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void dealUp(int scrollY) {
        switch (status) {
            case STATUS_DEFAULT:
                if (scrollY > slideSlop) {
                    //展开
                    toggle(STATUS_EXTEND);
                } else if (scrollY < -slideSlop) {
                    //关闭
                    toggle(STATUS_CLOSE);
                } else {
                    toggle(STATUS_DEFAULT);
                }
                break;
            case STATUS_EXTEND:
                //scrollY<0，说明至少滑动了offsetExtend的距离
                //偏移量scrollY是负数，所以关闭
                if (scrollY < offsetDefault) {
                    toggle(STATUS_CLOSE);
                } else if (scrollY < offsetExtend - slideSlop) {
                    toggle(STATUS_DEFAULT);
                } else {
                    //滑动的距离大于0小于slideSlop距离时，恢复到原状态
                    toggle(STATUS_EXTEND);
                }
                break;
            case STATUS_CLOSE:
                //偏移量大于0则展开
                if (scrollY > offsetDefault) {
                    toggle(STATUS_EXTEND);
                    //滑动距离大于slideSlop但小于0则滑动到初始状态
                } else if (scrollY > offsetClose + slideSlop) {
                    toggle(STATUS_DEFAULT);
                } else {
                    //滑动距离小于slideSlop距离则恢复到原状态
                    toggle(STATUS_CLOSE);
                }
                break;
        }
    }

    public void toggle(int status) {
        this.status = status;
        //y轴上的偏移量
        curY = getScrollY();
        switch (status) {
            case STATUS_DEFAULT:
                finalY = offsetDefault;
                setRecyclerViewLastY(status);
                break;
            case STATUS_EXTEND:
                finalY = offsetExtend;
                setRecyclerViewLastY(status);
                break;
            case STATUS_CLOSE:
                finalY = offsetClose;
                setRecyclerViewLastY(status);
                break;
        }
        start();
    }

    public void start() {
        stop();
        if (animator != null) {
            animator.start();
        }
    }

    public void stop() {
        if (animator != null) {
            animator.end();
        }
    }

    /**
     * 设置RecyclerView是否可滑动
     * 目的：恢复当从顶部下滑后再滑动到顶部，可跟随滑动
     */
    private void setRecyclerViewLastY(boolean isScroll) {
        ScrollRecyclerView.canScroll = isScroll;
    }

    private OnScrollChangedListener listener;

    public interface OnScrollChangedListener {
        void onScrollChange(int status);

        void onScrollProgress(int progress);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener l) {
        this.listener = l;
    }


    /**
     * 暂时不用
     *
     * @param status
     */
    private void setRecyclerViewLastY(int status) {

    }
}
