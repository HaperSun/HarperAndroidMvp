/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.sun.face.manager;


import com.sun.face.model.FaceError;

public interface OnResultListener<T> {
    void onResult(T result);

    void onError(FaceError error);
}
