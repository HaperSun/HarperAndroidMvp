package com.sun.demo2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Harper
 * @date 2022/3/26
 * note:
 */
public class MyViewModel extends ViewModel {

    public int number;

    private MutableLiveData<Integer> mSecond;

    public MutableLiveData<Integer> getSecond(){
        if (mSecond == null){
            mSecond = new MutableLiveData<>();
            mSecond.setValue(0);
        }
        return mSecond;
    }
}
