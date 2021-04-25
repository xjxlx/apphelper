package android.helper.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.helper.R;
import android.os.Build;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.helper.base.BaseTitleActivity;

import static com.android.helper.common.CommonConstants.KEY_BASE_WEB_VIEW_URL;

public class TestWebViewActivity extends BaseTitleActivity {

    private android.webkit.WebView mWvTest;
    private android.widget.ProgressBar mPbProgress;
    private String mWebUrl;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_web_view;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试WebView");
        mWvTest = findViewById(R.id.wv_test);
        mPbProgress = findViewById(R.id.pb_progress);

        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        // 开始配置webView
        WebSettings settings = mWvTest.getSettings();
        settings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持)
        settings.setJavaScriptEnabled(true);// 支持js功能

//        mWvTest.addJavascriptInterface(mAndroidJs, AndroidJs.getJsInterfaceName());

        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true);// 支持双击缩放(wap网页不支持)
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);
        //禁止屏幕缩放
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        settings.setBlockNetworkImage(false);//解决图片不显示

        //  WebSettings.LOAD_DEFAULT 如果本地缓存可用且没有过期则使用本地缓存，否加载网络数据 默认值
        //  WebSettings.LOAD_CACHE_ELSE_NETWORK 优先加载本地缓存数据，无论缓存是否过期
        //  WebSettings.LOAD_NO_CACHE  只加载网络数据，不加载本地缓存
        //  WebSettings.LOAD_CACHE_ONLY 只加载缓存数据，不加载网络数据
        //Tips:有网络可以使用LOAD_DEFAULT 没有网时用LOAD_CACHE_ELSE_NETWORK
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 解决图片不显示问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 使用该方法，就不会去启动其他的浏览器
        mWvTest.setWebViewClient(new WebViewClient());

        // 使用该方法，去加载进度条
        mWvTest.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100) {
                    //加载完毕进度条消失
                    mPbProgress.setVisibility(View.GONE);
                } else {
                    //更新进度
                    mPbProgress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        mWebUrl = intent.getStringExtra(KEY_BASE_WEB_VIEW_URL);

        //加载网页链接
        mWvTest.loadUrl(mWebUrl);
    }
}