package android.helper.app;

import android.app.Application;
import android.helper.BuildConfig;
import android.helper.R;
import android.helper.httpclient.AutoInterceptor;
import android.helper.interfaces.ICommonApplication;

import okhttp3.Interceptor;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BaseApplication.getInstance().setICommonApplication(new ICommonApplication() {
            @Override
            public Application getApplication() {
                return AppApplication.this;
            }

            @Override
            public boolean isDebug() {
                return BuildConfig.DEBUG;
            }

            @Override
            public String logTag() {
                return "App";
            }

            @Override
            public String getAppName() {
                return getResources().getString(R.string.app_name);
            }

            @Override
            public void initApp() {
            }

            @Override
            public String getBaseUrl() {
                return "http://api-zhgj-app.beixin.hi-cloud.net:8000/gateway-api/";
            }

            @Override
            public Interceptor[] getInterceptors() {
                return new Interceptor[]{new AutoInterceptor()};
            }
        });
    }

}
