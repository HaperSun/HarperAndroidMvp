package com.sun.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.base.activity.BaseMvpActivity;
import com.sun.img.preview.ImagePreviewActivity;
import com.sun.demo2.R;
import com.sun.demo2.databinding.ActivityRecyclerViewImageBinding;
import com.sun.demo2.model.ImgItemBean;
import com.sun.img.img.ImgLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2021/12/13
 * @note: 测试在列表中的图片加载
 */
public class RecyclerViewImageActivity extends BaseMvpActivity {

    private Context mContext;
    private List<ImgItemBean> mItemBeans;
    private ActivityRecyclerViewImageBinding mBind;

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
        mBind = (ActivityRecyclerViewImageBinding) mViewDataBinding;
    }

    @Override
    public void initData() {
        mContext = RecyclerViewImageActivity.this;
        mItemBeans = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mItemBeans.add(new ImgItemBean("https://qiniu.fxgkpt.com/hycg/1639356784663.jpg",
                    "https://pic.ntimg.cn/file/20180211/7259105_125622777789_2.jpg"));
        }
        Adapter adapter = new Adapter();
        mBind.recyclerView.setAdapter(adapter);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_view_img, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewImageActivity.Adapter.Holder holder, int position) {
            ImgItemBean bean = mItemBeans.get(position);
            ImgLoader.getInstance().loadImage(bean.getImg1(), holder.mIv1);
            ImgLoader.getInstance().loadImage(bean.getImg2(), holder.mIv2);
            holder.itemView.setOnClickListener(v -> ImagePreviewActivity.actionStart(mContext, bean.getImg1(), bean.getImg2()));
        }

        @Override
        public int getItemCount() {
            return mItemBeans.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView mIv1;
            ImageView mIv2;

            public Holder(@NonNull View itemView) {
                super(itemView);
                mIv1 = itemView.findViewById(R.id.img_1);
                mIv2 = itemView.findViewById(R.id.img_2);
            }
        }
    }
}