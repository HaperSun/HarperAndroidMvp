package com.sun.demo2.model;

import android.text.TextUtils;

import com.sun.demo2.R;
import com.sun.demo2.util.PinYinUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/16
 * @note: 通讯录实体类
 */
public class AddressBook2Bean implements Serializable, Comparable<AddressBook2Bean> {
    //名字
    private String name;
    //内容
    private String content;
    //日期
    private String time;
    //头像
    private int headerid;

    public String pyName;
    public char firstLetter;

    public AddressBook2Bean(String name, int headerid) {
        this.headerid = headerid;
        this.name = name;
        name = name.replace(" ", "");
        if (!TextUtils.isEmpty(name)) {
            String startName = name.substring(0, 1).toUpperCase();
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(startName)) {
                pyName = name.toUpperCase();
                firstLetter = startName.charAt(0);
            } else {
                pyName = PinYinUtil.getInstance().covertHanziToPinyinUpCase(name);
                if (TextUtils.isEmpty(pyName)) {
                    pyName = "#";
                    firstLetter = '#';
                } else {
                    char first = pyName.charAt(0);
                    if (first >= 'A' && first <= 'Z') {
                        firstLetter = first;
                    } else {
                        firstLetter = '#';
                    }
                }
            }
        } else {
            pyName = "#";
            firstLetter = '#';
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHeaderid() {
        return headerid;
    }

    public void setHeaderid(int headerid) {
        this.headerid = headerid;
    }

    public String getPyName() {
        return pyName;
    }

    public void setPyName(String pyName) {
        this.pyName = pyName;
    }

    public char getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(char firstLetter) {
        this.firstLetter = firstLetter;
    }

    @Override
    public int compareTo(AddressBook2Bean bean) {
        try {
            //过滤#的情况
            if ("#".equals(pyName) || firstLetter == '#') {
                return -1;
            } else {
                //比较其他少于字母
                if (firstLetter == bean.firstLetter) {
                    return pyName.compareTo(bean.pyName);
                } else {// 再比拼音
                    return firstLetter - bean.firstLetter;
                }
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static List<AddressBook2Bean> getData(){
        List<AddressBook2Bean> beans = new ArrayList<>();
        beans.add(new AddressBook2Bean("丽丽", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("妈妈", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("爸爸", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("#xx", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("虹猫", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("蓝兔", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("阿牛", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("CK", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("灯虎", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("尔康", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("凡哥", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("Gr", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("123阿斯顿", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("Ri本", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("ii啥", R.mipmap.avatar));
        beans.add(new AddressBook2Bean("杰哥", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("凯奇", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("楠楠", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("哦哦", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("皮皮", R.mipmap.app_logo));
        beans.add(new AddressBook2Bean("钱德勒", R.mipmap.app_logo));
        Collections.sort(beans);
        return beans;
    }
}
