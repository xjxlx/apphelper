package com.android.helper.utils;

import android.text.TextUtils;

/**
 * Class的工具类，用来获取class的信息
 */
public class ClassUtil {

    public static String mName = "";
    public static String mPathName = "";
    public static Object mObj;

    /**
     * @return 获取本类的名字
     */
    public static String getClassName(Object object) {
        if ((object == null) || (mObj == null) || (!object.equals(mObj) || (TextUtils.isEmpty(mName)))) {
            mName = object.getClass().getSimpleName();
            mObj = object;
        }
        return mName;
    }

    /**
     * @return 获取本类相对路径的名字
     */
    public static String getClassPathName(Object object) {
        if ((object == null) || (mObj == null) || (!object.equals(mObj) || (TextUtils.isEmpty(mPathName)))) {
            mPathName = object.getClass().getName();
            mObj = object;
        }
        return mPathName;
    }

}
