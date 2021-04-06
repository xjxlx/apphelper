package android.helper.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
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

}
