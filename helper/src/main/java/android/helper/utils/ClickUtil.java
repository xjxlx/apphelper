package android.helper.utils;

/**
 * 点击的工具类
 */
public class ClickUtil {

    private static long lastClickTime;

    /**
     * 间隔时间内是否做了双击
     *
     * @param time 间隔时间--单位毫秒(ms)
     * @return true-- 是  ；false -- 否
     */
    public static boolean isDoubleClick(int time) {
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - lastClickTime;

        if (timeInterval > 0 && timeInterval < time) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
