package android.helper.interfaces.listener;

import android.view.View;

/**
 * Dialog 和 PopupWindow 的打开和关闭的监听
 */
public interface DialogChangeListener {
    void onShow(View view);

    void onDismiss();
}
