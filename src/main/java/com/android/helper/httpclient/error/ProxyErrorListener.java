package com.android.helper.httpclient.error;

/**
 * @author dasouche
 * @CreateDate: 2021/9/24-4:23 下午
 * @Description: 网络错误信息的代理接口
 */
public interface ProxyErrorListener {
    void errorCallBack(Throwable throwable);
}
