package com.android.helper.app;

import android.app.Application;

import androidx.fragment.app.FragmentActivity;

import com.android.helper.utils.ScreenUtil;
import com.android.refresh.app.ApplicationManager;

import okhttp3.Interceptor;

/**
 * 初始化CommonApplication的实现类，作用是为了避免多次继承Application，但是在基类中
 * 好多地方要进行一次初始化的设置，这里使用接口的实现去控制，所有Application的初始化工作
 * 都将在这里去实际的完成。使用的时候，固定的调用方法{@link BaseApplication#getInstance()}
 */
public class BaseApplication {

    private static ApplicationInterface mApplication;
    private static AppBarStatusListener mAppBarStatusListener;
    private static BaseApplication INSTANCE;
    /**
     * 一个项目中通用的对象，该对象一般情况杨下不会被销毁，因为加载的时机比较特殊，只能写成这种方式
     */
    private FragmentActivity mFragmentActivity;

    public static BaseApplication getInstance() {
        if (INSTANCE == null) {
            synchronized (BaseApplication.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BaseApplication();
                }
            }
        }
        return INSTANCE;
    }

    public BaseApplication setApplication(ApplicationInterface iCommonApplication) {
        mApplication = iCommonApplication;
        if (mApplication != null) {
            initApp();
            // 加载公共的逻辑
            mApplication.initApp();
        }
        return this;
    }

    public BaseApplication setAppBarStatusListener(AppBarStatusListener appBarStatusListener) {
        mAppBarStatusListener = appBarStatusListener;
        return this;
    }

    // <editor-fold desc="initData" defaultstate="collapsed">

    public void initApp() {
        ApplicationManager.init(getApplication(), mApplication.getBuilder());
        ScreenUtil.getScreenHeight(getApplication());
    }
    // </editor-fold>

    public Application getApplication() {
        return mApplication.getApplication();
    }

    public boolean isDebug() {
        boolean isDebug = true;
        if (!isNull()) {
            isDebug = mApplication.isDebug();
        }
        return isDebug;
    }

    public String logTag() {
        String logTag = "";
        if (!isNull()) {
            logTag = mApplication.logTag();
        }
        return logTag;
    }

    public String getAppName() {
        String appName = "";
        if (!isNull()) {
            appName = mApplication.getAppName();
        }
        return appName;
    }

    public String getBaseUrl() {
        String baseUrl = "";
        if (!isNull()) {
            baseUrl = mApplication.getBaseUrl();
        }
        return baseUrl;
    }

    public Interceptor[] getInterceptors() {
        Interceptor[] interceptors = null;
        if (!isNull()) {
            interceptors = mApplication.getInterceptors();
        }
        return interceptors;
    }

    public int getAppBarStatusColor() {
        int color = 0;
        if (mAppBarStatusListener != null) {
            color = mAppBarStatusListener.getAppBarStatusColor();
        }
        return color;
    }

    public boolean getAppBarStatusFontDark() {
        boolean dark = false;
        if (mAppBarStatusListener != null) {
            dark = mAppBarStatusListener.getAppBarStatusFontDark();
        }
        return dark;
    }


    public FragmentActivity getCommonLivedata() {
        return mFragmentActivity;
    }

    public void setCommonLivedata(FragmentActivity fragmentActivity) {
        mFragmentActivity = fragmentActivity;
    }

    public boolean isNull() {
        return mApplication == null;
    }

}
