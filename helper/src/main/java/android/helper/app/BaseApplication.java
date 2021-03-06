package android.helper.app;

import android.content.Context;
import android.helper.base.refresh.BaseRefreshFooter;
import android.helper.base.refresh.BaseRefreshHeader;
import android.helper.interfaces.ICommonApplication;
import android.helper.utils.ScreenUtil;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    private static ICommonApplication mICommonApplication;
    private static BaseApplication mBaseApplication;
    private static String mBaseUrl;

    public static BaseApplication getInstance() {
        if (mBaseApplication == null) {
            mBaseApplication = new BaseApplication();
        }
        return mBaseApplication;
    }

    public void setICommonApplication(ICommonApplication iCommonApplication) {
        mICommonApplication = iCommonApplication;
        onCreate();
    }

    private void onCreate() {
        // 手动调用初始化方法，让初始化方法得到执行
        if (mICommonApplication != null) {
            mICommonApplication.initApp();
        }
        initData();
    }

    private void initData() {
        try {
            // 捕获所有的异常，存入到app目录下
            Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        } catch (Exception e) {
            Log.e("捕获系统异常信息：", e.getMessage());
        }

        ScreenUtil.getScreenHeight(getContext());
        initLogger();
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)      //（可选）是否显示线程信息。 默认值为true
                .methodCount(0)               // （可选）要显示的方法行数。 默认2
                .methodOffset(0)               // （可选）设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
                .tag(getLogTag())                  //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isDebug(); // 只有在 Debug模式下才会打印
            }
        });
    }

    public static Context getContext() {
        if (mICommonApplication != null) {
            return mICommonApplication.getApplication();
        } else {
            return null;
        }
    }

    /**
     * @return 返回debug的状态，默认为打开的状态
     */
    public static boolean isDebug() {
        if (mICommonApplication != null) {
            return mICommonApplication.isDebug();
        } else {
            return true;
        }
    }

    /**
     * @return 返回log的Tag
     */
    public static String getLogTag() {
        if (mICommonApplication != null) {
            return mICommonApplication.logTag();
        } else {
            return "AppHelper";
        }
    }

    /**
     * @return 返回App的包名，默认返回为空字符串
     */
    public static String getAppName() {
        if (mICommonApplication != null) {
            return mICommonApplication.getAppName();
        } else {
            return "";
        }
    }

    /**
     * @return 返回基类的url
     */
    public static String getBaseUrl() {
        if (!TextUtils.isEmpty(mBaseUrl)) {
            return mBaseUrl;
        } else {
            if (mICommonApplication != null) {
                return mICommonApplication.getBaseUrl();
            } else {
                return "";
            }
        }
    }

    /**
     * 动态设置url
     *
     * @param baseUrl 指定的BaseUrl
     */
    public static void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * @return 返回拦截器对象
     */
    public static Interceptor[] getInterceptors() {
        if (mICommonApplication != null) {
            return mICommonApplication.getInterceptors();
        } else {
            return null;
        }
    }

    static {
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
}
