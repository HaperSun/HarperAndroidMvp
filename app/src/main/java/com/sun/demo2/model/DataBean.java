package com.sun.demo2.model;

/**
 * @author Harper
 * @date 2021/12/9
 * note:
 */
public class DataBean {

    private String name;
    private int data;

    public DataBean() {
    }

    public DataBean(String name, int data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public int getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(int data) {
        this.data = data;
    }
}
