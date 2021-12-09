package com.sun.demo2.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.demo2.R;
import com.sun.demo2.model.DataBean;

import java.util.List;

/**
 * @author Harper
 * @date 2021/12/9
 * note:
 */
public class EditTextInRecyclerViewAdapter extends RecyclerView.Adapter<EditTextInRecyclerViewAdapter.Holder> {

    private List<DataBean> mBeans;

    public void setAdapterData(List<DataBean> beans) {
        mBeans = beans;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_edit_text_in_recycler_view,
                parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditTextInRecyclerViewAdapter.Holder holder, int position) {
        DataBean bean = mBeans.get(position);
        holder.mTvName.setText(bean.getName());
        holder.mViewSplit.setVisibility(position == mBeans.size() - 1 ? View.GONE : View.VISIBLE);
        holder.mTvName.setOnClickListener(v -> {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener.onSelectPeople(position);
            }
        });

        if (holder.mEtCount.getTag() != null && holder.mEtCount.getTag() instanceof TextWatcher) {
            holder.mEtCount.removeTextChangedListener((TextWatcher) holder.mEtCount.getTag());
            holder.mEtCount.clearFocus();
        }
        String data = bean.getData() + "";
        holder.mEtCount.setText(data);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mOnAdapterClickListener != null) {
                    mOnAdapterClickListener.onInputCount(position, holder.mEtCount.getText().toString().trim());
                }
            }
        };
        holder.mEtCount.addTextChangedListener(textWatcher);
        holder.mEtCount.setTag(textWatcher);
        //点击删除
        holder.mVgDelete.setOnClickListener(v -> {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView mTvName;
        EditText mEtCount;
        ViewGroup mVgDelete;
        View mViewSplit;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mEtCount = itemView.findViewById(R.id.et_count);
            mVgDelete = itemView.findViewById(R.id.fl_delete);
            mViewSplit = itemView.findViewById(R.id.view_split);
        }
    }

    private OnAdapterClickListener mOnAdapterClickListener;

    public void setOnAdapterClickListener(OnAdapterClickListener onAdapterClickListener) {
        mOnAdapterClickListener = onAdapterClickListener;
    }

    public interface OnAdapterClickListener {

        /**
         * 点击选人
         *
         * @param position position
         */
        void onSelectPeople(int position);

        /**
         * 输入了频率
         *
         * @param position position
         * @param count    count
         */
        void onInputCount(int position, String count);

        /**
         * 点击了删除
         *
         * @param position position
         */
        void onDelete(int position);
    }
}
