package com.sun.base.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Harper
 * @date:   2021/11/12
 * @note: 关于集合操作的工具类
 */
public class CollectionUtil {

    private CollectionUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取数组的大小
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> int size(T[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * 获取集合的大小
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> int size(Collection<T> array) {
        return array == null ? 0 : array.size();
    }

    /**
     * 判断数组是否为空或者null
     *
     * @param array
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断集合是否为空或者null
     *
     * @param array
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> array) {
        return array == null || array.isEmpty();
    }

    /**
     * 判断集合是否为空或者null
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * 集合部位null并且部位isEmpty则返回true
     *
     * @param array
     * @return
     */
    public static <T> boolean notEmpty(Collection<T> array){
        return array != null && !array.isEmpty();
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）请注意请务必实现集合元素的equals方法(集合里面元素顺序也要一样)
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public static <T> boolean isListEqual(Collection<T> left, Collection<T> right) {
        if (left == null) {
            return right == null;
        }else if (right == null) {
            return left == null;
        }
        if (left.isEmpty()) {
            return right.isEmpty();
        }
        int leftSize = left.size();
        int rightSize = right.size();
        if (leftSize != rightSize) {
            return false;
        }
        Iterator<T> leftIterator = left.iterator();
        Iterator<T> rightIterator = right.iterator();
        while (leftIterator.hasNext()) {
            if (!Objects.equals(leftIterator.next(), rightIterator.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）请注意请务必实现集合元素的equals方法(集合里面元素顺序也要一样) 忽略空集合，即null和空数组认为是相等的
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public static <T> boolean isListEqualIgnoreEmpty(Collection<T> left, Collection<T> right) {
        if (isEmpty(left)) {
            return isEmpty(right);
        }
        return isListEqual(left, right);
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）请注意请务必实现集合元素的equals方法(集合里面元素顺序可以不一样) 忽略空集合，即null和空数组认为是相等的
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public static <T> boolean isListEqualIgnoreEmptyAndOrder(Collection<T> left, Collection<T> right) {
        return isListEqualIgnoreEmpty(left, right) || isListEqualIgnoreOrder(left, right);
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）请注意请务必实现集合元素的equals方法(集合里面元素顺序可以不一样)
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public static <T> boolean isListEqualIgnoreOrder(Collection<T> left, Collection<T> right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return left == null;
        }
        if (left.isEmpty()) {
            return right.isEmpty();
        }
        int leftSize = left.size();
        int rightSize = right.size();
        if (leftSize != rightSize) {
            return false;
        }
        Iterator<T> leftIterator = left.iterator();
        while (leftIterator.hasNext()) {
            if (!right.contains(leftIterator.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断数组中是否包含某个元素
     *
     * @param array 数组
     * @param data  某个元素
     * @param <T>
     * @return
     */
    public static <T> boolean contains(T[] array, T data) {
        if (isEmpty(array)) {
            return false;
        }
        int length = array.length;
        for (int i = 0; i < length; i++) {
            T t = array[i];
            if (t.equals(data)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断集合中是否包含某个元素
     *
     * @param collection 集合
     * @param data       某个元素
     * @param <T>
     * @return
     */
    public static <T> boolean contains(Collection<T> collection, T data) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(data);
    }
}
