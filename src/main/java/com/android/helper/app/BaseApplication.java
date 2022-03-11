package com.android.helper.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.android.helper.base.refresh.BaseRefreshFooter;
import com.android.helper.base.refresh.BaseRefreshHeader;
import com.android.helper.utils.ScreenUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer;

import org.jetbrains.annotations.NotNull;

import okhttp3.Interceptor;

/**
 * 初始化CommonApplication的实现类，作用是为了避免多次继承Application，但是在基类中
 * 好多地方要进行一次初始化的设置，这里使用接口的实现去控制，所有Application的初始化工作
 * 都将在这里去实际的完成。使用的时候，固定的调用方法{@link BaseApplication#getInstance()}
 */
public class BaseApplication {

    private static ApplicationInterface mApplication;
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

    public void setApplication(ApplicationInterface iCommonApplication) {
        mApplication = iCommonApplication;
        if (mApplication != null) {
            initApp();
            // 加载公共的逻辑
            mApplication.initApp();
        }
    }

    // <editor-fold desc="initData" defaultstate="collapsed">

    public void initApp() {
        try {
            // 捕获所有的异常，存入到app目录下
            Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        } catch (Exception e) {
            Log.e("捕获系统异常信息：", e.getMessage());
        }

        ScreenUtil.getScreenHeight(getApplication());
        initLogger();
        initSmartRefreshLayout();
    }
    //</editor-fold>

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

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)      //（可选）是否显示线程信息。 默认值为true
                .methodCount(0)               // （可选）要显示的方法行数。 默认2
                .methodOffset(0)               // （可选）设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
                .tag(mApplication.logTag())                  //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isDebug(); // 只有在 Debug模式下才会打印
            }
        });
    }

    public void initSmartRefreshLayout() {

        //SmartReRefreshLayout的初始化，static 代码段可以防止内存泄露
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout refreshLayout) {
                //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
                //   refreshLayout.setReboundDuration(300);//回弹动画时长（毫秒）

                // 设置刷新的背景颜色和字体的颜色
                //  refreshLayout.setPrimaryColorsId(R.color.colorPrimary, R.color.base_refresh_foot_background);

                // refreshLayout.setHeaderHeight(100);//Header标准高度（显示下拉高度>=标准高度 触发刷新）
                // refreshLayout.setFooterHeight(100);//Footer标准高度（显示上拉高度>=标准高度 触发加载）

                // refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
                // refreshLayout.setEnableHeaderTranslationContent(true);//是否下拉Header的时候向下平移列表或者内容
                // refreshLayout.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
                refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//是否在列表不满一页时候开启上拉加载功能
                //  refreshLayout.setEnableFooterFollowWhenNoMoreData(true);//是否在全部加载结束之后Footer跟随内容1.0.4
                //  refreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
                //  refreshLayout.setEnableScrollContentWhenRefreshed(true);//是否在刷新完成时滚动列表显示新的内容 1.0.5

                //  refreshLayout.setDisableContentWhenRefresh(false);//是否在刷新的时候禁止列表的操作
                // refreshLayout.setDisableContentWhenLoading(false);//是否在加载的时候禁止列表的操作

                // refreshLayout.setEnableOverScrollBounce(true); // 设置是否启用越界回弹
                // refreshLayout.setEnableAutoLoadMore(false); //设置是否监听列表在滚动到底部时触发加载事件（默认true）

                //  refreshLayout.autoRefresh();//自动刷新
                //  refreshLayout.autoLoadMore();//自动加载
                //  refreshLayout.autoRefresh(400);//延迟400毫秒后自动刷新
                //  refreshLayout.autoLoadMore(400);//延迟400毫秒后自动加载
                //  refreshLayout.finishRefresh();//结束刷新
                //  refreshLayout.finishLoadMore();//结束加载
                //  refreshLayout.finishRefresh(3000);//延迟3000毫秒后结束刷新
                //  refreshLayout.finishLoadMore(3000);//延迟3000毫秒后结束加载
                //  refreshLayout.finishRefresh(false);//结束刷新（刷新失败）
                //  refreshLayout.finishLoadMore(false);//结束加载（加载失败）
                //  refreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
                //  refreshLayout.closeHeaderOrFooter();//关闭正在打开状态的 Header 或者 Footer（1.1.0）
                //  refreshLayout.resetNoMoreData();//恢复没有更多数据的原始状态 1.0.4（1.1.0删除）
                //  refreshLayout.setNoMoreData(false);//恢复没有更多数据的原始状态 1.0.5

                // 刷新的时候，禁止其他的操作，这两个属性必须要加上，不然可能在刷新的时候，出现异常
                refreshLayout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
                refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
            }
        });

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NotNull
            @Override
            public RefreshHeader createRefreshHeader(@NotNull Context context, @NotNull RefreshLayout layout) {
                return new BaseRefreshHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NotNull
            @Override
            public RefreshFooter createRefreshFooter(@NotNull Context context, @NotNull RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new BaseRefreshFooter(context).setDrawableSize(20);
            }
        });
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
