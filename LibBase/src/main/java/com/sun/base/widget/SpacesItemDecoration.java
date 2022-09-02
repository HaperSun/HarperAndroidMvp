package com.sun.base.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: Harper
 * @date: 2022/9/2
 * @note: 实现RecyclerView的等间距分割
 * 参考链接：https://www.jianshu.com/p/3b860938e503
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 左右间距
     */
    private final int leftRight;
    /**
     * 上下间距
     */
    private final int topBottom;
    /**
     * 是否需要第一排的上下间距
     */
    private final boolean needSideTop;

    public SpacesItemDecoration(int leftRight, int topBottom) {
        this(leftRight, topBottom, true);
    }

    public SpacesItemDecoration(int leftRight, int topBottom, boolean needSideTop) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
        this.needSideTop = needSideTop;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            getItemOffsetsGridLayoutManager(outRect, view, parent, gridLayoutManager);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            getItemOffsetsLinearLayoutManager(outRect, view, parent, linearLayoutManager);
        }
    }

    private void getItemOffsetsGridLayoutManager(Rect outRect, View view, RecyclerView parent, GridLayoutManager gridLayoutManager) {
        final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int spanCount = gridLayoutManager.getSpanCount();
        if (gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            //判断是否在第一排
            if (needSideTop && gridLayoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                //第一排的需要上面
                outRect.top = topBottom;
            }
            outRect.bottom = topBottom;
            //这里忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.getSpanSize() == spanCount) {
                //占满
                outRect.left = leftRight;
                outRect.right = leftRight;
            } else {
                outRect.left = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * leftRight);
                outRect.right = (int) (((float) leftRight * (spanCount + 1) / spanCount) - outRect.left);
            }
        } else {
            if (needSideTop && gridLayoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                //第一排的需要left
                outRect.left = leftRight;
            }
            outRect.right = leftRight;
            //这里忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.getSpanSize() == spanCount) {
                //占满
                outRect.top = topBottom;
                outRect.bottom = topBottom;
            } else {
                outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * topBottom);
                outRect.bottom = (int) (((float) topBottom * (spanCount + 1) / spanCount) - outRect.top);
            }
        }
    }

    private void getItemOffsetsLinearLayoutManager(Rect outRect, View view, RecyclerView parent, LinearLayoutManager linearLayoutManager) {
        int position = parent.getChildAdapterPosition(view);
        if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            //竖直方向的
            if (position != 0) {
                outRect.top = topBottom;
            }
            outRect.left = leftRight;
            outRect.right = leftRight;
        } else {
            //水平方向的
            outRect.top = topBottom;
            if (position != 0) {
                outRect.left = leftRight;
            }
            outRect.bottom = topBottom;
        }
    }

}
