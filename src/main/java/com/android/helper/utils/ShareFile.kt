package com.android.helper.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * @author : 流星
 * @CreateDate: 2023/4/4-23:19
 * @Description:
 */
class ShareFile private constructor() {
    companion object {
        @JvmStatic
        val instance: ShareFile by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ShareFile()
        }
    }

    /**
     * 分享指定的文件
     */
    fun shareFile(
        activity: Activity,
        path: String,
    ) {
        val file = File(path)
        if (!file.exists()) {
            ToastUtil.show("分享的文件不存在！")
            return
        }
        val share = Intent(Intent.ACTION_SEND)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri: Uri = FileProvider.getUriForFile(activity, activity.packageName + ".FileProvider", file)
            share.putExtra(Intent.EXTRA_STREAM, contentUri)
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        }
        share.type = "application/vnd.ms-excel" // 显示可以展示的文件，这里显示的是表格文件
        share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(Intent.createChooser(share, "分享文件"))
    }
}
