package com.android.helper.app;

import android.app.Application;

import com.android.common.app.ApplicationManager;

import okhttp3.Interceptor;

/**
 * @author : 流星
 * @CreateDate: 2021/12/25-11:46
 * @Description: Application 的通用接口
 */
public interface ApplicationInterface {

    /**
     * 初始化application的一些操作
     */
    void initApp();

    /**
     * @return 获取Application的Context作为公共类库的功用的Context.
     */
    Application getApplication();

    /**
     * @return 当前是不是debug模式
     */
    boolean isDebug();

    /**
     * @return 当前应用中log的tag, 如果项目的应用中发生了异常，那么就会在sd卡的目录下生成一个以这个tag作为名字的文件夹
     */
    String logTag();

    /**
     * @return 项目中app的名字
     */
    String getAppName();

    /**
     * @return 设置基类的url
     */
    String getBaseUrl();

    /**
     * @return 设置公用的拦截器
     */
    Interceptor[] getInterceptors();

//    com.android.common.app.ApplicationManager.Builder
    ApplicationManager.Builder getBuilder();
}
