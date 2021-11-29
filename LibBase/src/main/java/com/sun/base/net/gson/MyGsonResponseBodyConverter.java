package com.sun.base.net.gson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.base.net.response.BaseResponse;
import com.sun.base.net.response.EncryptedResponse;
import com.sun.base.net.exception.ServerException;


import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note:
 */
class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    public static final String TAG = "GsonResponseBodyConverter";

    private final Gson gson;
    private final Type type;

    MyGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            //ResultResponse 只解析result字段
            BaseResponse resultResponse = gson.fromJson(response, BaseResponse.class);
            if (type instanceof Class && ((Class) type).getName().equalsIgnoreCase(String.class.getName())) {
                return (T) response;
            }
            if (resultResponse.isOK()) {
                //  code == 1 表示成功返回，继续用本来的Model类解析
                return gson.fromJson(decryptEncryptedResponse(response), type);
            } else {
                // TODO: 2017/3/28  ErrResponse 将msg解析为异常消息文本
                if (!TextUtils.isEmpty(response)) {
                    if (type instanceof Class && ((Class) type).getName().equalsIgnoreCase(String.class.getName())) {
                        return (T) response;
                    }
                    return gson.fromJson(response,type);
                } else {
                    throw new ServerException(resultResponse.code, resultResponse.msg, response);
                }
            }
        } finally {

        }
    }

    /**
     * 对加密数据进行解析
     *
     * @return
     */
    private String decryptEncryptedResponse(String response) {
        if (type instanceof Class && EncryptedResponse.class.isAssignableFrom(((Class) type))) {
            // 是加密的返回需要解密
            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
            JsonElement data = jsonObject.get("data");
            if (data != null) {
                String aesStr = data.getAsString();
//                String originStr = AESUtils.decrypt(ResponseDataCryptConfig.aesKey(), ResponseDataCryptConfig.aesIvParameter(), aesStr);
//                if (!TextUtils.isEmpty(originStr)) {
//                    jsonObject.add("data", new JsonParser().parse(originStr));
//                    return jsonObject.toString();
//                }
            }
        }
        return response;
    }
}