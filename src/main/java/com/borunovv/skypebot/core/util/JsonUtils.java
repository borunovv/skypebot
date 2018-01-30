package com.borunovv.skypebot.core.util;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;

import java.util.List;
import java.util.Map;

/**
 * @author borunovv
 */
public class JsonUtils {

    public static String toJson(Object obj) {
        return JSONValue.toJSONString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        //Map<String, Object> a = (JSONObject) JSONValue.parse(json,clazz);
        if (clazz.equals(Map.class)) {
            return (T) JSONValue.parse(json);
        } else if (clazz.equals(List.class)) {
            return (T) (JSONArray) JSONValue.parse(json);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + clazz.getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> fromJsonToMap(String json) {
        return (Map<String, Object>)fromJson(json, Map.class);
    }
}
