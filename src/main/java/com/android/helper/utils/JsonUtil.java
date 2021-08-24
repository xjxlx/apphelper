package com.android.helper.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.luck.picture.lib.tools.SPUtils;

import java.util.HashMap;
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

}
