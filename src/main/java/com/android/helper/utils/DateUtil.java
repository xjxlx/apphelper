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

    /**
     * @param stringValue 时间字符串：例如：2021-12-21 15:26:32
     * @param pattern     指定格式，例如：yyyy-MM-dd HH:mm:ss
     * @return 把字符串转换成时间戳
     */
    public static long stringToMillis(String stringValue, String pattern) {
        long time = 0;
        try {
            if ((!TextUtils.isEmpty(pattern)) && (!TextUtils.isEmpty(stringValue))) {
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                Date date = formatter.parse(stringValue);
                //日期转时间戳（毫秒）
                if (date != null) {
                    time = date.getTime();
                }
            }
        } catch (Exception e) {
            LogUtil.e("转换失败！");
        }
        return time;
    }

    /**
     * @param longTime 具体的时间戳
     * @param pattern  转换规则，例如：yyyy-MM-dd HH:mm:ss
     * @return 把一个时间戳转换成一个格式化后的时间
     */
    public static String longTimeToDate(long longTime, String pattern) {
        String time = "";
        try {
            if ((longTime > 0) && (!TextUtils.isEmpty(pattern))) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = new Date(longTime);
                time = simpleDateFormat.format(date);
            }
        } catch (Exception ignored) {
        }
        return time;
    }

    public static String parse(long longTime) {
        String result = "";

        long hh = longTime / 60 / 60 % 60;
        long mm = longTime / 60 % 60;
        long ss = longTime % 60;

        LogUtil.e("hh:" + hh + "  mm:" + mm + "   ss:" + ss);
        if (hh > 0) {
            result = hh + ":" + mm + ":" + ss;
        } else {
            result = "00:" + mm + ":" + ss;
        }

        return result;
    }

    /**
     * @param newTime 当前的时间戳，这个时间戳不是年月日时分秒的，是相差的时间戳
     * @return 返回：小时，分钟，秒的对象
     */
    public static String getTimeToTimeMillis(Long newTime) {
        String hours;
        String minutes;
        String seconds;
        //获取天数
        long day = newTime / 24 / 60 / 60 / 1000;
        //获取小时值
        long hour = (newTime - day * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        //获取分值
        long minute = (newTime - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);
        //获取秒数
        long second = (newTime - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;

        if (hour < 10) {
            hours = "0" + hour;
        } else {
            hours = String.valueOf(hour);
        }

        if (minute < 10) {
            minutes = "0" + minute;
        } else {
            minutes = String.valueOf(minute);
        }

        if (second < 10) {
            seconds = "0" + second;
        } else {
            seconds = String.valueOf(second);
        }
        return hours + ":" + minutes + ":" + seconds;
    }

}
