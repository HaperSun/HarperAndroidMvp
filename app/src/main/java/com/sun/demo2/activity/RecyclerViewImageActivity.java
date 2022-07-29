package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.base.util.CollectionUtil;
import com.sun.base.bean.AnyItem;
import com.sun.base.widget.EmptyHolder;
import com.sun.base.widget.FooterHolder;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityRecyclerViewImageBinding;
import com.sun.media.img.ImageLoader;
import com.sun.media.img.ui.activity.ImagePreviewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 测试在列表中的图片加载
 */
public class RecyclerViewImageActivity extends BaseMvpActivity<ActivityRecyclerViewImageBinding> {

    private Context mContext;
    private List<AnyItem> mItems;
    private int mPage = 1;
    private final int mPageSize = 10;
    private Adapter mAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, RecyclerViewImageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_recycler_view_image;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mContext = this;
        bind.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mItems = new ArrayList<>();
        mAdapter = new Adapter();
        bind.recyclerView.setAdapter(mAdapter);
        bind.smartRefreshLayout.autoRefresh(200, 100, 1, false);
        bind.smartRefreshLayout.setOnRefreshListener(layout -> refreshData());
        bind.smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> getData());
    }

    private void refreshData() {
        mPage = 1;
        getData();
    }

    private void getData() {
        List<String> itemBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            itemBeans.add("https://qiniu.fxgkpt.com/hycg/1639356784663.jpg");
        }
        getDataSuccess(itemBeans);
    }

    private void getDataSuccess(List<String> beans) {
        //设置分页列表数据
        if (mPage == 1) {
            bind.smartRefreshLayout.finishRefresh(200);
        } else {
            bind.smartRefreshLayout.finishLoadMore();
        }
        bind.smartRefreshLayout.setEnableLoadMore(beans != null && beans.size() == mPageSize);
        if (mPage == 1) {
            mItems.clear();
        }
        if (CollectionUtil.notEmpty(beans)) {
            for (String bean : beans) {
                mItems.add(new AnyItem(AnyItem.TYPE1, bean));
            }
            if (beans.size() < mPageSize) {
                mItems.add(new AnyItem(AnyItem.TYPE2, new Object()));
            }
        } else {
            if (mItems.size() > 0) {
                mItems.add(new AnyItem(AnyItem.TYPE2, new Object()));
            }
        }
        if (mItems.size() == 0) {
            mItems.add(new AnyItem(AnyItem.TYPE3, new Object()));
        }
        mAdapter.notifyDataSetChanged();
        mPage++;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            switch (viewType) {
                case AnyItem.TYPE1:
                    return new Holder(LayoutInflater.from(context).inflate(R.layout.adapter_recycler_view_img, parent, false));
                case AnyItem.TYPE2:
                    return new FooterHolder(LayoutInflater.from(context).inflate(R.layout.item_footer_layout, parent, false));
                default:
                    return new EmptyHolder(LayoutInflater.from(context).inflate(R.layout.item_empty_layout, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof Holder){
                Holder holder = (Holder) viewHolder;
                String bean = (String) mItems.get(position).object;
                if (bean != null){
                    ImageLoader.getInstance().loadImage(bean, holder.mIv1);
                    holder.itemView.setOnClickListener(v -> ImagePreviewActivity.start(mContext, bean));
                }
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).type;
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView mIv1;

            public Holder(@NonNull View itemView) {
                super(itemView);
                mIv1 = itemView.findViewById(R.id.img_1);
            }
        }
    }
}