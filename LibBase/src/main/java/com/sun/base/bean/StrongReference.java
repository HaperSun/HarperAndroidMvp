package com.sun.base.bean;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
public class StrongReference<T> {

    private T view;

    public StrongReference(T view) {
        this.view = view;
    }

    public T get() {
        return view;
    }

    public void clear() {
        view = null;
    }
}
