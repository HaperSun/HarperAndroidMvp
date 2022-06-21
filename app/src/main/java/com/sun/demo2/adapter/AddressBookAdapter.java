package com.sun.demo2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.demo2.R;
import com.sun.demo2.model.AddressBook1Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/16
 * @note:
 */
public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.Holder> {

    private List<AddressBook1Bean> mBeans = new ArrayList<>();

    public void setAdapterData(List<AddressBook1Bean> beans) {
        this.mBeans = beans;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_book, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        final AddressBook1Bean addressBook1Bean = mBeans.get(position);
        holder.tvName.setText(addressBook1Bean.getName());
        holder.tvHeader.setImageResource(addressBook1Bean.getHeaderid());
        String mark = addressBook1Bean.getStart();
        if (position == getPosition(mark)) {
            holder.llLetter.setVisibility(View.VISIBLE);
            holder.tvLetter.setText(addressBook1Bean.getStart());
        } else {
            holder.llLetter.setVisibility(View.GONE);
        }
        if (position != getItemCount() - 1 && addressBook1Bean.getStart().equalsIgnoreCase(mBeans.get(position + 1).getStart())) {
            holder.viewSplit.setVisibility(View.VISIBLE);
        } else {
            holder.viewSplit.setVisibility(View.GONE);
        }
    }

    private int getPosition(String mark) {
        for (int i = 0; i < getItemCount(); i++) {
            if (mBeans.get(i).getStart().equalsIgnoreCase(mark)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        LinearLayout llLetter;
        TextView tvLetter;
        TextView tvName;
        ImageView tvHeader;
        View view;
        View viewSplit;

        public Holder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            viewSplit = itemView.findViewById(R.id.view_split);
            llLetter = itemView.findViewById(R.id.ll_letter);
            tvLetter = itemView.findViewById(R.id.tv_letter);
            tvName = itemView.findViewById(R.id.tv_name);
            tvHeader = itemView.findViewById(R.id.iv_header);
        }
    }
}