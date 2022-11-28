package com.android.helper.utils.statusBar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.helper.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 状态栏工具
 * <p>
 * 使用说明： 1：如果是正常的Activity的话，就直接设置颜色：setStatusColor（），并设置状态栏字体颜色：setStatusFontColor（）
 * 2：如果是需要布局延伸到状态栏的话，就设置透明状态栏：setStatusTranslucent（），并设置颜色：setStatusFontColor（）
 * 2.1：如果要延伸布局到状态栏中的话，就需要继承一个：fitsSystemWindows 属性为false的主题才可以
 */
public class StatusBarUtil {

    private static StatusBarUtil util;
    private Activity mActivity;

    public StatusBarUtil(Activity activity) {
        this.mActivity = activity;
    }

    public static StatusBarUtil getInstance(Activity activity) {

        util = new StatusBarUtil(activity);
        return util;
    }

    /************************************* 状态栏设置 ---> 开始 ********************************************/

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public StatusBarUtil setStatusTranslucent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 状态栏
            window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = mActivity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return util;
    }

    /**
     * 如果需要内容紧贴着StatusBar 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    public StatusBarUtil setFitSystemWindow(boolean fitSystemWindow) {
        View rootView = null;

        if (rootView == null) {
            rootView = mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        rootView.setFitsSystemWindows(fitSystemWindow);
        return util;
    }

    /**
     * 设置status的颜色
     *
     * @param color
     */
    @TargetApi(19)
    public StatusBarUtil setStatusColor(int color) {
        // 设置状态栏颜色
        Window window = mActivity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 状态栏
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(mActivity.getResources().getColor(color));
        return util;
    }

    /**
     * 半透明状态栏
     */
    protected void setHalfTransparent() {

        if (Build.VERSION.SDK_INT >= 21) {// 21表示5.0
            View decorView = mActivity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else if (Build.VERSION.SDK_INT >= 19) {// 19表示4.4
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 小米的MIUI和魅族的Flyme在Android 4.4之后各自提供了自家的修改方法，其他品牌只能在Android 6.0及以后才能修改。
     *
     * @param dark
     * @return 指定状态栏字体的颜色，true 设置为黑色，false 设置为白色
     */
    public StatusBarUtil setStatusFontColor(boolean dark) {

        // 字体颜色在6.0以上才可以设置
        Window window = mActivity.getWindow();

        // 1:首先判断是不是小米和魅族
        String deviceBrand = getDeviceBrand();

        if (TextUtils.equals(deviceBrand, "Xiaomi")) {
            // LogUtil.e("sss:小米手机！");

            setMIUIStatusBarLightMode(window, dark);

        } else if (TextUtils.equals(deviceBrand, "Meizu")) {

            // LogUtil.e("sss:魅族手机！");
            // setMeizuStatusBarDarkIcon(window, dark);

            StatusBarColorUtil.setStatusBarDarkIcon(mActivity, dark);
        } else {
            // LogUtil.e("sss:" + deviceBrand);
            // 大于等于6.0 就设置为谷歌原生的处理方式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 谷歌原生的处理方式
                View decor = window.getDecorView();
                // 谷歌原生的处理方式
                if (dark) {
                    decor.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    // 设置状态栏底色白色
                    decor.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
            } else {
                // 如果小于6.0 ，就把状态栏设置为30%的透明色 ,否则在某些手机上就不会显示出来正常的状态栏的字体颜色
                setStatusColor(R.color.transparent_30);
            }
        }

        return util;
    }

    /**
     * @param window
     * @param dark
     * @return 小米手机的处理方式
     */
    public boolean setMIUIStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;

        // 新版的处理方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (dark) {
                window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

        } else {
            // 老版的处理方式
            if (window != null) {
                Class clazz = window.getClass();
                try {
                    int darkModeFlag = 0;
                    Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                    Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                    darkModeFlag = field.getInt(layoutParams);
                    Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);

                    extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
                    result = true;
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public boolean setMeizuStatusBarDarkIcon(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public String getDeviceBrand() {
        return Build.BRAND;
    }

    /************************************* 状态栏设置 ---> 结束 ********************************************/

    /**
     * 隐藏状态栏，并且全屏
     */
    public void HideStateBar() {
        // 布局延伸进状态栏
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏

    }

    /**
     * 隐藏虚拟按键，滑动时候可以重新出现
     */
    public StatusBarUtil HideVirtualButtons() {
        if (mActivity == null) {
            return null;
        }
        // 布局延伸进状态栏
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏

        // 隐藏底部导航栏，并禁止弹出
        View decorView = mActivity.getWindow().getDecorView();
        int uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);
        return util;
    }

    public StatusBarUtil hideBottomUIMenu() {
        // 隐藏虚拟按键，滑动也不能重新显示
        Window window = mActivity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        return util;
    }

    /**
     * @return 隐藏底导航栏，手势滑动的时候再次出现，默认隐藏,建议放到基类中使用，避免小米手机底部出现幺蛾子
     */
    public StatusBarUtil hideBottomMenu() {
        // 隐藏虚拟按键，滑动也不能重新显示
        Window window = mActivity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);

        // 隐藏底部导航栏，并禁止弹出
        View decorView = mActivity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // 自动过一会后自动隐藏
        ;

        decorView.setSystemUiVisibility(uiOptions);

        return util;
    }

}
