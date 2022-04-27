/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.sun.face.util;


import com.sun.face.exception.FaceError;

public interface OnResultListener<T> {
    void onResult(T result);

    void onError(FaceError error);
}
