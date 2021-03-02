package android.helper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.helper.R;
import android.helper.app.BaseApplication;

@SuppressLint("StaticFieldLeak")
public class ToastUtil {
    
    private static final String TAG = "ToastUtil";
    private static Toast toast;
    private static int yOffset;
    private static Context context;
    private static View view;
    
    /**
     * 私有化构造
     */
    private ToastUtil() {
    }
    
    /**
     * 强大的吐司，能够连续弹的吐司,默认弹出在屏幕底部5分之一的位置
     *
     * @param text 内容
     */
    public static void show(String text) {
        if (context == null) {
            context = BaseApplication.getContext();
        }
        
        if (yOffset <= 0) {
            int screenHeight = ScreenUtil.getScreenHeight(context);
            yOffset = screenHeight / 5;
        }
        show(text, Toast.LENGTH_SHORT, Gravity.BOTTOM, 0, yOffset);
    }
    
    /**
     * @param text     内容
     * @param duration 时间
     * @param gravity  位置
     * @param xOffset  默认居中
     * @param yOffset  偏移，如果为-1的话，就使用默认屏幕5分之一的高度
     */
    @SuppressLint("InflateParams")
    public static void show(String text, int duration, int gravity, int xOffset, int yOffset) {
        
        if (context == null) {
            context = BaseApplication.getContext();
        }
        
        if (context == null) {
            LogUtil.e(TAG, "context为空！");
            return;
        }
        
        if (TextUtils.isEmpty(text)) {
            return;
        }
        
        if (toast != null) {
            toast.cancel();
        }
        
        toast = new Toast(context);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.widget_toast, null);
        }
        TextView textView = view.findViewById(R.id.message);
        // 4:设置布局的内容
        textView.setText(text);
        
        // 5:设置Toast的参数
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setView(view);
        
        toast.setDuration(duration);
        toast.show();
    }
    
}
