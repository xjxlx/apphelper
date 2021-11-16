package com.android.helper.utils.dialog;

import android.app.Dialog;
import android.view.View;

/**
 * @author : 流星
 * @CreateDate: 2021/11/16-1:55 下午
 * @Description:
 */
public interface DialogClickListener {
    void onClick(View v, DialogUtil.Builder builder);
}
