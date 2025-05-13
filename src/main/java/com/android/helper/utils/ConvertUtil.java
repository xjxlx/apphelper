package com.android.helper.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import com.android.common.utils.LogUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 转换工具类 */
public class ConvertUtil {

  /**
   * @param list String类型的数据集合
   * @return 数组转换集合
   */
  public static String[] ListToStringArray(List<String> list) {
    String[] arr = new String[0];
    if ((list != null) && (list.size() > 0)) {
      arr = new String[list.size()];
      for (int i = 0; i < list.size(); i++) {
        arr[i] = list.get(i);
      }
    }
    return arr;
  }

  /**
   * @param <T> 数组的泛型，各种的类型都可以转换,测试
   * @return 把一个数组转换成集合
   */
  public static <T> List<T> ArrayToList(T[] array) {
    List<T> list = new ArrayList<>();
    if ((array != null) && (array.length > 0)) {
      list = Arrays.asList(array);
    }
    return list;
  }

  /**
   * 动态设置shape的颜色
   *
   * @param view view的对象
   * @param color 颜色的字符串，例如：“#ff99ff”
   */
  public static void setShapeColor(View view, String color) {
    Drawable background = view.getBackground();
    if (background instanceof GradientDrawable) {
      GradientDrawable gradientDrawable = (GradientDrawable) background;
      gradientDrawable.setColor(Color.parseColor(color));
    }
  }

  // 将bitmap转为byte格式的数组
  public static byte[] bmpToByteArray(final Bitmap bitmap, final boolean needRecycle) {
    // 创建字节数组输出流
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // Bitmap.compress()方法的参数format可设置JPEG或PNG格式；quality可选择压缩质量；fOut是输出流（OutputStream）
    boolean compress = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
    LogUtil.e("压缩：" + compress);
    if (needRecycle) {
      bitmap.recycle();
    }
    // 将字节数组输出流转为byte数组
    byte[] result = outputStream.toByteArray();
    try {
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * @param list 数据源的集合
   * @param key 指定不用的数据的key
   * @return 过滤掉数组中不用的数据
   */
  public static List<String> filterList(List<String> list, String key) {
    if (list.contains(key)) {
      ArrayList<String> ts = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
        String t = list.get(i);
        if (!TextUtils.equals(t, key)) {
          ts.add(t);
        }
      }
      return ts;
    } else {
      return list;
    }
  }
}
