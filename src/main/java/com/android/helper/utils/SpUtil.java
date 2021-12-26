package com.android.helper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.helper.app.BaseApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

    private static SharedPreferences sp;

    // 获取SP 对象
    private static synchronized SharedPreferences getSp() {
        if (sp == null) {
            sp = BaseApplication.getInstance().getApplication().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp;
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
            // 开启编辑器
            SharedPreferences.Editor edit = sp.edit();
            // 存入数据
            if (value == null) {
                value = "";
            }
            edit.putString(key, value);
            // 提交
            edit.apply();
        } else {
            LogUtil.e("存入PutString类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取String类型的数据, 如果获取不到, 则返回null
     */
    public static String getString(String key) {
        String value = null;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getString(key, null);
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
            // 开启编辑器
            SharedPreferences.Editor edit = sp.edit();
            // 存入数据
            edit.putInt(key, value);
            // 提交
            edit.apply();
        } else {
            LogUtil.e("存入PutInt类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取int类型的数据, 如果获取不到, 则返回0
     */
    public static int getInt(String key) {
        int value = 0;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getInt(key, 0);
        } else {
            LogUtil.e("取出getInt类型的值为空!");
        }
        return value;
    }

    /**
     * @param key          取数据的key
     * @param defaultValue 默认返回值
     * @return 获取int类型的数据, 如果获取不到, 则返回 默认返回值
     */
    public static int getInt(String key, int defaultValue) {
        int value = 0;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getInt(key, defaultValue);
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
            // 开启编辑器
            SharedPreferences.Editor edit = sp.edit();
            // 存入数据
            edit.putBoolean(key, value);
            // 提交
            edit.apply();
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
            value = sp.getBoolean(key, false);
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
            // 开启编辑器
            SharedPreferences.Editor edit = sp.edit();
            // 存入数据
            edit.putLong(key, value);
            // 提交
            edit.apply();
        } else {
            LogUtil.e("存入PutInt类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取long类型的数据, 如果获取不到, 则返回0
     */
    public static long getLong(String key) {
        long value = -1;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getLong(key, 0);
        } else {
            LogUtil.e("取出getInt类型的值为空!");
        }
        return value;
    }

    public static long getLong(String key, long defaultLong) {
        long value = -1;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getLong(key, defaultLong);
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
            // 开启编辑器
            SharedPreferences.Editor edit = sp.edit();
            // 存入数据
            edit.putFloat(key, value);
            // 提交
            edit.apply();
        } else {
            LogUtil.e("存入PutFloat类型的值为空!");
        }
    }

    /**
     * @param key 获取数据的key
     * @return 获取float类型的数据, 如果获取不到, 则返回0.0f
     */
    public static float getFloat(String key) {
        float value = 0.0f;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getFloat(key, 0.0f);
        } else {
            LogUtil.e("取出getFloat类型的值为空!");
        }
        return value;
    }

    public static float getFloat(String key, float defaultValue) {
        float value = 0.0f;
        if (!TextUtils.isEmpty(key)) {
            getSp();
            value = sp.getFloat(key, defaultValue);
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
            SharedPreferences.Editor edit = sp.edit();
            edit.remove(key);
            edit.apply();
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
            Gson gson = new Gson();
            HashMap<String, Object> map = null;
            // 获取存储的数据
            String string = getString(key);
            // 数据不为空
            if (!TextUtils.isEmpty(string)) {
                map = gson.fromJson(string, HashMap.class);
                if (map != null) {
                    map.put(mapKey, mapValue);
                }
            } else {
                // 之前没有存储过数据
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
     * @param key    sp的key
     * @param mapKey map的key
     * @return 根据指定的key返回map中存储的value
     */
    public static String getStringForMap(String key, String mapKey) {
        if ((TextUtils.isEmpty(key)) || (TextUtils.isEmpty(mapKey))) {
            return null;
        }
        // 获取存储的数据
        String string = getString(key);
        if (TextUtils.isEmpty(string)) {
            // 数据为空，返回null
            return null;
        } else {
            try {
                Gson gson = new Gson();
                HashMap<String, String> hashMap = gson.fromJson(string, HashMap.class);
                if (hashMap == null || hashMap.size() <= 0) {
                    return null;
                } else {
                    return hashMap.get(mapKey);
                }
            } catch (JsonSyntaxException e) {
                LogUtil.e("转换HasMap数据失败！");
                return null;
            }
        }
    }

    public static int getIntForMap(String key, String mapKey) {
        if ((TextUtils.isEmpty(key)) || (TextUtils.isEmpty(mapKey))) {
            return 0;
        }
        // 获取存储的数据
        String string = getString(key);
        if (TextUtils.isEmpty(string)) {
            return 0;
        } else {
            try {
                Gson gson = new Gson();
                HashMap<String, Integer> hashMap = gson.fromJson(string, HashMap.class);
                if (hashMap == null || hashMap.size() <= 0) {
                    return 0;
                } else {
                    return hashMap.get(mapKey);
                }
            } catch (JsonSyntaxException e) {
                LogUtil.e("转换HasMap数据失败！");
                return 0;
            }
        }
    }

    /**
     * @param key sp的key
     * @return 根据指定的key返回map中存储的value
     */
    public static HashMap<String, String> getMap(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        // 获取存储的数据
        String string = getString(key);
        if (TextUtils.isEmpty(string)) {
            // 数据为空，返回null
            return null;
        } else {
            try {
                Gson gson = new Gson();
                HashMap<String, String> hashMap = gson.fromJson(string, HashMap.class);
                if (hashMap == null || hashMap.size() <= 0) {
                    return null;
                } else {
                    return hashMap;
                }
            } catch (JsonSyntaxException e) {
                LogUtil.e("转换HasMap数据失败！");
                return null;
            }
        }
    }

    public static boolean clearMap(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        // 获取存储的数据
        String string = getString(key);
        if (TextUtils.isEmpty(string)) {
            // 数据为空，返回null
            return false;
        } else {
            try {
                Gson gson = new Gson();
                HashMap<String, String> hashMap = gson.fromJson(string, HashMap.class);
                if (hashMap == null || hashMap.size() <= 0) {
                    return false;
                } else {
                    hashMap.clear();
                    return true;
                }
            } catch (JsonSyntaxException e) {
                LogUtil.e("转换HasMap数据失败！");
                return false;
            }
        }
    }

    /**
     * @param key    sp的key
     * @param mapKey map的key
     * @return 移除一个map中的key
     */
    public static boolean removeMap(String key, String mapKey) {
        if ((TextUtils.isEmpty(key)) || (TextUtils.isEmpty(mapKey))) {
            return false;
        }
        // 获取存储的数据
        String string = getString(key);
        if (TextUtils.isEmpty(string)) {
            // 数据为空，返回null
            return false;
        } else {
            Gson gson = new Gson();
            HashMap hashMap = gson.fromJson(string, HashMap.class);
            if (hashMap == null || hashMap.size() <= 0) {
                return false;
            } else {
                Object remove = hashMap.remove(mapKey);
                if (remove == null) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

}