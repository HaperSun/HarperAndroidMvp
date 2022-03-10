package com.sun.demo2.model;

/**
 * @author Harper
 * @date 2022/2/23
 * note:反射
 * java.lang.Class
 * java.lang.reflect.Method
 * java.lang.reflect.Constructor
 * java.lang.reflect.Field
 */
public class TestReflect {

    private void test(){
        try {
            Class a = Class.forName("java.lang.String");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class b = String.class;
        Class c = "java.lang.String".getClass();
    }
}
