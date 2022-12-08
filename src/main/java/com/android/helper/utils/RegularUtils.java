package com.android.helper.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类  校验的工具列
 * Created by erge 2019-10-18 13:30
 */
public class RegularUtils {

    /**
     * 是否是正确手机号的正则表达式
     * 为了防止新号段手机号，只要满足首位是1，总共11位即判断位正确的手机号
     */
    public static boolean isCorrectPhone(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) return false;
        String regExp = "^[1][1-9]{10}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phoneNumber);
        return m.find();
    }

    /**
     * @param content 匹配的类型
     * @param regular 匹配公式
     * @return 匹配结果 如果匹配上了则返回true,否则返回false
     */
    public static boolean match(String content, String regular) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(regular)) {
            return false;
        } else {
            Pattern p = Pattern.compile(regular);
            Matcher m = p.matcher(content);
            return m.find();
        }
    }

}
