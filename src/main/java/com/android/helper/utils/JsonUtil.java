package com.android.helper.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.luck.picture.lib.tools.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String mapToJson(Map<String, Object> map) {
        if (map != null && map.size() > 0) {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            LogUtil.e("mapToJson  --->: " + json);
            return json;
        }
        return "";
    }

    public static Map<String, Object> jsonToMap(String json) {
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.<Map<String, Object>>fromJson(json, Map.class);
            LogUtil.e("mapToJson  --->: " + map);
            return map;
        }
        return null;
    }

    /**
     * 把一对数句按照map的形式存入到一个指定的key的sp中，类似于本地数据库的操作,可以无线存储
     *
     * @param spKey sp的key
     * @param key   map的key
     * @param value map的value
     */
    public static void spMapToJson(String spKey, String[] key, Object[] value) {
        Map<String, Object> map = null;
        if (key == null || (value == null) || (key.length != value.length)) {
            return;
        }
        String json = SPUtils.getInstance().getString(spKey);
        if (TextUtils.isEmpty(json)) {
            map = new HashMap<>();
        } else {
            map = jsonToMap(json);
        }

        for (int i = 0; i < value.length; i++) {
            String keyObject = key[i];
            Object valueObject = value[i];
            if (map != null) {
                map.put(keyObject, valueObject);
            }
        }
        if (map != null) {
            LogUtil.e("无线存储的map：" + map);
            SPUtils.getInstance().put(spKey, new Gson().toJson(map));
        }
    }

    /**
     * @param spKey sp的key
     * @param key   map中的key
     * @param <T>   具体的返回值
     * @return 取出无线存储sp中的值 ,返回的数据可能为空
     */
    public static <T> T spJsonMapToValue(String spKey, String key) {
        try {
            if (!TextUtils.isEmpty(key)) {
                String json = SPUtils.getInstance().getString(spKey);
                if (!TextUtils.isEmpty(json)) {
                    Map<String, Object> map = jsonToMap(json);
                    if (map != null) {
                        return (T) map.get(key);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("数据取出失败" + e.getMessage());
        }
        return null;
    }

    /**
     * 清空存入的sp数据
     *
     * @param spKey sp的key
     */
    public static void spClearMap(String spKey) {
        if (!TextUtils.isEmpty(spKey)) {
            SPUtils.getInstance().put(spKey, "");
            LogUtil.e("清空了sp中存储的key对应的值");
        }
    }

    /**
     * @param json 必须是一个Gson的字符串
     * @param cls  类型对象
     * @param <T>  指定的类型
     * @return 把一个json转换成一个集合
     */
    public static <T> List<T> convertList(String json, Class<T> cls) {
        List<T> list = null;
        if (!TextUtils.isEmpty(json)) {
            try {
                Gson gson = new Gson();
                list = new ArrayList<>();
                JsonElement jsonElement = JsonParser.parseString(json);
                if (jsonElement != null) {
                    // 如果对象不为空，说明是一个正常的json
                    boolean jsonArray = jsonElement.isJsonArray();
                    if (jsonArray) {
                        JsonArray asJsonArray = jsonElement.getAsJsonArray();
                        for (JsonElement element : asJsonArray) {
                            T t = gson.fromJson(element, cls);
                            list.add(t);
                        }
                    }
                } else {
                    // 说明不是一个正常的JSON，可以尝试转换为一个正常的JSON,再次去尝试一下
                    String jsonValue = gson.toJson(json);
                    JsonElement jsonElementValue = JsonParser.parseString(jsonValue);
                    if (jsonElementValue != null) {
                        boolean jsonArray = jsonElementValue.isJsonArray();
                        if (jsonArray) {
                            JsonArray asJsonArray = jsonElementValue.getAsJsonArray();
                            for (JsonElement element : asJsonArray) {
                                T t = gson.fromJson(element, cls);
                                list.add(t);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return list;
    }

}
