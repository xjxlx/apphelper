package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.helper.R;
import com.android.helper.interfaces.listener.ViewCallBackListener;

/**
 * 消息的管理类，使用的时候，配合service一块使用
 * 使用过方法：
 * 1：先创建notification,调用方法：createNotification（）
 * 2：发送消息，可以选择三种情况，sendNotification（）发送单个消息，startForeground（），发送一个前台的消息，
 * startLoopForeground（） 发送轮询的消息
 */
public class NotificationUtil {

    /**
     * 点击通知时候跳转的请求码
     */
    public static final int CODE_JUMP_REQUEST = 1000;

    /**
     * handler的单独发送
     */
    public static final int CODE_WHAT_SEND_START_FOREGROUND = CODE_JUMP_REQUEST + 1;
    /**
     * handler的轮询发送
     */
    public static final int CODE_WHAT_SEND_START_FOREGROUND_LOOP = CODE_WHAT_SEND_START_FOREGROUND + 1;
    /**
     * 跳转activity的请求码
     */
    public static final int CODE_REQUEST_ACTIVITY = CODE_WHAT_SEND_START_FOREGROUND_LOOP + 1;

    private final Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static NotificationUtil util;
    private Intent mIntentActivity;
    private Intent mIntentService;

    // 消息对象
    private Notification mNotification;
    private String mTickerText;     //  消息设置首次出现的名字
    private String mContentTitle;          // 消息标题头
    private String mContentText;           // 消息内容
    private int mContentSmallIcon;         // 消息的图标
    private int mNotificationNumber;       // 消息的数量
    private int mNotificationLevel = -100;        // 消息的等级
    private PendingIntent pendingIntent;

    /**
     * 自定义震动
     * <p>
     * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
     * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
     */
    private final long[] vibrates = {0, 1000};

    private String mChannelDescription;                 // 渠道的描述
    private String mChannelName;                        // 渠道的名字
    private int mChannelImportance;                     // 渠道的等级,默认是等级3，会提示声音

    private NotificationManager manager;
    private int mRemoteViewsLayout;                     // 状态栏布局
    private Service mService;                           // 服务类
    private ViewCallBackListener<RemoteViews> mViewCallBackListener;
    private long mIntervalTime;                         // 轮询的间隔
    private boolean mVibrate;                           // 震动
    private boolean mSound = true;                             // 是否发出声音，默认发出

    private NotificationUtil(Context context) {
        this.mContext = context;
        if (mContext != null) {
            // 创建管理器
            manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public static NotificationUtil getInstance(Context context) {
        if (util == null) {
            util = new NotificationUtil(context);
        }
        return util;
    }

    public NotificationUtil setActivity(Class<? extends Activity> activityCls) {
        mIntentActivity = new Intent(mContext, activityCls);
        mIntentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return util;
    }

    public NotificationUtil setService(Class<? extends Service> serviceCls) {
        mIntentService = new Intent(mContext, serviceCls);
        mIntentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return util;
    }

    /**
     * 通知首次出现在通知栏，带上升动画效果的，可设置文字，图标
     */
    public NotificationUtil setTickerText(String tickerText) {
        //通知首次出现在通知栏，带上升动画效果的，可设置文字，图标
        this.mTickerText = tickerText;
        return util;
    }

    /**
     * 设置标题头
     *
     * @param title 标题头
     */
    public NotificationUtil setContentTitle(String title) {
        this.mContentTitle = title;
        return util;
    }

    public NotificationUtil setContentText(String text) {
        this.mContentText = text;
        return util;
    }

    public NotificationUtil setSmallIcon(@DrawableRes int icon) {
        this.mContentSmallIcon = icon;
        return util;
    }

    /**
     * @param description 渠道的描述
     * @return 可见的渠道的描述
     */
    public NotificationUtil setChannelDescription(String description) {
        this.mChannelDescription = description;
        return util;
    }

    /**
     * @param channelName 渠道的名字
     * @return 设置渠道的名字
     */
    public NotificationUtil setChannelName(String channelName) {
        this.mChannelName = channelName;
        return util;
    }

    /**
     * @param importance * IMPORTANCE_NONE 关闭通知
     *                   * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
     *                   * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
     *                   * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
     *                   * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
     * @return 设置渠道的等级，
     */
    public NotificationUtil setChannelImportance(int importance) {
        this.mChannelImportance = importance;
        return util;
    }

    /**
     * 设置声音，使用这个方法的话，必须在资源目录下设置一个raw的目录，资源设置的话，也需要R.raw.xx,例如： R.raw.tougong
     *
     * @param sound 声音的路径，使用
     */
    public NotificationUtil setSound(@DrawableRes int sound) {
        if (mContext != null) {
            // 自定义声音
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + sound);
        }
        return util;
    }

    /**
     * @param sound true：发出声音提示，fasle:不发出声音提示
     * @return 是否震动，默认发出声音提示
     */
    public NotificationUtil setSound(boolean sound) {
        this.mSound = sound;
        return util;
    }

    public NotificationUtil setNumber(int number) {
        this.mNotificationNumber = number;
        return util;
    }

    /**
     * @param level 消息通知的等级，7.0以下使用 {@link Notification#PRIORITY_HIGH } ，7.0以上使用 {@link NotificationManager#IMPORTANCE_HIGH }去设置
     * @RequiresApi(api = Build.VERSION_CODES.N)  7.0 以上使用
     */
    public NotificationUtil setNotificationLevel(int level) {
        this.mNotificationLevel = level;
        return util;
    }

    /**
     * @return 设置是否震动
     */
    public NotificationUtil setVibrate(boolean vibrate) {
        this.mVibrate = vibrate;
        return util;
    }

    /**
     * @param layoutId 布局的资源
     * @return 设置消息的通知栏布局
     */
    public <T> NotificationUtil setRemoteView(int layoutId, ViewCallBackListener<RemoteViews> callBackListener) {
        this.mRemoteViewsLayout = layoutId;
        this.mViewCallBackListener = callBackListener;
        return util;
    }

    /**
     * 创建消息的对象
     */
    public NotificationUtil createNotification() {
        if (mContext != null) {
            // 跳转的activity意图
            if (mIntentActivity != null) {
                pendingIntent = PendingIntent.getActivity(mContext, CODE_JUMP_REQUEST, mIntentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            // 跳转的服务类
            if (mIntentService != null) {
                pendingIntent = PendingIntent.getService(mContext, CODE_JUMP_REQUEST, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            // 使用用户的包名作为渠道的id，保证唯一性
            String channelId = mContext.getPackageName();

            // 渠道名字
            if (TextUtils.isEmpty(mChannelName)) {
                // 使用app的名字作为渠道的名字，用户可以看到的通知渠道的名字.
                mChannelName = mContext.getResources().getString(R.string.app_name);
            }

            // 构建消息通知的对象
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId);

            // 首次出现的提示
            if (!TextUtils.isEmpty(mTickerText)) {
                builder.setTicker(mTickerText);
            }

            // 消息的标题
            if (!TextUtils.isEmpty(mContentTitle)) {
                builder.setContentTitle(mContentTitle);
            }

            // 消息的内容
            if (!TextUtils.isEmpty(mContentText)) {
                builder.setContentText(mContentText);
            }

            // 消息的图标
            if (mContentSmallIcon != 0) {
                builder.setSmallIcon(mContentSmallIcon);
            }

            // 消息的声音、灯光、震动
            if (mSound) {
                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            }

            // 设置消息的数量
            if (mNotificationNumber > 0) {
                builder.setNumber(mNotificationNumber);
            }

            //点击之后的页面
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent);
            }

            // 横幅通知
            if (pendingIntent != null) {
                builder.setFullScreenIntent(pendingIntent, true);
            }

            // 设置通知的等级
            if (mNotificationLevel == -100) {
                mNotificationLevel = NotificationCompat.PRIORITY_DEFAULT;
            }

            // 当SDK大于16且小于26的时候
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // 锁屏可见
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                mNotification = builder
                        .setPriority(mNotificationLevel)// 设置优先级
                        .build();
            } else {    // 当SDK大于26的时候
                // 渠道的等级
                if (mChannelImportance == 0) {
                    mChannelImportance = NotificationManager.IMPORTANCE_MAX;
                }

                // 渠道对象
                NotificationChannel mChannel = new NotificationChannel(channelId, mChannelName, mChannelImportance);

                if (TextUtils.isEmpty(mChannelDescription)) {
                    // 默认的渠道描述
                    mChannelDescription = mChannelName + "的渠道描述";
                }

                // 消息通知的描述 , 用户可以看到的通知渠道的描述
                mChannel.setDescription(mChannelDescription);

                // 设置通知出现时的闪灯（如果 android 设备支持的话）
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);

                // 设置通知出现时的震动（如果 android 设备支持的话）
                if (mVibrate) {
                    mChannel.enableVibration(true);
                    mChannel.setShowBadge(true);//显示logo
                    mChannel.setVibrationPattern(vibrates);
                }

                // 锁屏可见
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见

                // 设置声音
                if (mSound) {
                    mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), Notification.AUDIO_ATTRIBUTES_DEFAULT);
                }

                // 通知Manager去创建渠道
                manager.createNotificationChannel(mChannel);

                mNotification = builder
                        .setPriority(mNotificationLevel)// 设置优先级
                        .setChannelId(channelId)// 设置渠道
                        .build();
            }

            // 通知栏的状态布局
            if (mRemoteViewsLayout != 0) {
                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), mRemoteViewsLayout);
                mNotification.contentView = remoteViews;
                // 把布局回调回去
                mViewCallBackListener.callBack(null, remoteViews);
            }
        }
        return util;
    }

    /**
     * @param id 消息的id
     * @return 发送一个消息
     */
    public NotificationUtil sendNotification(int id) {
        // 发送消息通知
        if ((manager != null) && (mNotification != null)) {
            manager.notify(id, mNotification);
        }
        return util;
    }

    public Notification getNotification() {
        return mNotification;
    }

    /**
     * @param service 指定的服务类型
     * @return 开启前台服务
     */
    public NotificationUtil startForeground(int id, Service service) {
        if (service != null) {
            if (id > 0) {
                this.mService = service;

                Message message = mHandler.obtainMessage();
                message.what = CODE_WHAT_SEND_START_FOREGROUND;
                message.arg1 = id;
                mHandler.sendMessage(message);
            } else {
                LogUtil.e("发送通知的id不能为0！");
            }
        }
        return util;
    }

    /**
     * 开始轮询的发送服务的通知，避免间隔的时间长了，服务被误判，停止联网的操作
     *
     * @param intervalTime 每次间隔的时间
     * @param service      指定的服务
     */
    @SuppressLint("CheckResult")
    public void startLoopForeground(int id, long intervalTime, Service service) {
        if (service != null) {
            if (id > 0) {
                this.mService = service;
                this.mIntervalTime = intervalTime;

                Message message = mHandler.obtainMessage();
                message.what = CODE_WHAT_SEND_START_FOREGROUND_LOOP;
                message.arg1 = id;
                mHandler.sendMessage(message);
            } else {
                LogUtil.e("发送通知的id不能为0！");
            }
        }
    }

    /**
     * 停止轮询服务的发送
     */
    public void stopLoopForeground() {
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.e("停止了轮训消息的发送！");
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 先停止之前的消息发送，避免数据的快速轮询
            stopLoopForeground();

            if ((mService != null) && (mNotification != null)) {
                int id = msg.arg1;

                switch (msg.what) {
                    case CODE_WHAT_SEND_START_FOREGROUND:
                        LogUtil.e("开始了服务消息的单独发送！");
                        mService.startForeground(id, mNotification);
                        // sendNotification(id);
                        break;

                    case CODE_WHAT_SEND_START_FOREGROUND_LOOP:
                        LogUtil.e("开始了服务消息的轮询发送！");
                        mService.startForeground(id, mNotification);
                        //  sendNotification(id);

                        Message message = mHandler.obtainMessage();
                        message.what = CODE_WHAT_SEND_START_FOREGROUND_LOOP;
                        message.arg1 = id;
                        mHandler.sendMessageDelayed(message, mIntervalTime);
                        break;
                }
            }

        }
    };

    /**
     * @return 检测是否已经打开了通知的权限
     */
    public boolean checkOpenNotify(Context context) {
        boolean isOpened = false;
        try {
            NotificationManagerCompat from = NotificationManagerCompat.from(context);
            isOpened = from.areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpened;
    }

    /**
     * 跳转通知的设置页面
     */
    public void goToSetNotify(Activity context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 26) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getApplicationContext().getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        context.startActivityForResult(intent, CODE_REQUEST_ACTIVITY);
    }

}
