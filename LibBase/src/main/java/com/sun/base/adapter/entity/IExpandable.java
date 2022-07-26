package com.sun.base.adapter.entity;

import java.util.List;

/**
 * @author Harper
 * @date 2022/6/29
 * note:
 */
public interface IExpandable<T> {
    boolean isExpanded();
    void setExpanded(boolean expanded);
    List<T> getSubItems();

    /**
     * Get the level of this item. The level start from 0.
     * If you don't care about the level, just return a negative.
     */
    int getLevel();
}