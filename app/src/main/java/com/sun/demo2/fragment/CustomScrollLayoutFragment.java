package com.sun.demo2.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.base.widget.ScrollLayout;
import com.sun.demo2.R;
import com.sun.demo2.databinding.FragmentViewPagerBinding;

import java.util.ArrayList;

/**
 * @author Harper
 * @date 2022/5/31
 * note:
 */
public class CustomScrollLayoutFragment extends BaseMvpFragment<FragmentViewPagerBinding> {

    private ArrayList<String> mList;

    public static CustomScrollLayoutFragment getInstance() {
        CustomScrollLayoutFragment fragment = new CustomScrollLayoutFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_view_pager;
    }

    @Override
    public void initView() {
        bind.title.setBackgroundColor(Color.argb(0, 63, 81, 181));
        bind.scrollLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mList.add("我是中国人：" + i);
        }
        Adapter adapter = new Adapter();
        bind.scrollRecyclerView.setAdapter(adapter);
        initClick();
    }

    private void initClick() {
        bind.scrollLayout.setOnScrollChangedListener(new ScrollLayout.OnScrollChangedListener() {
            @Override
            public void onScrollChange(int status) {
                bind.scrollTextView.setVisibility(status == ScrollLayout.STATUS_CLOSE ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onScrollProgress(int progress) {
                if (progress > 0) {
                    bind.title.setVisibility(View.VISIBLE);
                } else {
                    bind.title.setVisibility(View.INVISIBLE);
                }
                bind.title.setBackgroundColor(Color.argb(progress, 63, 81, 181));
                bind.scrollLayout.setBackgroundColor(Color.argb(progress, 0, 0, 0));
            }
        });

        bind.scrollTextView.setOnTextViewListener(v -> bind.scrollLayout.toggle(ScrollLayout.STATUS_DEFAULT));

        bind.news.setOnClickListener(v -> showToast("新闻"));
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_view, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            String name = mList.get(position);
            holder.mTvName.setText(name);
            holder.mTvName.setOnClickListener(v -> showToast(name));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView mTvName;

            public Holder(@NonNull View itemView) {
                super(itemView);
                mTvName = itemView.findViewById(R.id.tv);
            }
        }
    }
}
