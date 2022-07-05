package com.sun.demo2.model;

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
public class AddressBook1Bean implements Serializable, Comparable<AddressBook1Bean> {
    //名字
    private String name;
    //内容
    private String content;
    //日期
    private String time;
    //头像
    private int headerid;


    //中文转换为拼音
    private String pinyin;
    //首字母
    private String start;

    public AddressBook1Bean(String name, int headerid) {
        this.headerid = headerid;
        this.name = name;
        pinyin = PinYinUtil.getInstance().getPinYin(name);
        start = pinyin.substring(0, 1).toUpperCase();
        if (!start.matches("[A-Z]")) {
            start = "#";
        }
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getStart() {
        return start;
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

    @Override
    public int compareTo(AddressBook1Bean addressBook1Bean) {
        if (start.equals("#") && !addressBook1Bean.getStart().equals("#")) {
            return 1;
        } else if (!start.equals("#") && addressBook1Bean.getStart().equals("#")) {
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(addressBook1Bean.getPinyin());
        }
    }

    public static List<AddressBook1Bean> getData(){
        List<AddressBook1Bean> beans = new ArrayList<>();
        beans.add(new AddressBook1Bean("丽丽", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("妈妈", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("爸爸", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("#xx", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("虹猫", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("蓝兔", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("阿牛", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("CK", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("灯虎", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("尔康", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("凡哥", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("Gr", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("123阿斯顿", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("Ri本", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("ii啥", R.mipmap.ic_avatar));
        beans.add(new AddressBook1Bean("杰哥", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("凯奇", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("楠楠", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("哦哦", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("皮皮", R.mipmap.ic_app_logo));
        beans.add(new AddressBook1Bean("钱德勒", R.mipmap.ic_app_logo));
        Collections.sort(beans);
        return beans;
    }
}
