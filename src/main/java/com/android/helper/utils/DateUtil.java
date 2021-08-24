package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    /**
     * @param pattern 指定格式，例如：yyyy-MM-dd HH:mm:ss
     * @return 获取当前的时间，返回String类型的字符串
     */
    public static String getCurrentTimeToString(String pattern) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * @param pattern 指定格式，例如：yyyy-MM-dd HH:mm:ss
     * @return 获取当前的时间，返回String类型的字符串
     */
    public static String getTimeForDate(Date date, String pattern) {
        String result = "";
        try {
            if ((!TextUtils.isEmpty(pattern)) && (date != null)) {
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                result = formatter.format(date);
            }
        } catch (Exception e) {
            LogUtil.e("转换失败！");
        }
        return result;
    }

    /**
     * @param duration 当前的时间
     * @return 格式化一个long类型的毫秒值，如果有小时，则格式化为：01:30:59，否则格式化为：30:59
     */
    public static CharSequence formatMillis(long duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND, (int) duration);
        boolean hasHour = duration / (60 * 60 * 1000) > 0;
        CharSequence pattern = hasHour ? "kk:mm:ss" : "mm:ss";
        return DateFormat.format(pattern, calendar);
    }
}
