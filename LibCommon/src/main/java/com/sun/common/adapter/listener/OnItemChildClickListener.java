package com.sun.common.adapter.listener;

import android.view.View;

import com.sun.common.adapter.BaseAdapter;


/**
 * @author Harper
 * @date 2022/6/29
 * note:
 */
public abstract class OnItemChildClickListener extends SimpleClickListener {
    @Override
    public void onItemClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseAdapter adapter, View view, int position) {
        onSimpleItemChildClick(adapter, view, position);
    }

    @Override
    public void onItemChildLongClick(BaseAdapter adapter, View view, int position) {

    }

    public abstract void onSimpleItemChildClick(BaseAdapter adapter, View view, int position);
}
