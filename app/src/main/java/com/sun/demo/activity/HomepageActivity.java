package com.sun.demo.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.ui.activity.BaseMvpActivity;
import com.sun.demo.R;
import com.sun.demo.databinding.ActivityHomepageBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/6
 * @note:
 */
public class HomepageActivity extends BaseMvpActivity {

    private Context mContext;
    private List<String> mTitles;
    private RecyclerView mRecyclerView;

    @Override
    public int layoutId() {
        return R.layout.activity_homepage;
    }

    @Override
    public void initView() {
        ActivityHomepageBinding binding = (ActivityHomepageBinding) mViewDataBinding;
        mRecyclerView = binding.recyclerView;
    }

    @Override
    public void requestData() {
        mContext = HomepageActivity.this;
        mTitles = getTitles();
        Adapter adapter = new Adapter();
        mRecyclerView.setAdapter(adapter);
    }

    private List<String> getTitles() {
        List<String> titles = new ArrayList<>();
        titles.add("GreenDao在登录成功后，的一个使用实例");
        titles.add("Bar Charts 单柱状图");
        titles.add("Bar Charts 双柱状图");
        titles.add("Pie Charts 饼状图");
        return titles;
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main_recycler_view,
                    parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.mTitleTextView.setText(mTitles.get(position));
            holder.itemView.setOnClickListener(v -> {
                doClick(position);
            });
        }

        private void doClick(int position) {
            switch (position) {
                case 0:
                    LoginActivity.startActivity(mContext);
                    break;
                case 1:
                    BarChartBasicActivity.startActivity(mContext);
                    break;
                case 2:
                    BarChartMultiActivity.startActivity(mContext);
                    break;
                case 3:
                    PieChartsBasicActivity.startActivity(mContext);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mTitles.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView mTitleTextView;

            public Holder(View itemView) {
                super(itemView);
                mTitleTextView = itemView.findViewById(R.id.adapter_title_text);
            }
        }
    }
}