package com.sun.base.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author: Administrator
 * @date: 2020/7/21
 * @description:
 */
public class GetRequestUtil {
    /**
     * 请求 get
     * 参数 String url
     */
    public static String getRqstUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        return builder.toString();
    }

    /**
     * 请求 post
     * 参数 JsonObject
     */
    public static JsonObject getRequestParams(Map<String, String> params) {
        JsonObject object = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (key != null && params.get(key) != null) {
                try {
                    object.add(key, jsonParser.parse(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtil.d("object_params", object == null ? "" : object.toString());
        return object;
    }

    public static RequestBody getRequestBodyParams(Map<String, String> params) {
        JsonObject object = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (key != null && params.get(key) != null) {
                try {
                    object.add(key, jsonParser.parse(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtil.d("object_params", object == null ? "" : object.toString());
        return RequestBody.create(MediaType.parse("application/json"), object.toString());
    }

    public static RequestBody getRequestBody(JSONObject object) {
        return RequestBody.create(MediaType.parse("application/json"), object.toString());
    }

    public static RequestBody getRequestBody(JSONArray array) {
        return RequestBody.create(MediaType.parse("application/json"), array.toString());
    }
}
