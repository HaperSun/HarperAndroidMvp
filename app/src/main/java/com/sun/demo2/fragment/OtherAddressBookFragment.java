package com.sun.demo2.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.base.util.CollectionUtil;
import com.sun.common.bean.AnyItem;
import com.sun.demo2.R;
import com.sun.demo2.databinding.FragmentOtherAddressBookBinding;
import com.sun.demo2.model.AddressBook2Bean;
import com.sun.demo2.view.widget.FastIndexBar;
import com.sun.demo2.view.widget.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/20
 * @note: 仿通讯录
 */
public class OtherAddressBookFragment extends BaseMvpFragment implements FastIndexBar.OnCharSelectedListener {

    private FragmentOtherAddressBookBinding bind;
    private FragmentActivity mActivity;
    private List<AnyItem> mItems;
    private final Handler mHandler = new Handler();

    public static OtherAddressBookFragment getInstance() {
        OtherAddressBookFragment fragment = new OtherAddressBookFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_other_address_book;
    }

    @Override
    public void initView() {
        bind = (FragmentOtherAddressBookBinding) mViewDataBinding;
        mActivity = getActivity();
        bind.indexBar.setOnCharSelectedListener(this);
    }

    @Override
    public void initData() {
        mItems = new ArrayList<>();
        mItems.add(new AnyItem(AnyItem.TYPE1, "#"));
        List<AddressBook2Bean> beans = AddressBook2Bean.getData();
        for (int i = 0; i < beans.size(); i++) {
            AddressBook2Bean bean = beans.get(i);
            if (i == 0) {
                if (bean.firstLetter >= 'A' && bean.firstLetter <= 'Z') {
                    mItems.add(new AnyItem(AnyItem.TYPE1, bean.firstLetter + ""));
                }
            } else {
                if (bean.firstLetter != beans.get(i - 1).firstLetter) {
                    //与上一个user首字母不同,先添加字母
                    mItems.add(new AnyItem(AnyItem.TYPE1, bean.firstLetter + ""));
                }
            }
            mItems.add(new AnyItem(AnyItem.TYPE2, bean));
        }

        //根据实际内容更新右边的索引条
        StringBuilder letters = new StringBuilder();
        for (AnyItem anyItem : mItems) {
            if (AnyItem.TYPE1 == anyItem.type) {
                letters.append((String) anyItem.object);
            }
        }
        if (!TextUtils.isEmpty(letters)) {
            bind.indexBar.setLettersData(letters.toString());
        }
        Adapter adapter = new Adapter();
        bind.listView.setAdapter(adapter);
    }

    @Override
    public void onCharSelected(char c) {
        bind.tvBig.setText(c + "");
        bind.tvBig.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 400);
        for (int i = 0; i < mItems.size(); i++) {
            if (AnyItem.TYPE1 == mItems.get(i).type && c == ((String) mItems.get(i).object).charAt(0)) {
                bind.listView.smoothScrollToPositionFromTop(i, 0, 300);
                break;
            }
        }
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mActivity.runOnUiThread(() -> bind.tvBig.setVisibility(View.GONE));
        }
    };

    class Adapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == AnyItem.TYPE1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (getItem(position)).type;
        }

        @Override
        public int getCount() {
            return CollectionUtil.size(mItems);
        }

        @Override
        public AnyItem getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            AnyItem item = getItem(i);
            switch (type) {
                //置顶条
                case AnyItem.TYPE1:
                    SectionHolder sectionHolder;
                    if (convertView == null) {
                        convertView = View.inflate(viewGroup.getContext(), R.layout.item_user_section, null);
                        sectionHolder = new SectionHolder(convertView);
                        convertView.setTag(sectionHolder);
                    } else {
                        sectionHolder = (SectionHolder) convertView.getTag();
                    }
                    String str = (String) item.object;
                    sectionHolder.tvSection.setText(str);
                    break;
                case AnyItem.TYPE2: {
                    //姓名
                    UserHolder userHolder;
                    if (convertView == null) {
                        convertView = View.inflate(viewGroup.getContext(), R.layout.item_user_mult, null);
                        userHolder = new UserHolder(convertView);
                        convertView.setTag(userHolder);
                    } else {
                        userHolder = (UserHolder) convertView.getTag();
                    }
                    AddressBook2Bean bean = (AddressBook2Bean) item.object;
                    String name = bean.getName();
                    userHolder.tvName.setText(name);
                    userHolder.tvName.setOnClickListener(v -> showToast(name));
                }
                break;
            }
            return convertView;
        }
    }

    private static class SectionHolder {
        //字符
        TextView tvSection;

        SectionHolder(View view) {
            tvSection = view.findViewById(R.id.tv_section);
        }
    }

    private static class UserHolder {
        //名字
        TextView tvName;

        UserHolder(View view) {
            tvName = view.findViewById(R.id.tv_name);
        }
    }

}
