package com.sun.demo2.fragment;

import android.os.Bundle;

import com.sun.base.base.fragment.BaseMvpFragment;
import com.sun.demo2.R;
import com.sun.demo2.adapter.AddressBookAdapter;
import com.sun.demo2.databinding.FragmentWechatAddressBookBinding;
import com.sun.demo2.model.AddressBook1Bean;

import java.util.List;

/**
 * @author Harper
 * @date 2022/6/16
 * note: 仿微信通讯录
 */
public class WechatAddressBookFragment extends BaseMvpFragment {

    private FragmentWechatAddressBookBinding bind;
    private List<AddressBook1Bean> mBeans;

    public static WechatAddressBookFragment getInstance() {
        WechatAddressBookFragment fragment = new WechatAddressBookFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int layoutId() {
        return R.layout.fragment_wechat_address_book;
    }

    @Override
    public void initView() {
        bind = (FragmentWechatAddressBookBinding) mViewDataBinding;
        bind.sideBar.setOnStrSelectCallBack((index, selectStr) -> {
            for (int i = 0; i < mBeans.size(); i++) {
                if (selectStr.equalsIgnoreCase(mBeans.get(i).getStart())){
                    bind.recyclerView.scrollToPosition(i);
                    return;
                }
            }
        });
    }

    @Override
    public void initData() {
        mBeans = AddressBook1Bean.getData();
        AddressBookAdapter adapter = new AddressBookAdapter();
        adapter.setAdapterData(mBeans);
        bind.recyclerView.setAdapter(adapter);
    }
}
