package com.android.helper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.helper.app.BaseApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * <p>文件描述<p>
 * <p>作者：hp<p>
 * <p>创建时间：2019/1/14<p>
 * <p>更改时间：2019/1/14<p>
 */
public class SpUtil {
    /**
     * Sp 存储的文件名
     */
    public static final String SP_FILE_NAME = "userInfo";

    private static volatile SharedPreferences sp;

    /**
     *
     */
    private static synchronized void getSp() {
        if (sp == null) {
            if ((BaseApplication.getInstance() != null) && (BaseApplication.getInstance().getApplication() != null)) {
                sp = BaseApplication.getInstance().getApplication().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
            }
        }
    }

    /**
     * 放入String类型的数据
     *
     * @param key   存入的key
     * @param value 存入的value
     */
    public static void putString(String key, String value) {
        if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(value))) {
            getSp();
            if (sp != null) {
                // 开启编辑器
                SharedPreferences.Editor edit = sp.edit();
                // 存入数据
                if (value == null) {
                    value = "";
                }
                if (edit != null) {
                    edit.putString(key, value);
                    // 提交
                    edit.apply();
                }
            }
        } else {
            LogUtil.e("存入PutString类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取String类型的数据, 如果获取不到, 则返回""
     */
    public static String getString(String key) {
        return getString(key, "");
    }

    /**
     * @param key 获取数据的key
     * @return 获取String类型的数据, 如果获取不到, 则返回默认的defaultValue
     */
    public static String getString(String key, String defaultValue) {
        String value = "";
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                value = sp.getString(key, defaultValue);
            }
        }
        return value;
    }

    /**
     * 放入Int类型的数据
     *
     * @param key   存入的key
     * @param value 存入的value
     */
    public static void putInt(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                // 开启编辑器
                SharedPreferences.Editor edit = sp.edit();
                if (edit != null) {
                    // 存入数据
                    edit.putInt(key, value);
                    // 提交
                    edit.apply();
                }
            }
        } else {
            LogUtil.e("存入PutInt类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取int类型的数据, 如果获取不到, 则返回0
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * @param key          取数据的key
     * @param defaultValue 默认返回值
     * @return 获取int类型的数据, 如果获取不到, 则返回 defaultValue
     */
    public static int getInt(String key, int defaultValue) {
        int value = 0;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                value = sp.getInt(key, defaultValue);
            }
        } else {
            LogUtil.e("取出getInt类型的值为空!");
        }
        return value;
    }

    /**
     * 放入Boolean类型的数据
     *
     * @param key   存入的key
     * @param value 存入的value
     */
    public static void putBoolean(String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                // 开启编辑器
                SharedPreferences.Editor edit = sp.edit();
                if (edit != null) {
                    // 存入数据
                    edit.putBoolean(key, value);
                    // 提交
                    edit.apply();
                }
            }
        } else {
            LogUtil.e("存入PutBoolean类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取Boolean类型的数据, 如果获取不到, 则返回false
     */
    public static boolean getBoolean(String key) {
        boolean value = false;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                value = sp.getBoolean(key, false);
            }
        } else {
            LogUtil.e("取出getBoolean类型的值为空!");
        }
        return getBoolean(key, false);
    }

    /**
     * @param key 获取数据的key
     * @return 获取Boolean类型的数据, 如果获取不到, 则返回指定的defaultValue
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        boolean value = false;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                value = sp.getBoolean(key, defaultValue);
            }
        } else {
            LogUtil.e("取出getBoolean类型的值为空!");
        }
        return value;
    }

    /**
     * 放入long类型的数据
     *
     * @param key   存入的key
     * @param value 存入的value
     */
    public static void putLong(String key, long value) {
        if (key != null) {
            getSp();
            if (sp != null) {
                // 开启编辑器
                SharedPreferences.Editor edit = sp.edit();
                if (edit != null) {
                    // 存入数据
                    edit.putLong(key, value);
                    // 提交
                    edit.apply();
                }
            }
        } else {
            LogUtil.e("存入PutInt类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取long类型的数据, 如果获取不到, 则返回0
     */
    public static long getLong(String key) {
        return getLong(key, 0);
    }

    public static long getLong(String key, long defaultLong) {
        long value = 0;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                value = sp.getLong(key, defaultLong);
            }
        } else {
            LogUtil.e("取出getInt类型的值为空!");
        }
        return value;
    }

    /**
     * 放入float类型的数据
     *
     * @param key   存入的key
     * @param value 存入的value
     */
    public static void putFloat(String key, float value) {
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                // 开启编辑器
                SharedPreferences.Editor edit = sp.edit();
                if (edit != null) {
                    // 存入数据
                    edit.putFloat(key, value);
                    // 提交
                    edit.apply();
                }
            }
        } else {
            LogUtil.e("存入PutFloat类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取float类型的数据, 如果获取不到, 则返回0.0f
     */
    public static float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public static float getFloat(String key, float defaultValue) {
        float value = 0.0f;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                value = sp.getFloat(key, defaultValue);
            }
        } else {
            LogUtil.e("取出getFloat类型的值为空!");
        }
        return value;
    }

    /**
     * 删除指定的Key的Value
     *
     * @param key key
     */
    public static void Remove(String key) {
        if (!TextUtils.isEmpty(key)) {
            getSp();
            if (sp != null) {
                SharedPreferences.Editor edit = sp.edit();
                if (edit != null) {
                    edit.remove(key);
                    edit.apply();
                }
            }
        }
    }

    /**
     * 清空Sp
     */
    public static void clear() {
        getSp();
        if (sp != null) {
            sp.edit().clear().apply();
            LogUtil.e("数据已经清空！");
        }
    }

    /**
     * 用map集合的形式，无限存入数据
     *
     * @param key      存入sp中的key
     * @param mapKey   存入到集合中的key
     * @param mapValue 存入到集合总的value
     */
    public static void putMap(String key, String mapKey, Object mapValue) {
        if ((TextUtils.isEmpty(key)) || (TextUtils.isEmpty(mapKey))) {
            throw new NullPointerException("map数据的Key为空,无法继续存入！");
        }

        try {
            // 校验之前是否有存储过该对象
            HashMap<String, Object> map;
            Gson gson = new Gson();

            // 获取存储的数据
            String spValue = getString(key);
            // 数据不为空
            if (!TextUtils.isEmpty(spValue)) {
                map = gson.fromJson(spValue, new TypeToken<HashMap<String, Object>>() {
                }.getType());

                // 有数据，就重新设置数据
                if (map != null) {
                    map.put(mapKey, mapValue);
                }
            } else {
                // 没有存储过数据，就直接存储
                map = new HashMap<>();
                map.put(mapKey, mapValue);
            }
            // 把存储的数据重新转换成json对象
            String json = gson.toJson(map);

            putString(key, json);
        } catch (JsonSyntaxException exception) {
            LogUtil.e("转换HasMap数据失败！");
        }
    }

    /**
     * @param key    sp对应的key
     * @param mapKey map中对应的key
     * @return 返回sp中一个String类型map，根据map的key，返回一个对应的value
     */
    public static String getStringForMap(String key, String mapKey) {
        String value = "";
        try {
            if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
                // 获取存储的数据
                String spValue = getString(key);
                if (!TextUtils.isEmpty(spValue)) {
                    // 转换为集合
                    Gson gson = new Gson();
                    HashMap<String, String> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, String>>() {
                    }.getType());

                    if ((hashMap != null) && (hashMap.size() > 0)) {
                        String s = hashMap.get(mapKey);
                        if (s != null) {
                            value = s;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换HasMap数据失败！");
        }
        return value;
    }

    /**
     * @param key    sp对应的key
     * @param mapKey map中对应的key
     * @return 返回sp中一个int类型map，根据map的key，返回一个对应的value
     */
    public static int getIntForMap(String key, String mapKey) {
        int value = 0;
        try {
            if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
                // 获取存储的数据
                String spValue = getString(key);
                if (!TextUtils.isEmpty(spValue)) {
                    // 转换为集合
                    Gson gson = new Gson();
                    HashMap<String, Integer> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, Integer>>() {
                    }.getType());

                    if ((hashMap != null) && (hashMap.size() > 0)) {
                        Integer integer = hashMap.get(mapKey);
                        if (integer != null) {
                            value = integer;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换HasMap数据失败！");
        }
        return value;
    }

    /**
     * @param key    sp对应的key
     * @param mapKey map中对应的key
     * @return 返回sp中一个flot类型的map，根据map的key，返回一个对应的value
     */
    public static float getFloatForMap(String key, String mapKey) {
        float value = 0f;
        try {
            if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
                // 获取存储的数据
                String spValue = getString(key);
                if (!TextUtils.isEmpty(spValue)) {
                    // 转换为集合
                    Gson gson = new Gson();
                    HashMap<String, Float> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, Float>>() {
                    }.getType());

                    if ((hashMap != null) && (hashMap.size() > 0)) {
                        Float aFloat = hashMap.get(mapKey);
                        if (aFloat != null) {
                            value = aFloat;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换HasMap数据失败！");
        }
        return value;
    }

    /**
     * @param key    sp对应的key
     * @param mapKey map中对应的key
     * @return 返回sp中一个double类型的map，根据map的key，返回一个对应的value
     */
    public static double getDoubleForMap(String key, String mapKey) {
        double value = 0d;
        try {
            if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
                // 获取存储的数据
                String spValue = getString(key);
                if (!TextUtils.isEmpty(spValue)) {
                    // 转换为集合
                    Gson gson = new Gson();
                    HashMap<String, Double> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, Double>>() {
                    }.getType());

                    if ((hashMap != null) && (hashMap.size() > 0)) {
                        Double aDouble = hashMap.get(mapKey);
                        if (aDouble != null) {
                            value = aDouble;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换HasMap数据失败！");
        }
        return value;
    }

    /**
     * @param key    sp对应的key
     * @param mapKey map中对应的key
     * @return 返回sp中一个T类型的map，根据map的key，返回一个对应的value，此处如果什么数据都获取不到，可能会返回一个null
     */
    public static <T> T getTForMap(String key, String mapKey, Class<T> cls) {
        T t = null;
        try {
            if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
                // 获取存储的数据
                String spValue = getString(key);
                if (!TextUtils.isEmpty(spValue)) {
                    // 转换为集合
                    Gson gson = new Gson();
                    HashMap<String, T> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, T>>() {
                    }.getType());

                    if ((hashMap != null) && (hashMap.size() > 0)) {
                        t = hashMap.get(mapKey);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换HasMap数据失败！");
        }
        return t;
    }

    /**
     * @param key    sp对应的key
     * @param mapKey map中对应的key
     * @return 返回sp中一个T类型的map，根据map的key，返回一个对应的value，此处如果什么数据都获取不到，可能会返回一个null
     */
    public static Object getObjectForMap(String key, String mapKey) {
        Object object = null;
        try {
            if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
                // 获取存储的数据
                String spValue = getString(key);
                if (!TextUtils.isEmpty(spValue)) {
                    // 转换为集合
                    Gson gson = new Gson();
                    HashMap<String, Object> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, Object>>() {
                    }.getType());

                    if ((hashMap != null) && (hashMap.size() > 0)) {
                        object = hashMap.get(mapKey);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换HasMap数据失败！");
        }
        return object;
    }

    /**
     * @param key sp的key
     * @return 根据指定的key返回map中存储的value
     */
    public static HashMap<String, Object> getObjectMap(String key) {
        HashMap<String, Object> hashMap = new HashMap<>();

        if (!TextUtils.isEmpty(key)) {
            // 获取存储的数据
            String spValue = getString(key);
            if (!TextUtils.isEmpty(spValue)) {
                try {
                    Gson gson = new Gson();
                    hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    LogUtil.e("转换HasMap数据失败！");
                }
            }
        }
        return hashMap;
    }

    /**
     * @param key sp的key
     * @return 根据指定的key返回map中存储的value
     */
    public static HashMap<String, String> getMap(String key) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(key)) {
            // 获取存储的数据
            String spValue = getString(key);
            if (!TextUtils.isEmpty(spValue)) {
                try {
                    Gson gson = new Gson();
                    hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, String>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    LogUtil.e("转换HasMap数据失败！");
                }
            }
        }
        return hashMap;
    }

    /**
     * @param key    sp的key
     * @param mapKey map的key
     * @return 移除一个map中的key
     */
    public static boolean removeValueForMap(String key, String mapKey) {
        boolean isSuccess = false;
        if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(mapKey))) {
            // 获取存储的数据
            String spValue = getString(key);
            if (!TextUtils.isEmpty(spValue)) {
                Gson gson = new Gson();
                HashMap<String, Object> hashMap = gson.fromJson(spValue, new TypeToken<HashMap<String, Object>>() {
                }.getType());

                if (hashMap != null && hashMap.size() > 0) {
                    hashMap.remove(mapKey);
                    isSuccess = true;
                }
            }
        }
        return isSuccess;
    }

}