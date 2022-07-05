package com.sun.demo2.view.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.base.util.CommonUtil;
import com.sun.demo2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harper
 * @date 2022/5/30
 * note:
 */
public class PeoplePopupWindow extends PopupWindow {

    private List<SelectBean> mBeans;
    private PopupWindowListener mPopupWindowListener;

    public PeoplePopupWindow(Context context) {
        super(context);
        initPopupWindow(context);
    }

    private void initPopupWindow(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_window_people, null);
        setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        setWidth((int)context.getResources().getDimension(R.dimen.dp131));
        // 设置SelectPicPopupWindow弹出窗体的高
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体不可点击
        setFocusable(false);
        setOutsideTouchable(false);
        // 刷新状态
        update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        setBackgroundDrawable(dw);
        initPopupWindowData(view);
    }

    private void initPopupWindowData(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rv_select);
        mBeans = getData();
        Adapter adapter = new Adapter();
        recyclerView.setAdapter(adapter);
    }

    private List<SelectBean> getData() {
        List<SelectBean> beans = new ArrayList<>();
        beans.add(new SelectBean("车辆", true));
        beans.add(new SelectBean("人员", true));
        beans.add(new SelectBean("摄像头", false));
        beans.add(new SelectBean("四色图", false));
        beans.add(new SelectBean("风险点", false));
        beans.add(new SelectBean("重大危险源", false));
        return beans;
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_people_select, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            SelectBean bean = mBeans.get(position);
            holder.tvName.setText(bean.getName());
            boolean select = bean.isSelected();
            CommonUtil.setViewBackground(holder.ivSelect, select ? R.mipmap.ic_rec_blue_selected : R.mipmap.ic_rec_grey_unselect);
            holder.itemView.setOnClickListener(v -> {
                if (mPopupWindowListener != null) {
                    doItemClick(position);
                }
            });
        }

        private void doItemClick(int position) {
            SelectBean bean = mBeans.get(position);
            bean.setSelected(!bean.isSelected());
            notifyItemChanged(position);
        }

        @Override
        public int getItemCount() {
            return mBeans.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView tvName;
            ImageView ivSelect;

            public Holder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                ivSelect = itemView.findViewById(R.id.iv_select);
            }
        }
    }

    public void setPopupWindowListener(PopupWindowListener popupWindowListener) {
        mPopupWindowListener = popupWindowListener;
    }

    public interface PopupWindowListener {
        void onItemClick();
    }

    public static class SelectBean {
        private String name;
        private boolean selected;

        public SelectBean(String name, boolean selected) {
            this.name = name;
            this.selected = selected;
        }

        public String getName() {
            return name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

}
