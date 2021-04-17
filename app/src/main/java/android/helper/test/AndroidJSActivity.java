package android.helper.test;

import android.annotation.SuppressLint;
import android.helper.R;
import android.net.Uri;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ToastUtil;

/**
 * JS和前端的交互
 */
public class AndroidJSActivity extends BaseTitleActivity {

    private android.widget.TextView tvAndroidcalljs;
    private android.widget.TextView tvAndroidcalljsargs;
    private android.widget.TextView tvShowmsg;
    private android.webkit.WebView webview;
    private View tv_androidgetjsarg;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_android_j_s;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void initView() {
        super.initView();

        setTitleContent("Android和JS互相调用");

        tvAndroidcalljs = findViewById(R.id.tv_androidcalljs);
        tvAndroidcalljsargs = findViewById(R.id.tv_androidcalljsargs);
        tv_androidgetjsarg = findViewById(R.id.tv_androidgetjsarg);

        tvShowmsg = findViewById(R.id.tv_showmsg);
        webview = findViewById(R.id.webview);

        WebSettings settings = webview.getSettings();
        /**
         * 前端调用移动端时候，必须的设置
         */
        settings.setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/测试.html");
        // webview.loadUrl("https://www.vipandroid.cn/apk/index.html");
        webview.addJavascriptInterface(AndroidJs.getInstance(), AndroidJs.getJsInterfaceName());

        // android 调用JS 无参数方法,不带返回值
        tvAndroidcalljs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.evaluateJavascript("javascript:javacalljs()", null);
            }
        });

        // android 调用JS的时候， 传递参数给JS
        tvAndroidcalljsargs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.evaluateJavascript("javascript:javacalljswith('Android主动传递参数给JS')", null);
            }
        });

        // android 调用JS,并获取js传递过来的参数
        tv_androidgetjsarg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.evaluateJavascript("javascript:returnString()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        ToastUtil.show("获取JS的参数为：" + value);
                    }
                });
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        // 拦截URL的路径
        webview.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                String host = url.getHost();
                String path = url.getPath();
                String scheme = url.getScheme();

                LogUtil.e("scheme:" + scheme + "  host = " + host + "  path:" + path);

                return super.shouldInterceptRequest(view, request);
            }
        });
    }

    private int mCount;

}