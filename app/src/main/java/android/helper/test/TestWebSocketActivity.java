package android.helper.test;

import android.helper.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.LogUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.ByteString;

public class TestWebSocketActivity extends BaseTitleActivity {

    private final String TAG_SERVICE = "服务端：";
    private final String TAG_APP = "移动端：";

    private static WebSocket mWebSocketApp;
    private static WebSocket mWebSocketService;
    private MockWebServer mMockWebServer;
    private ExecutorService writeExecutor = Executors.newSingleThreadExecutor();  // 线程池
    private SocketHandler mSocketHandler = new SocketHandler();
    private MockResponse response;
    private android.widget.EditText mEtInputApp;
    private android.widget.Button mBtnStartApp;
    private android.widget.EditText mEtInputService;
    private android.widget.Button mBtnStartService;
    private LinearLayout mRvContent;
    private android.widget.Button mBtnCancelApp;
    private android.widget.Button mBtnCloseApp;
    private android.widget.Button mBtnCancelService;
    private android.widget.Button mBtnCloseService;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_test_web_socket;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试WebSocket的长连接功能");
        mBtnStartApp = findViewById(R.id.btn_start_app);
        mBtnStartService = findViewById(R.id.btn_start_service);

        mEtInputApp = findViewById(R.id.et_input_app);
        mEtInputService = findViewById(R.id.et_input_service);

        mBtnCancelApp = findViewById(R.id.btn_cancel_app);
        mBtnCancelService = findViewById(R.id.btn_cancel_service);

        mBtnCloseApp = findViewById(R.id.btn_close_app);
        mBtnCloseService = findViewById(R.id.btn_close_service);
        mRvContent = findViewById(R.id.rv_content);

        // 客户端发送小洗
        mBtnStartApp.setOnClickListener(v -> sendApp(mEtInputApp.getText().toString()));

        // 服务端发送小洗
        mBtnStartService.setOnClickListener(v -> sendService(mEtInputService.getText().toString()));

        // 取消连接
        mBtnCancelApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelApp();
//                cancelService();
            }
        });
        mBtnCancelService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cancelApp();
                cancelService();
            }
        });

        // 关闭连接
        mBtnCloseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectApp();
                disconnectService();
            }
        });

        mBtnCloseService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectApp();
                disconnectService();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        // 初始化测试服务器
        initWebSocketService();

        // 异步获取服务器地址
        writeExecutor.execute(() -> {
            // 获取socket的地址
            String webSocketUrl = "ws://" + mMockWebServer.getHostName() + ":" + mMockWebServer.getPort() + "/";
            Message message = mSocketHandler.obtainMessage();
            message.obj = webSocketUrl;
            message.what = 1;
            mSocketHandler.sendMessage(message);
        });
    }

    class WsListener extends WebSocketListener {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);

            send2(TAG_APP, "onClosed---> App关闭了连接！---> code:" + code + "  reason:" + response);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            send2(TAG_APP, "onClosing！---> code:" + code + "  reason:" + reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            send2(TAG_APP, "onFailure ---> App连接失败！");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);

            send2(TAG_APP, "App接收到了消息：" + text);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            send2(TAG_APP, "App接收到了二进制消息：" + bytes);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            send2(TAG_APP, "App打开了长连接！");
        }
    }

    private void initWebSocketService() {
        mMockWebServer = new MockWebServer();
        response = new MockResponse()
                .withWebSocketUpgrade(new WebSocketListener() {
                    @Override
                    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                        super.onOpen(webSocket, response);
                        //有客户端连接时回调
                        mWebSocketService = webSocket;
                        send2(TAG_SERVICE, "服务器收到客户端连接成功回调");
                        send2(TAG_SERVICE, "我是服务器，你好呀");
                    }

                    @Override
                    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                        super.onMessage(webSocket, text);

                        send2(TAG_SERVICE, "服务器收到消息:" + text);
                    }

                    @Override
                    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        super.onClosed(webSocket, code, reason);
                        send2(TAG_SERVICE, "onClosed ---> 服务器断开了连接！");
                    }
                });

        mMockWebServer.enqueue(response);
    }

    //发送String消息
    public void sendApp(final String message) {
        if (mWebSocketApp != null) {
            mWebSocketApp.send(message);
        }
    }

    //发送String消息
    public void sendService(final String message) {
        if (mWebSocketService != null) {
            mWebSocketService.send(message);
        }
    }

    //发送String消息
    public void send2(String tag, final String message) {
        if (mSocketHandler != null) {
            Message message1 = mSocketHandler.obtainMessage();
            message1.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("tag", tag);
            bundle.putString("msg", message);
            message1.setData(bundle);

            mSocketHandler.sendMessage(message1);
        }
    }

    //发送byte消息
    public void send(final ByteString message) {
        if (mWebSocketApp != null) {
            mWebSocketApp.send(message);
        }
    }

    //主动断开连接
    public void disconnectApp() {
        if (mWebSocketApp != null) {
            mWebSocketApp.close(1000, "我自己想关闭的");
        }
    }

    //主动断开连接
    public void disconnectService() {
        if (mWebSocketService != null) {
            mWebSocketService.close(1000, "我自己想关闭的");
        }
    }

    //主动断开连接
    public void cancelApp() {
        if (mWebSocketApp != null) {
            mWebSocketApp.cancel();
        }
    }

    //主动断开连接
    public void cancelService() {
//        if (mWebSocketService != null) {
//            mWebSocketService.cancel();
//        }
    }

    class SocketHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:  // 获取服务器地址
                    // 获取url
                    String url = (String) msg.obj;

                    OkHttpClient mClient = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true) // 失败了自动重连
                            .pingInterval(10, TimeUnit.SECONDS)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    mWebSocketApp = mClient.newWebSocket(request, new WsListener());

                    break;
                case 2:
                    Bundle data = msg.getData();
                    String tag = data.getString("tag");
                    String message = data.getString("msg");
                    LogUtil.e(tag, message);
                    addView(tag, message);

                    break;
            }
        }
    }

    private void addView(String tag, String content) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(mContext);

        if (TextUtils.equals(tag, TAG_APP)) {
            params.gravity = Gravity.LEFT;
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue_1));
        } else if (TextUtils.equals(tag, TAG_SERVICE)) {
            params.gravity = Gravity.RIGHT;
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.green_1));
        }
        textView.setText(content);
        mRvContent.addView(textView, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocketHandler.removeCallbacksAndMessages(null);
        mSocketHandler = null;

        cancelApp();
        cancelService();

        disconnectApp();
        disconnectService();
    }
}