package com.android.helper.utils.photo;

import android.content.Context;
import android.text.TextUtils;

import com.android.helper.utils.FileUtil;
import com.android.helper.utils.LogUtil;
import com.luck.picture.lib.entity.LocalMedia;

import org.jetbrains.annotations.NotNull;

import kotlin.Suppress;

public class PhotoUtil {

    /**
     * @param context    context
     * @param localMedia localMedia
     * @return 依赖于图片选择库的一个工具，用来返回选中的对象地址
     */
    @Suppress(names = "MISSING_DEPENDENCY_CLASS")
    public static String getPathForSelectorPicture(@NotNull Context context, @NotNull LocalMedia localMedia) {
        boolean compressed = localMedia.isCompressed();
        String url;
        if (compressed) {
            url = localMedia.getCompressPath();
            LogUtil.e("压缩拍摄视频的路径为：$mPhoto_path");
        } else {
            url = localMedia.getPath();
            LogUtil.e("没有压缩拍摄视频的路径为：$mPhoto_path");
        }
        if (!TextUtils.isEmpty(url)) {
            return FileUtil.UriToPath(context, url);
        }
        return null;
    }

}
