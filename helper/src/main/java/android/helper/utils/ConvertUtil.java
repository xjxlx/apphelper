package android.helper.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 转换工具类
 */
public class ConvertUtil {

    /**
     * 加密手机号码,把手机号码的第三位到第七位隐藏
     *
     * @param view TextView的对象
     */
    public static void setPhoneNumber(TextView view) {
        String str = view.getText().toString();
        StringBuffer buffer = new StringBuffer(str);
        StringBuffer replace = buffer.replace(3, 7, "****");
        view.setText(replace);
    }

    /**
     * @param list
     * @return 数组转换集合
     */
    public static String[] ListToArray(List<String> list) {

        String[] arr = new String[list.size()];

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                arr[i] = s;
            }
        }
        return arr;
    }

    /**
     * @param array
     * @param <T>   数组的泛型
     * @return 把一个数组转换成集合
     */
    public static <T> List<T> ArrayToList(T[] array) {
        List<T> list = new ArrayList<>();

        if (array != null && array.length > 0) {
            list = Arrays.asList(array);
        }
        return list;
    }

    /**
     * 动态设置shape的颜色
     *
     * @param imageView
     * @param color
     */
    public static void setShapeColor(ImageView imageView, String color) {

        Drawable background = imageView.getBackground();
        if (background instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(Color.parseColor(color));
        }
    }

    public static void setShapeColor(TextView imageView, int color) {

        Drawable background = imageView.getBackground();
        if (background instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            if (gradientDrawable != null) {
                gradientDrawable.setColor(imageView.getContext().getResources().getColor(color));
            }
        }
    }

    // 将bitmap转为byte格式的数组
    public static byte[] bmpToByteArray(final Bitmap bitmap, final boolean needRecycle) {
        //创建字节数组输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //Bitmap.compress()方法的参数format可设置JPEG或PNG格式；quality可选择压缩质量；fOut是输出流（OutputStream）
        boolean compress = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        LogUtil.e("压缩：" + compress);
        if (needRecycle) {
            bitmap.recycle();
        }
        //将字节数组输出流转为byte数组
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String MapToJson(Map<String, Object> map) {
        String json = "";
        if (map != null || map.size() > 0) {
//            String userId = SPUtils.getInstance().getString(Constants.USER_NAME);
//            map.put("mUserId", userId);
            //Map 转成  JSONObject 字符串
            JSONObject jsonObj = new JSONObject(map);
            if (jsonObj != null) {
                json = jsonObj.toString();
            }
        }
        return json;
    }

    /**
     * @param map
     * @return 把map的value 转换为 list集合
     */
    public static <T> ArrayList<T> MapToList(Map<String, T> map) {
        ArrayList<T> list = new ArrayList<T>();
        if (map != null && map.size() > 0) {
            Set<Map.Entry<String, T>> entries = map.entrySet();
            for (Map.Entry<String, T> bean : entries) {
                T value = bean.getValue();
                list.add(value);
            }
        }
        return list;
    }

    /**
     * @param set set 集合
     * @return 把一个set集合转换为list集合
     */
    public static <T> ArrayList<T> SetToList(HashSet<T> set) {
        if (set == null) {
            return null;
        }

        ArrayList<T> result = new ArrayList<T>(set);

        return result;
    }

    /**
     * @param dp 具体的dp值
     * @return 使用标准的dp值
     */
    public static float toDp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * @param px px的值
     * @return 返回一个标准的px的值
     */
    public static float toPx(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * @param sp sp的值
     * @return 返回一个标准的sp的值
     */
    public static float toSp(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    /***
     *
     * @param url 指定的url，必须是一个合理的url，否则返回一个空数组
     * @return 从Url中解析出来所有的参数
     */
    public static Map<String, String> getParameterForUrl(String url) {
        Map<String, String> map = new HashMap<>();

        if (url.contains("?")) {
            String trim = url.trim();
            String[] split = trim.split("[?]");
            String sp = split[1];
            if (!TextUtils.isEmpty(sp)) {
                // 解析出每一个对象
                String[] split1 = sp.split("[&]");
                for (int i = 0; i < split1.length; i++) {
                    String s = split1[i];
                    if (!TextUtils.isEmpty(s)) {
                        String[] split2 = s.split("[=]");
                        map.put(split2[0], split2[1]);
                    }
                }

            }
        }
        return map;
    }

    /**
     * @return 把指定的集合转换成一个String字符串
     */
    public static String ListToString(List<String> list) {
        if (list != null && list.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                String value = list.get(i);
                buffer.append(value);
            }
            return buffer.toString();
        }
        return "";
    }

    /**
     * @param list 数据源的集合
     * @param key  指定不用的数据的key
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
