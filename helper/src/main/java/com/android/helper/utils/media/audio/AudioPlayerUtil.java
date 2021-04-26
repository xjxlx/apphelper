package com.android.helper.utils.media.audio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.android.helper.R;
import com.android.helper.utils.BitmapUtil;
import com.android.helper.utils.DateUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.ServiceUtil;
import com.android.helper.utils.TextViewUtil;

import java.util.List;

import static com.android.helper.utils.media.audio.AudioConstant.ACTION_LEFT;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_PAUSE;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_RIGHT;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_START;
import static com.android.helper.utils.media.audio.AudioConstant.CODE_SEND_BROADCAST_RECEIVER;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_ERROR;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_IDLE;

/**
 * 音频播放的工具类
 */
public class AudioPlayerUtil extends AudioPlayerCallBackListener {

    private AudioServiceConnection connection;
    private boolean mBindService;
    private final Context mContext;
    private Intent intent;
    @SuppressLint("StaticFieldLeak")
    private static AudioService.AudioBinder mAudioBinder;
    private BindServiceListener mBindServiceListener;
    private SeekBar mSeekBar;
    private TextView mSeekBarProgressView, mSeekBarTotalView;
    private View mStartButton; // 开始按钮
    private AudioPlayerCallBackListener mCallBackListener;
    private String mAudioPath; // 播放的路径
    private boolean mAutoPlayer;// 是否自动播放

    private int mNotificationStart;         // 消息通知栏开始的按钮
    private int mNotificationPause;         // 消息通知栏暂停的按钮
    private int mNotificationLeft;          // 消息通知栏左侧的按钮
    private int mNotificationRight;         // 消息通知栏右侧的按钮

    private String mNotificationImage;      // 消息通知栏左侧的图标
    private String mNotificationTitle;      // 消息通知栏上方的标题
    private List<AudioEntity> mAudioList;   // 消息通知栏使用到的数据列表
    private Class<? extends Activity> mPendingIntentActivity; // 点击【悬浮按钮通知】或者【锁屏通知】或者【状态栏】跳转的页面
    private NotificationUtil mNotificationUtil;
    private AudioReceiver mAudioReceiver;
    private int mAudioPosition = -1;             // 消息通知栏当前按播放音频的角标,默认的值是-1
    private RemoteViews mRemoteViews;
    private AudioService mAudioService;     // 音乐播放器的服务类

    public AudioPlayerUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 绑定服务
     */
    public void bindService(BindServiceListener bindServiceListener) {
        this.mBindServiceListener = bindServiceListener;
        LogUtil.e(AudioConstant.TAG, "bindService--->开始绑定服务！");

        intent = new Intent(mContext, AudioService.class);
        if (connection == null) {
            connection = new AudioServiceConnection();
        }

        // 启动后台服务
        try {
            ServiceUtil.startService(mContext, intent);
        } catch (Exception e) {
            LogUtil.e("开启服务失败:" + e.getMessage());
        }

        // 绑定前台的服务,禁止冲洗请的绑定
        if (!mBindService) {
            mBindService = mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        LogUtil.e(AudioConstant.TAG, "bindService--->后台服务绑定成功：" + mBindService);
    }

    /**
     * 解绑服务
     */
    public void unBindService() {
        if (mBindService) {
            mContext.unbindService(connection);
            mBindService = false;
        }
    }

    /**
     * @param audioPath 播放地址
     */
    public void setResource(String audioPath) {
        this.mAudioPath = audioPath;
        if (mAudioBinder != null) {
            mAudioBinder.setAudioResource(audioPath);
        }
    }

    /**
     * @param autoPlayer 是否自动播放
     */
    public void autoPlayer(boolean autoPlayer) {
        this.mAutoPlayer = autoPlayer;
    }

    public void start() {
        if (mAudioBinder != null) {
            mAudioBinder.start();
        }
    }

    public void pause() {
        if (mAudioBinder != null) {
            mAudioBinder.pause();
        }
    }

    public void stop() {
        if (mAudioBinder != null) {
            boolean stop = mAudioBinder.stop();
        }
    }

    /**
     * 页面停止不可见时候的处理
     */
    public void destroy() {
        if (mBindService) {
            unBindService();
        }

        // 解除注册广播接收者
        if (mAudioReceiver != null) {
            mContext.unregisterReceiver(mAudioReceiver);
            LogUtil.e("解除了动态广播的注册！");
        }

        // 停止间隔的轮询
        if (mNotificationUtil != null) {
            mNotificationUtil.stopLoopForeground();
        }

        // 停止后台的服务
        mContext.stopService(intent);
        mAudioBinder = null;
        mBindService = false;
        mBindServiceListener = null;
        mSeekBar = null;
        mSeekBarProgressView = null;
        mSeekBarTotalView = null;
        mStartButton = null;
        mCallBackListener = null;
        mAudioPath = null;
        mAutoPlayer = false;
    }

    class AudioServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.e("-----@@@@@@----> onServiceConnected!");

            if (service instanceof AudioService.AudioBinder) {
                mAudioBinder = (AudioService.AudioBinder) service;
                LogUtil.e(AudioConstant.TAG, "onServiceConnected--->服务回调成功：" + (mAudioBinder));

                // 生命周期的回调
                if (AudioPlayerUtil.this.mBindServiceListener != null) {
                    AudioPlayerUtil.this.mBindServiceListener.bindResult(mBindService);
                }

                if (mAudioBinder != null) {
                    mAudioService = mAudioBinder.getService();

                    mAudioBinder.setAudioCallBackListener(AudioPlayerUtil.this);

                    setSeekBar(mSeekBar);
                    setStartButton(mStartButton);

                    // 绑定成功后自动播放
                    if (mAutoPlayer) {
                        LogUtil.e(AudioConstant.TAG, "onServiceConnected--->服务回调成功,开始自动播放！");
                        if (!TextUtils.isEmpty(mAudioPath)) {
                            setResource(mAudioPath);
                        }
                    }

                    // 动态注册广播接收者
                    if (mAudioReceiver == null) {
                        mAudioReceiver = new AudioReceiver();
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(AudioConstant.ACTION_START);
                        intentFilter.addAction(AudioConstant.ACTION_PAUSE);
                        intentFilter.addAction(AudioConstant.ACTION_LEFT);
                        intentFilter.addAction(AudioConstant.ACTION_RIGHT);
                        // 注册
                        mContext.registerReceiver(mAudioReceiver, intentFilter);
                    }

                    // 在数据回调成功的时候去创建消息通知工具
                    try {
                        if (mNotificationUtil == null) {
                            mNotificationUtil = NotificationUtil.getInstance(mContext);
                            mNotificationUtil
                                    .setTickerText("首次出现在通知栏")
                                    .setContentTitle("消息通知栏")
                                    .setContentText("消息的内容")
                                    .setSmallIcon(R.drawable.icon_left_right)
                                    .setLockScreenVisibility(true)
                                    .setNotificationLevel(Notification.PRIORITY_DEFAULT)
                                    .setActivity(mPendingIntentActivity)
                                    .setRemoteView(R.layout.notification_audio, (view, remoteViews) -> {
                                        if (remoteViews != null) {
                                            AudioPlayerUtil.this.mRemoteViews = remoteViews;

                                            // 左侧的按钮
                                            if (mNotificationLeft != 0) {
                                                remoteViews.setImageViewResource(R.id.iv_to_left, mNotificationLeft);
                                            }

                                            // 中间的按钮
                                            if (mAudioBinder.isPlaying()) {
                                                if (mNotificationPause != 0) {
                                                    remoteViews.setImageViewResource(R.id.iv_start, mNotificationPause);
                                                }
                                            } else {
                                                if (mNotificationStart != 0) {
                                                    remoteViews.setImageViewResource(R.id.iv_start, mNotificationStart);
                                                }
                                            }

                                            // 右侧的按钮
                                            if (mNotificationRight != 0) {
                                                remoteViews.setImageViewResource(R.id.iv_to_right, mNotificationRight);
                                            }

                                            // 播放按钮点击事件的处理
                                            Intent intentStart = new Intent();
                                            intentStart.setAction(ACTION_PAUSE);
                                            intentStart.setAction(ACTION_START);
                                            PendingIntent btPendingIntentStart = PendingIntent.getBroadcast(mContext, CODE_SEND_BROADCAST_RECEIVER, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
                                            remoteViews.setOnClickPendingIntent(R.id.iv_start, btPendingIntentStart);

                                            // 左侧按钮点击事件的处理
                                            Intent intentLeft = new Intent();
                                            intentLeft.setAction(ACTION_LEFT);
                                            PendingIntent btPendingIntentLeft = PendingIntent.getBroadcast(mContext, CODE_SEND_BROADCAST_RECEIVER, intentLeft, PendingIntent.FLAG_UPDATE_CURRENT);
                                            remoteViews.setOnClickPendingIntent(R.id.iv_to_left, btPendingIntentLeft);

                                            // 左侧按钮点击事件的处理
                                            Intent intentRight = new Intent();
                                            intentRight.setAction(ACTION_RIGHT);
                                            PendingIntent btPendingIntentRight = PendingIntent.getBroadcast(mContext, CODE_SEND_BROADCAST_RECEIVER, intentRight, PendingIntent.FLAG_UPDATE_CURRENT);
                                            remoteViews.setOnClickPendingIntent(R.id.iv_to_right, btPendingIntentRight);
                                        }
                                    })
                                    .createNotification()
                                    .sendNotification(1)
                                    .startForeground(1, mAudioService);
                        }
                    } catch (Exception e) {
                        LogUtil.e("------------->:" + e.getMessage());
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e("-----@@@@@@----> onServiceDisconnected!");
        }
    }

    /**
     * @param callBackListener 数据回调
     */
    public void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener) {
        if (callBackListener != null) {
            this.mCallBackListener = callBackListener;
        }
    }

    public void setSeekBar(SeekBar seekBar) {
        if (seekBar == null) {
            return;
        }
        this.mSeekBar = seekBar;

        // 设置默认的进度
        seekBar.setProgress(0);

        if (mAudioBinder == null) {
            return;
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MediaPlayer mediaPlayer = mAudioBinder.getMediaPlayer();
                    if (mediaPlayer != null) {
                        int status = mAudioBinder.getStatus();
                        if ((status != STATUS_IDLE) && (status != STATUS_ERROR)) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mAudioBinder.sendProgress(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAudioBinder.sendProgress(true);
            }
        });
    }

    /**
     * @param progressTimeView 设置SeekBar不停变换进度的view
     */
    public void setSeekBarProgressTime(TextView progressTimeView) {
        this.mSeekBarProgressView = progressTimeView;

        // 设置默认的进度
        TextViewUtil.setText(mSeekBarProgressView, "00:00");
    }

    /**
     * @param totalTimeView 设置SeekBar固定不变的view
     */
    public void setSeekBarTotalTime(TextView totalTimeView) {
        if (totalTimeView == null) {
            return;
        }
        this.mSeekBarTotalView = totalTimeView;

        // 设置默认的进度
        TextViewUtil.setText(mSeekBarTotalView, "00:00");
    }

    /**
     * @param view 设置开关按钮的变换
     */
    public void setStartButton(View view) {
        if (view == null) {
            return;
        }
        this.mStartButton = view;
        if (mAudioBinder == null) {
            return;
        }

        // 播放按钮的点击事件
        view.setOnClickListener(v -> {
            boolean initialized = mAudioBinder.initialized();
            if (initialized) {
                if (mAudioBinder.isPlaying()) {
                    pause();
                } else {
                    start();
                }
            } else {
                setResource(mAudioPath);
            }
        });
    }

    public void switchStartButton(boolean selector) {
        if (mStartButton != null) {
            mStartButton.setSelected(selector);
        }
    }

    @Override
    public void onBufferProgress(int total, double current, int percent) {
        LogUtil.e("onBufferProgress:-->total:" + total + "  --->current:" + current + " --->percent:" + percent);
        if (mSeekBar != null) {
            mSeekBar.setMax(total);
            mSeekBar.setSecondaryProgress((int) current);
        }

        if (total > 0) {
            // 设置总的进度
            if (mSeekBarTotalView != null) {
                CharSequence totalContent = mSeekBarTotalView.getText();
                if (TextUtils.isEmpty(totalContent)) {
                    CharSequence charSequence = DateUtil.formatMillis(total);
                    TextViewUtil.setText(mSeekBarTotalView, charSequence);
                }
                // 设置默认的进度
                if (mSeekBarProgressView != null) {
                    CharSequence text = mSeekBarProgressView.getText();
                    if (TextUtils.isEmpty(text)) {
                        int length = totalContent.length();
                        if (length == 3) {
                            TextViewUtil.setText(mSeekBarProgressView, "00:00:00");
                        } else {
                            TextViewUtil.setText(mSeekBarProgressView, "00:00");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onProgress(int total, int current, String percent) {
        if (mSeekBar != null) {
            mSeekBar.setMax(total);
            mSeekBar.setProgress(current);
        }

        // 设置总的进度
        if (mSeekBarTotalView != null && total > 0) {
            CharSequence charSequence = DateUtil.formatMillis(total);
            TextViewUtil.setText(mSeekBarTotalView, charSequence);
        }

        // 设置默认的进度
        if (current > 0) {
            CharSequence charSequence = DateUtil.formatMillis(current);
            TextViewUtil.setText(mSeekBarProgressView, charSequence);
        }

        // 更正按钮的转改,加到这个地方，更加靠谱
        switchStartButton(true);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        LogUtil.e("onPrepared");
        if (mCallBackListener != null) {
            mCallBackListener.onPrepared();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("onStart");

        switchStartButton(true);
        if (mCallBackListener != null) {
            mCallBackListener.onStart();
        }

        // 获取当前view的角标
        mAudioPosition = getCurrentPositionForUrl();

        // 更改view的图标
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.iv_start, mNotificationPause);

            if (!TextUtils.isEmpty(mNotificationImage)) {
                // 左侧图片的资源
                BitmapUtil.getBitmapForService(mContext, mNotificationImage, (successful, tag, bitmap) -> {
                    if (tag != null && tag.equals(BitmapUtil.STATUS_SUCCESS)) {
                        mRemoteViews.setImageViewBitmap(R.id.iv_launcher, bitmap);
                    }
                });
            }

            // 中间的标题头
            if (!TextUtils.isEmpty(mNotificationTitle)) {
                mRemoteViews.setTextViewText(R.id.tv_title, mNotificationTitle);
                mRemoteViews.setTextColor(R.id.tv_title, Color.BLACK);
                mRemoteViews.setTextViewTextSize(R.id.tv_title, TypedValue.COMPLEX_UNIT_SP, 16);
            }
        }

        // 发送间隔的轮询
        if (mNotificationUtil != null) {
            mNotificationUtil.startLoopForeground(1, 5000, mAudioService);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.e("onPause");
        switchStartButton(false);
        if (mCallBackListener != null) {
            mCallBackListener.onPause();
        }

        if (mRemoteViews != null) {
            if (mNotificationStart != 0) {
                mRemoteViews.setImageViewResource(R.id.iv_start, mNotificationStart);
            }
        }

        // 发送间隔的轮询
        if (mNotificationUtil != null) {
            mNotificationUtil.startForeground(1, mAudioService);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.e("onStop");
        switchStartButton(false);

        if (mCallBackListener != null) {
            mCallBackListener.onStop();
        }
    }

    @Override
    public void onError(Exception e) {
        super.onError(e);
        LogUtil.e("onError");
        switchStartButton(false);
        if (mCallBackListener != null) {
            mCallBackListener.onError(e);
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        LogUtil.e("onComplete");
        switchStartButton(false);
        if (mCallBackListener != null) {
            mCallBackListener.onComplete();
        }
    }

    /**
     * @param notificationStart notification开始的按钮
     * @param notificationPause notification暂停的按钮
     * @param notificationLeft  notification左侧的按钮
     * @param notificationRight notification右侧的按钮
     */
    public void setNotificationIcon(@DrawableRes int notificationStart, @DrawableRes int notificationPause, @DrawableRes int notificationLeft, @DrawableRes int notificationRight) {
        mNotificationStart = notificationStart;
        mNotificationPause = notificationPause;
        mNotificationLeft = notificationLeft;
        mNotificationRight = notificationRight;
    }

    /**
     * @param notificationImage notification左侧的图标
     * @param notificationTitle notification的标题
     */
    public void setNotificationMessage(String notificationImage, String notificationTitle) {
        mNotificationImage = notificationImage;
        mNotificationTitle = notificationTitle;
    }

    public void setNotificationList(List<AudioEntity> list) {
        mAudioList = list;
    }

    public void setPendingIntentActivity(Class<? extends Activity> cls) {
        this.mPendingIntentActivity = cls;
    }

    public class AudioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((intent != null) && (mAudioBinder != null)) {
                String action = intent.getAction();
                boolean playing = mAudioBinder.isPlaying();
                LogUtil.e("-------------------------------->AudioReceiver ---> onReceive:" + action + "   --->player:" + playing);

                switch (action) {
                    case ACTION_START:
                    case ACTION_PAUSE:

                        if (mAudioBinder.initialized()) {
                            if (playing) {
                                pause();
                            } else {
                                start();
                            }
                        } else {
                            setResource(mAudioPath);
                        }

                        break;

                    case ACTION_LEFT:
                        onPage();
                        break;

                    case ACTION_RIGHT:
                        nextPage();
                        break;

                }
            }
        }
    }

    /**
     * 下一曲
     */
    public void nextPage() {
        LogUtil.e("播放下一首的方法");
        if ((mAudioList != null) && (mAudioList.size() > 0)) {
            if (mAudioPosition != -1) {
                if (mAudioPosition < mAudioList.size() - 1) {
                    mAudioPosition += 1;
                } else { //无限循环
                    mAudioPosition = 0;
                }

                AudioEntity audioEntity = mAudioList.get(mAudioPosition);
                if (audioEntity != null) {
                    String url = audioEntity.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        mAudioPath = url;
                        setResource(mAudioPath);
                    }
                }

            } else {
                LogUtil.e("nextPage--->角标异常,暂停播放！");
            }
        } else {
            LogUtil.e("播放下一首的方法--->集合为空");
        }
    }

    /**
     * 上一曲
     */
    public void onPage() {
        LogUtil.e("播放上一首的方法");
        if ((mAudioList != null) && (mAudioList.size() > 0)) {
            if (mAudioPosition != -1) {
                if (mAudioPosition > 0) {
                    mAudioPosition -= 1;
                } else { //无限循环
                    mAudioPosition = mAudioList.size() - 1;
                }

                AudioEntity audioEntity = mAudioList.get(mAudioPosition);
                if (audioEntity != null) {
                    String url = audioEntity.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        mAudioPath = url;
                        setResource(mAudioPath);
                    }
                }

            } else {
                LogUtil.e("onPage--->角标异常,暂停播放！");
            }
        } else {
            LogUtil.e("播放上一首的方法--->集合为空");
        }
    }

    /**
     * @return 获取当前音频播放对应的角标
     */
    public int getCurrentPositionForUrl() {
        mAudioPosition = -1;
        if ((mAudioList != null) && (mAudioList.size() > 0)) {
            for (int i = 0; i < mAudioList.size(); i++) {
                AudioEntity audioEntity = mAudioList.get(i);
                if (audioEntity != null) {
                    String ur = audioEntity.getUrl();
                    if (TextUtils.equals(ur, mAudioPath)) {
                        mAudioPosition = i;
                        return mAudioPosition;
                    }
                }
            }
        }
        LogUtil.e("当前的角标为：" + mAudioPosition);
        return mAudioPosition;
    }
}
