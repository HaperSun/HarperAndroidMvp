package com.sun.base.util;


import com.sun.base.bean.MediaFile;

import java.util.ArrayList;

/**
 * @author: Harper
 * @date: 2022/7/19
 * @note: 数据保存类
 * （随着Android版本的提高，对Intent传递数据的大小也做了不同的限制，在一些高版本或者低配机型上可能会发生
 * android.os.TransactionTooLargeException: data parcel size xxxx bytes异常，故用此方案适配）
 */
public class DataUtil {

    private static volatile DataUtil mDataUtilInstance;
    private ArrayList<MediaFile> mData = new ArrayList<>();

    private DataUtil() {
    }

    public static DataUtil getInstance() {
        if (mDataUtilInstance == null) {
            synchronized (DataUtil.class) {
                if (mDataUtilInstance == null) {
                    mDataUtilInstance = new DataUtil();
                }
            }
        }
        return mDataUtilInstance;
    }

    public ArrayList<MediaFile> getMediaData() {
        return mData;
    }

    public void setMediaData(ArrayList<MediaFile> data) {
        this.mData = data;
    }


}
