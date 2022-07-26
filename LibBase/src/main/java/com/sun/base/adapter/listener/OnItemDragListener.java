package com.sun.base.adapter.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Harper
 * @date 2022/6/29
 * note:
 */
public interface OnItemDragListener {
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);

    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);

    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
