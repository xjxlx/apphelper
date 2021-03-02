package android.helper.utils;

import android.app.Activity;
import android.content.Context;

public class ActivityUtil {

    /**
     * 判断Activity是否Destroy
     *
     * @param activity 依赖的页面
     * @return 如果activity已经被销毁了，则返回为true，否则返回false
     */
    public static boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    /**
     * 判断Activity是否Destroy
     *
     * @param context 依赖的页面
     * @return 如果activity已经被销毁了，则返回为true，否则返回false
     */
    public static boolean isDestroy(Context context) {
        if (context == null) {
            return true;
        } else {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                return isDestroy(activity);
            } else {
                return true;
            }
        }
    }

}
