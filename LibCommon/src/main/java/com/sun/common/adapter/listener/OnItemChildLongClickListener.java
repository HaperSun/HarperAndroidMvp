package com.sun.common.adapter.listener;

import android.view.View;

import com.sun.common.adapter.BaseAdapter;


/**
 * @author Harper
 * @date 2022/6/29
 * note:
 */
public abstract class OnItemChildLongClickListener extends SimpleClickListener {
    @Override
    public void onItemClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseAdapter adapter, View view, int position) {
        onSimpleItemChildLongClick(adapter, view, position);
    }

    public abstract void onSimpleItemChildLongClick(BaseAdapter adapter, View view, int position);
}
