package android.helper.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Administrator on 2019/6/12.
 * 数据的工具类
 */

public class NumberUtil {

    /**
     * @param d      指定的值
     * @param digits 保留的位数
     * @return 把数据转换成 保留指定位数的数据，使用四舍五入
     */
    public static String formatDouble(double d, int digits) {
        NumberFormat nf = NumberFormat.getNumberInstance();

        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);

        return nf.format(d);
    }

    /**
     * @param millisInFuture
     * @return 使用四舍五入的形式把long类型的数据转换成int类型
     */
    public static int NumberForLong(long millisInFuture) {
        int round = Math.round(millisInFuture / 1000);
        return round;
    }

    /**
     * @param array
     * @return 根据传入进来的数组，随机改变position位置，生成一个新的数组
     */
    public static int[] RandomSortToInt(int[] array) {

        if (array == null) {
            return null;
        } else {
            StringBuffer buffer = new StringBuffer();
            for (int s : array) {
                buffer.append(s + "，");
            }
            LogUtil.e("原来：array:" + buffer.toString());

            Random random = new Random();
            for (int i = 0; i < array.length; i++) {
                // 随机产生一个 0 - n（不包含n）的随机数
                int p = random.nextInt(i + 1);

                int tmp = array[i];
                array[i] = array[p];
                array[p] = tmp;
            }

            StringBuffer buffer1 = new StringBuffer();
            for (int s : array) {
                buffer1.append(s + "，");
            }
            LogUtil.e("后来：array:" + buffer1.toString());

            return array;
        }
    }

    /**
     * @param list
     * @return 产生一个0 - 集合长度的随机数,不包含集合长度 的角标
     */
    public static int RandomForListToIndex(List list) {
        if (list == null || list.size() <= 0) {
            return -1;
        } else {
            int size = list.size();

            int index = (int) (Math.random() * (size));

            LogUtil.e("list --->size:" + list.size() + " 随机数：" + index);

            return index;
        }
    }

    /**
     * @param list
     * @return 获取String类型结合种从0 到 集合长度（不包含集合长度）的一个随机数
     */
    public static String RandomForListToString(List<String> list) {
        if (list == null || list.size() <= 0) {
            return null;
        } else {
            int size = list.size();

            int index = (int) (Math.random() * (size));

            LogUtil.e("list --->size:" + list.size() + " 随机数：" + index);

            return list.get(index);
        }
    }

    /**
     * @param array
     * @return 随机改变传入数组的位置，产生一个新的随机数组
     */
    public static String[] RandomSortToString(String[] array) {

        if (array == null) {
            return null;
        } else {
            StringBuffer buffer = new StringBuffer();
            for (String s : array) {
                buffer.append(s + "，");
            }
            LogUtil.e("原来：array:" + buffer.toString());

            Random random = new Random();
            for (int i = 0; i < array.length; i++) {
                // 随机产生一个 0 - n（不包含n）的随机数
                int p = random.nextInt(i + 1);

                String tmp = array[i];
                array[i] = array[p];
                array[p] = tmp;
            }

            StringBuffer buffer1 = new StringBuffer();
            for (String s : array) {
                buffer1.append(s + "，");
            }
            LogUtil.e("后来：array:" + buffer1.toString());

            return array;
        }
    }

    /********************************************* 新的开始 *************************************/

    /**
     * @param value Float 类型的数据
     * @return 把一个Float类型的数据转换成一个正常显示的数据，如果是34.0 则等于34 ，如果是34.01 则等于34.01
     */
    public static String FloatToZeros(Float value) {
        if (value == 0) {
            return "0";
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal decimal = bigDecimal.stripTrailingZeros();
        // 为了数据的纯洁性，不会出现：“38.0”类似的数据
        return decimal.toPlainString();
    }

    /**
     * @param value Double 类型的数据
     * @return 把一个Double类型的数据转换成一个正常显示的数据，如果是34.0 则等于34 ，如果是34.01 则等于34.01
     */
    public static String DoubleToZeros(Double value) {
        if (value == 0) {
            return "0";
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal decimal = bigDecimal.stripTrailingZeros();
        // 为了数据的纯洁性，不会出现：“38.0”类似的数据
        return decimal.toPlainString();
    }

    /**
     * @param value Double 类型的数据
     * @return 把一个Double类型的数据转换成一个正常显示的数据，如果是34.0 则等于34 ，如果是34.01 则等于34.01
     */
    public static String StringToZeros(String value) {
        if (TextUtils.isEmpty(value)) {
            return "0";
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal decimal = bigDecimal.stripTrailingZeros();
        // 为了数据的纯洁性，不会出现：“38.0”类似的数据
        return decimal.toPlainString();
    }

    /**
     * @param dividend     被除数
     * @param divisor      除数
     * @param roundingMode 转换的方式，这里用的最多的是四舍五入和直接舍弃，默认四舍五入
     *                     {@link BigDecimal#ROUND_HALF_UP} 四舍五入
     *                     {@link BigDecimal#ROUND_DOWN} 直接舍弃
     * @param digits       保留的小数位数
     * @return 大整形的除法运算
     */
    public static String DecimalDivide(String dividend, long divisor, int roundingMode, int digits) {
        if ((!TextUtils.isEmpty(dividend)) && (divisor > 0)) {
            BigDecimal dividendDecimal = new BigDecimal(dividend);
            BigDecimal divisorDecimal = new BigDecimal(divisor);

            // 转换为大整形运算
            String result = dividendDecimal.
                    divide(divisorDecimal, digits, roundingMode)  // 除数、保留位数、模式
                    .stripTrailingZeros()// 去掉多余的0
                    .toPlainString();// 转换为科学计数法
            return result;
        } else {
            return "";
        }
    }

    /**
     * @param value 需要转换的字符串
     * @return 把指定的字符转换成小写字符
     */
    public static String StringToLower(String value) {
        if (!TextUtils.isEmpty(value)) {
            return value.toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * @param value 需要转换的字符串
     * @return 把指定的字符全部转换成大写字符
     */
    public static String StringToUpper(String value) {
        if (!TextUtils.isEmpty(value)) {
            return value.toUpperCase();
        } else {
            return "";
        }
    }

    /**
     * @param value 需要转换的数据
     * @return 把一个double的数据格式化为保留两位小数的字符串数据
     */
    public static String formatDigits2(double value) {
        return String.format(Locale.CHINA, "%.2f", value);
    }

    /**
     * @param value        需要转换的数据
     * @param roundingMode 模式，例如：四舍五入，只舍不入
     *                     {@link BigDecimal#ROUND_HALF_UP} 四舍五入
     *                     {@link BigDecimal#ROUND_DOWN} 直接舍弃
     * @param digits       保留的小数位数
     * @return 把一个String类型的数据转换成一个大整形的数据, 但是会保留小数数据
     */
    public static String formatDigitsForString(String value, int roundingMode, int digits) {
        if (!TextUtils.isEmpty(value)) {
            // 转换为大整形运算
            BigDecimal decimal = new BigDecimal(value);
            return decimal
                    .setScale(digits, roundingMode) // 数据模式
                    .stripTrailingZeros()
                    .toPlainString();
        } else {
            return "";
        }
    }

    /**
     * @param value        需要转换的数据
     * @param roundingMode 模式，例如：四舍五入，只舍不入
     *                     {@link BigDecimal#ROUND_HALF_UP} 四舍五入
     *                     {@link BigDecimal#ROUND_DOWN} 直接舍弃
     * @param digits       保留的小数位数
     * @return 把一个String类型的数据转换成一个大整形的数据, 但是会保留小数数据
     */
    public static String formatDigitsForDouble(double value, int roundingMode, int digits) {
        if (value != 0) {
            // 转换为大整形运算
            BigDecimal decimal = new BigDecimal(value);
            return decimal
                    .setScale(digits, roundingMode) // 数据模式
                    .stripTrailingZeros()
                    .toPlainString();
        } else {
            return "";
        }
    }

}