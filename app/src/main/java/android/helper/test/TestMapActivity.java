package android.helper.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.helper.R;
import android.view.View;

import com.android.helper.base.BaseTitleActivity;

import static com.android.helper.common.CommonConstants.KEY_BASE_WEB_VIEW_URL;

/**
 * 测试功能的集合
 */
public class TestMapActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_map;
    }

    @Override
    protected void initListener() {
        super.initListener();
        setonClickListener(R.id.tv_test_handler, R.id.tv_elv, R.id.tv_flex_box, R.id.tv_test_js_to_android,
                R.id.tv_test_scroll_help, R.id.tv_test_more_adapter, R.id.tv_test_ch, R.id.tv_test_web_socket,
                R.id.tv_test_webview,R.id.tv_test_touch);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_test_handler:
                startActivity(TestHandlerActivity.class);
                break;
            case R.id.tv_elv:
                startActivity(ExpandableActivity.class);
                break;
            case R.id.tv_flex_box:
                startActivity(FlexBoxLayoutActivity.class);
                break;
            case R.id.tv_test_js_to_android:
                startActivity(AndroidJSActivity.class);
                break;
            case R.id.tv_test_scroll_help:
                startActivity(TestScrollHelperActivity.class);
                break;
            case R.id.tv_test_more_adapter:
                startActivity(MoreAdapterActivity.class);
                break;
            case R.id.tv_test_ch:
                startActivity(SlidingMenuActivity.class);
                break;
            case R.id.tv_test_web_socket:
                startActivity(TestWebSocketActivity.class);
                break;
            case R.id.tv_test_webview:
                Intent intent = new Intent(mContext, TestWebViewActivity.class);
                String url = "http://wx.smartservice.bjev.com.cn/BAIC_C62X_OM_HTML5_demo/index.html";
                intent.putExtra(KEY_BASE_WEB_VIEW_URL, url);
                startActivity(intent);
                break;
            case R.id.tv_test_touch:
                startActivity(TestTouchActivity.class);
                break;
        }
    }

}