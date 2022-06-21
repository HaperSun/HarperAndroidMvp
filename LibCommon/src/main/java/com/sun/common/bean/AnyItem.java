package com.sun.common.bean;

/**
 * @author: Harper
 * @date: 2022/4/29
 * @note: recyclerView任意条目实体类
 */
public class AnyItem {

    public static final int TYPE1 = 0;
    public static final int TYPE2 = 1;
    public static final int TYPE3 = 2;
    public static final int TYPE4 = 3;
    public static final int TYPE5 = 4;
    public static final int TYPE6 = 5;
    public static final int TYPE7 = 6;
    public static final int TYPE8 = 7;
    public static final int TYPE9 = 8;
    public static final int TYPE10 = 9;
    public static final int TYPE11 = 10;
    public static final int TYPE12 = 11;
    public int type;
    public Object object;
    public int index;

    public AnyItem(int type, Object object) {
        this.type = type;
        this.object = object;
    }
}
