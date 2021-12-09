package com.sun.demo2.activity;


import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.ui.activity.BaseMvpActivity;
import com.sun.demo2.R;
import com.sun.demo2.adapter.EditTextInRecyclerViewAdapter;
import com.sun.demo2.databinding.ActivityEditTextInRecyclerViewBinding;
import com.sun.demo2.model.DataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/9
 * @note: RecyclerView中的EditText
 */
public class EditTextInRecyclerViewActivity extends BaseMvpActivity implements EditTextInRecyclerViewAdapter.OnAdapterClickListener {

    private RecyclerView mRecyclerView;
    private EditTextInRecyclerViewAdapter mAdapter;
    private List<DataBean> mDataBeans;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EditTextInRecyclerViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_edit_text_in_recycler_view;
    }

    @Override
    public void initView() {
        ActivityEditTextInRecyclerViewBinding binding = (ActivityEditTextInRecyclerViewBinding) mViewDataBinding;
        mRecyclerView = binding.recyclerView;
        binding.ivAddItem.setOnClickListener(v -> {
            mDataBeans.add(new DataBean("",0));
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void requestData() {
        mDataBeans = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDataBeans.add(new DataBean("名称" + i + 1, i + 10));
        }
        mAdapter = new EditTextInRecyclerViewAdapter();
        mAdapter.setAdapterData(mDataBeans);
        mRecyclerView.setAdapter(mAdapter);
        //解决recyclerview在NestScrollView中是使用时，滑动卡顿问题
//        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter.setOnAdapterClickListener(this);
    }

    @Override
    public void onSelectPeople(int position) {

    }

    @Override
    public void onInputCount(int position, String count) {
        for (int i = 0; i < mDataBeans.size(); i++) {
            if (i == position) {
                DataBean bean = mDataBeans.get(i);
                try {
                    bean.setData(Integer.parseInt(count));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDelete(int position) {
        mDataBeans.remove(position);
        mAdapter.notifyDataSetChanged();
    }
}