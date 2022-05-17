/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.sun.face.parser;


import com.sun.face.model.FaceError;

/**
 * @author: Harper
 * @date: 2022/5/17
 * @note: JSON解析
 */
public interface Parser<T> {
    T parse(String json) throws FaceError;
}
