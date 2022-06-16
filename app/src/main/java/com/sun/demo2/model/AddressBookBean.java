package com.sun.demo2.model;

import com.sun.demo2.R;
import com.sun.demo2.util.Cn2Spell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Harper
 * @date: 2022/6/16
 * @note: 通讯录实体类
 */
public class AddressBookBean implements Serializable, Comparable<AddressBookBean> {
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

    public AddressBookBean() {
    }

    public AddressBookBean(String name, String content, String time, int headerid) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.headerid = headerid;
    }

    public AddressBookBean(String name, int headerid) {
        this.headerid = headerid;
        this.name = name;
        pinyin = Cn2Spell.getPinYin(name);
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
    public int compareTo(AddressBookBean addressBookBean) {
        if (start.equals("#") && !addressBookBean.getStart().equals("#")) {
            return 1;
        } else if (!start.equals("#") && addressBookBean.getStart().equals("#")) {
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(addressBookBean.getPinyin());
        }
    }

    public List<AddressBookBean> getData(){
        List<AddressBookBean> beans = new ArrayList<>();
        beans.add(new AddressBookBean("丽丽", R.mipmap.avatar));
        beans.add(new AddressBookBean("妈妈", R.mipmap.app_logo));
        beans.add(new AddressBookBean("爸爸", R.mipmap.avatar));
        beans.add(new AddressBookBean("#xx", R.mipmap.avatar));
        beans.add(new AddressBookBean("虹猫", R.mipmap.avatar));
        beans.add(new AddressBookBean("蓝兔", R.mipmap.avatar));
        beans.add(new AddressBookBean("阿牛", R.mipmap.avatar));
        beans.add(new AddressBookBean("CK", R.mipmap.app_logo));
        beans.add(new AddressBookBean("灯虎", R.mipmap.avatar));
        beans.add(new AddressBookBean("尔康", R.mipmap.avatar));
        beans.add(new AddressBookBean("凡哥", R.mipmap.app_logo));
        beans.add(new AddressBookBean("Gr", R.mipmap.avatar));
        beans.add(new AddressBookBean("123阿斯顿", R.mipmap.avatar));
        beans.add(new AddressBookBean("Ri本", R.mipmap.avatar));
        beans.add(new AddressBookBean("ii啥", R.mipmap.avatar));
        beans.add(new AddressBookBean("杰哥", R.mipmap.app_logo));
        beans.add(new AddressBookBean("凯奇", R.mipmap.app_logo));
        beans.add(new AddressBookBean("楠楠", R.mipmap.app_logo));
        beans.add(new AddressBookBean("哦哦", R.mipmap.app_logo));
        beans.add(new AddressBookBean("皮皮", R.mipmap.app_logo));
        beans.add(new AddressBookBean("钱德勒", R.mipmap.app_logo));
        Collections.sort(beans);
        return beans;
    }
}
