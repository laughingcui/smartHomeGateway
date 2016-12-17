package com.everyoo.smartgateway.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @ClassName: JsonUtils
 * @Description: TODO Json泛型 方法类
 */
public class JsonUtils {
    private static Gson gson = new Gson();

    public static <T> T object(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    public static <T> T object(String json, Type typeOfT) {
        return  gson.fromJson(json, typeOfT);
    }
    public static <T> String toJson(Class<T> param) {
        return gson.toJson(param);
    }
}
