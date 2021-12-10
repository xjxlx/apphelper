package com.android.helper.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author : 流星
 * @CreateDate: 2021/12/10-6:03 下午
 * @Description: gson 工具类
 */
public class GsonUtil {

    /**
     * @param json   必须是一个Gson的字符串
     * @param tClass 类型对象
     * @param <T>    指定的类型
     * @return 把一个json转换成一个集合
     */
    public static <T> List<T> convertList(String json, Class<T> tClass) {
        List<T> list = null;
        if (!TextUtils.isEmpty(json)) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<T>>() {
                }.getType();

                list = gson.fromJson(json, type);

            } catch (Exception ignored) {
            }

        }
        return list;
    }

}
