package com.android.helper.utils.account.keep;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

/**
 * 屏幕一个像素的activity ，只有在8.0一下使用，8.0以上被废弃掉
 */
public class KeepActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = 1;
        attributes.height = 1;
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);
        KeepManager.getInstance().setKeep(this);
    }

}
