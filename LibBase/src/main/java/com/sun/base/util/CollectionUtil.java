package com.sun.base.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 关于集合操作的工具类
 */
public class CollectionUtil {

    private CollectionUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取数组的大小
     *
     * @param array 数组
     * @return 数组大小
     */
    public static <T> int size(T[] array) {
        return array == null ? 0 : array.length;
    }

    /**
     * 获取集合的大小
     *
     * @param c 集合
     * @return 集合大小
     */
    public static <T> int size(Collection<T> c) {
        return c == null ? 0 : c.size();
    }

    /**
     * 判断数组是否为空或者null
     *
     * @param array 数组
     * @return boolean
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 数组
     * @return boolean
     */
    public static <T> boolean notEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 判断集合是否为空或者null
     *
     * @param c 集合
     * @return boolean
     */
    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param c 集合
     * @return boolean
     */
    public static <T> boolean notEmpty(Collection<T> c) {
        return !isEmpty(c);
    }

    /**
     * 判断map是否为空或者null
     *
     * @param map map
     * @return boolean
     */
    public static <S, T> boolean isEmpty(Map<S, T> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断map是否不为空
     *
     * @param map map
     * @return boolean
     */
    public static <S, T> boolean notEmpty(Map<S, T> map) {
        return !isEmpty(map);
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）null和空数组认为是不相等的
     * 请注意请务必实现集合元素的equals方法(集合里面元素顺序也要一样)
     *
     * @param a 集合
     * @param b 集合
     * @return boolean
     */
    public static <T> boolean isListEqual(Collection<T> a, Collection<T> b) {
        if (null == a) {
            return null == b;
        } else if (null == b) {
            return false;
        }
        if (a.isEmpty()) {
            return b.isEmpty();
        }
        int lSize = a.size();
        int rSize = b.size();
        if (lSize != rSize) {
            return false;
        }
        Iterator<T> l = a.iterator();
        Iterator<T> r = b.iterator();
        while (l.hasNext()) {
            if (!Objects.equals(l.next(), r.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）忽略空集合，即null和空数组认为是相等的
     * 请注意请务必实现集合元素的equals方法(集合里面元素顺序也要一样)
     *
     * @param a 集合
     * @param b 集合
     * @return boolean
     */
    public static <T> boolean isListEqualIgnoreEmpty(Collection<T> a, Collection<T> b) {
        if (isEmpty(a)) {
            return isEmpty(b);
        }
        return isListEqual(a, b);
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）忽略空集合，即null和空数组认为是相等的
     * 请注意请务必实现集合元素的equals方法(集合里面元素顺序可以不一样)
     *
     * @param a 集合
     * @param b 集合
     * @return boolean
     */
    public static <T> boolean isListEqualIgnoreEmptyAndOrder(Collection<T> a, Collection<T> b) {
        return isListEqualIgnoreEmpty(a, b) || isListEqualIgnoreOrder(a, b);
    }

    /**
     * 比较两个集合是否相等（即两个集合里面的元素都一样）请注意请务必实现集合元素的equals方法(集合里面元素顺序可以不一样)
     *
     * @param a 集合
     * @param b 集合
     * @return boolean
     */
    public static <T> boolean isListEqualIgnoreOrder(Collection<T> a, Collection<T> b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a.isEmpty()) {
            return b.isEmpty();
        }
        int lSize = a.size();
        int rSize = b.size();
        if (lSize != rSize) {
            return false;
        }
        for (T t : a) {
            if (!b.contains(t)) {
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
     * @return boolean
     */
    public static <T> boolean contains(T[] array, T data) {
        if (isEmpty(array)) {
            return false;
        }
        for (T t : array) {
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
     * @return boolean
     */
    public static <T> boolean contains(Collection<T> collection, T data) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(data);
    }
}
