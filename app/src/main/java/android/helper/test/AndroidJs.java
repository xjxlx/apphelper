package android.helper.test;

import android.helper.utils.ToastUtil;
import android.webkit.JavascriptInterface;

public class AndroidJs {

    private static AndroidJs androidJs;

    private AndroidJs() {
    }

    /**
     * @return 返回一个类对象，用来告诉JS是哪个类
     */
    public static AndroidJs getInstance() {
        if (androidJs == null) {
            androidJs = new AndroidJs();
        }
        return androidJs;
    }

    /**
     * @return Js调用android 使用的别名，这个需要和后台定义好，用来调用本地的方法使用
     */
    @JavascriptInterface
    public static String getJsInterfaceName() {
        return "AndroidJs";
    }

    @JavascriptInterface
    public void jsCallAndroid() {
        ToastUtil.show("Js调用Android方法");
    }

    @JavascriptInterface
    public void jsCallAndroidArgs(String arg) {
        ToastUtil.show(arg);
    }

}
