package com.android.helper.utils;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author : 流星
 * @CreateDate: 2021/10/13-4:26 下午
 * @Description:
 */
public class UrlUtil {

    /**
     * @param value   包含URL的字符串
     * @param pattern 正则表达式的匹配规则
     * @param scheme  url的域名，将被附加到不以该方案开始的链接。
     * @author : 流星
     * @CreateDate: 2021/10/13
     * @Description: 提取字符串中的链接地址
     */
    public List<String> extractUrl(String value, String pattern, String scheme) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(value) && (!TextUtils.isEmpty(pattern)) && (scheme != null)) {
            Pattern compile = Pattern.compile(pattern);
            extractUrl(value, compile, scheme);
        }
        return result;
    }

    public List<String> extractUrl(String value, Pattern pattern, String scheme) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(value) && (pattern != null) && (scheme != null)) {
            String trim = value.trim();
            SpannableString spannableString = SpannableString.valueOf(trim);
            Linkify.addLinks(spannableString, pattern, scheme);
            if (spannableString != null) {
                URLSpan[] urlSpans = spannableString.getSpans(0, trim.length(), URLSpan.class);
                if (urlSpans != null) {
                    for (URLSpan urlSpan : urlSpans) {
                        if (urlSpan != null) {
                            String url = urlSpan.getURL();
                            result.add(url);
                        }
                    }
                }
            }
        }
        return result;
    }

}
