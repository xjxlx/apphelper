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
import com.android.common.utils.LogUtil;
import com.android.helper.R;
import com.android.helper.interfaces.listener.ViewCallBackListener;

/**
 * <ol>
 * <p>
 * RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
 * Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_summary_notice);
 * Intent intent = new Intent(mContext, IconifyActivity.class);
 * PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
 * NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
 *       .setTicker("ticker")                //新来通知时在状态栏现实的文本
 *       .setContentTitle("content title")  //设置通知标题
 *       .setContentText("content text")        //设置通知文本
 *       .setSmallIcon(R.drawable.ic_notice_1)  //设置通知左上角小图标
 *       .setContentIntent(pendingIntent)    //设置点击通知的操作
 *       .setDeleteIntent(pendingIntent)        //设置删除通知时的操作
 *       .setWhen(System.currentTimeMillis()) //设置通知上的时间戳
 *       .setProgress(0, 0, true)   //设置进度条
 *       .setContent(remoteView)                //设置通知使用自定义的视图，而非系统默认视图
 *       .setStyle(new NotificationCompat.BigTextStyle())   //设置通知样式，主要包括默认样式、BigTextStyle、BigPictureStyle和InboxStyle
 *       .setLargeIcon(mBitmap)          //设置通知大图标
 *       .setAutoCancel(true)                //设置用户点击通知后是否自动清除通知，true：清除；false：不清
 *       .setNumber(count)                    //设置通知右下角显示的数字
 *       .setDefaults(NotificationCompat.DEFAULT_ALL)    //设置应用于通知上的默认动作，如声音、三色灯、振动等
 *       .setLights(0x0000ff, 300, 300)//设置通知三色灯
 *       .setSound(Uri.parse("file:///sdcard/xx/xx.mp3"))    //自定义通知提示音
 *       .setVibrate(new long[]{0, 300, 500, 700})    //自定义振动
 *       .setOngoing(false)                    //设置是否为一个后台任务，默认为否；true表示是一个正在进行的后台任务，如音乐播放、文件下载、数据同步等
 *       .setPriority(NotificationCompat.PRIORITY_DEFAULT)    //设置该通知相对重要性
 *       .setColor(0x00ff00)                    //设置通知颜色
 *       .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)   //设置通知的可见性，取值为VISIBILITY_PRIVATE（默认）、VISIBILITY_PUBLIC、VISIBILITY_SECRET中的一种
 *       .setContentInfo("content info")        //设置通知右侧的大文本内容
 *       .setFullScreenIntent(pendingIntent, false)    //设置一个直接全屏加载的动作，而不是发送通知至通知栏
 *       .setExtras(new Bundle())            //设置通知的元信息
 *       .setCategory(NotificationCompat.CATEGORY_MESSAGE)    //设置通知所属类别
 *       .setGroupSummary(false)                //设置该通知为一个通知组中的摘要通知
 *       .setGroup(NotificationCompat.CATEGORY_CALL)    //设置该通知为分享同一通知键值的通知组中的一部分
 *       .setUsesChronometer(false)            //使用计时器而非时间戳来显示时间
 *       .setSubText("sub text")                //设置扩展视图中的子文本内容
 *       .setLocalOnly(false)                //设置该通知是否只在当前设备上显示，默认为否
 *       .setOnlyAlertOnce(false)            //设置是否只提示一次
 *       .setSortKey("sort key");                //设置针对一个包内的通知进行排序的键值
 * </ol>
 * <p>
 * 消息的管理类，使用的时候，配合service一块使用
 * 使用过方法：
 * 1：先创建notification,调用方法：createNotification（）
 * 2：发送消息，可以选择三种情况，sendNotification（）发送单个消息，startForeground（），发送一个前台的消息，
 * startLoopForeground（） 发送轮询的消息
 * <p>
 * 消息的管理类：
 *  使用方法：
 *          1：通过builder设置所有的参数
 *          2：创建通过Builder创建Notification的对象
 *          3：发送单个的普通消息，调用{@link NotificationUtil#sendNotification(int)}方法
 *          4：发送轮询的普通消息，调用{@link NotificationUtil#startLoopNotification(int, long)}方法
 *          5：发送前台的服务消息，调用{@link NotificationUtil#startForeground(int, Service)}方法
 *          6：发送前台的轮询服务消息，调用{@link NotificationUtil#startLoopForeground(int, long, Service)}方法
 *          7：停止所有的消息，调用{@link NotificationUtil#stopAllLoop()}方法
 *          8：停止单个的消息，调用{@link NotificationUtil#stopNotification(int)}方法
 *          9：检测是否打开了通知的功能，调用{@link NotificationUtil#checkOpenNotify(Context)}方法
 *          10：跳转到通知的页面，调用{@link NotificationUtil#goToSetNotify(Activity)}方法，由于手机型号不同，可能会跳转失败
 *          11：跳转到渠道通知的页面，调用{@link NotificationUtil#openChannelNotification()}方法，由于手机型号不同，可能会跳转失败
 * <p>
 *  注意：如果要开启前台服务，需要添加权限
 *       <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
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
     * handler的轮询发送 -- notification
     */
    public static final int CODE_WHAT_SEND_START_NOTIFICATION_LOOP = CODE_WHAT_SEND_START_FOREGROUND_LOOP + 1;
    /**
     * 跳转activity的请求码
     */
    public static final int CODE_REQUEST_ACTIVITY_NOTIFICATION = CODE_WHAT_SEND_START_NOTIFICATION_LOOP + 1;

    private NotificationManager manager;
    private Context mContext;
    private Intent mIntentActivity;
    private Intent mIntentService;
    private String mTickerText;            //  消息设置首次出现的名字
    private String mContentTitle;          // 消息标题头
    private int mContentSmallIcon;         // 消息的图标
    private String mContentText;           // 消息内容
    private int mNotificationNumber;       // 消息的数量
    private int mNotificationLevel;        // 消息的等级
    private PendingIntent pendingIntent;
    private long[] vibrates;
    private String mChannelDescription;                 // 渠道的描述
    private String mChannelName;                        // 渠道的名字
    private int mChannelImportance;                     // 渠道的等级,默认是等级3，会提示声音
    private int mRemoteViewsLayout;                     // 状态栏布局
    private long mIntervalTime;                         // 轮询的间隔
    private boolean mVibrate;                           // 震动
    private boolean mSound;                             // 是否发出声音，默认发出
    private Service mService;                           // 服务类
    private long mWhen;                                 // 消息出现的时间戳
    private boolean mAutoCancel;                        // 手动取消
    private OnHandlerLoopListener mOnHandlerLoopListener; // handler轮询的监听器

    private ViewCallBackListener<RemoteViews> mViewCallBackListener;
    // 消息对象
    private Notification mNotification;
    private NotificationCompat.Builder mNotificationBuilder;
    private Builder mBuilder;

    private NotificationUtil(Builder builder) {
        if (builder != null) {
            mBuilder = builder;
            this.mContext = builder.mContext;
            this.manager = builder.manager;
            this.mIntentActivity = builder.mIntentActivity;
            this.mIntentService = builder.mIntentService;
            this.mTickerText = builder.mTickerText;
            this.mContentTitle = builder.mContentTitle;
            this.mContentText = builder.mContentText;
            this.mContentSmallIcon = builder.mContentSmallIcon;
            this.mNotificationNumber = builder.mNotificationNumber;
            this.mNotificationLevel = builder.mNotificationLevel;
            this.vibrates = builder.vibrates;
            this.mChannelDescription = builder.mChannelDescription;
            this.mChannelName = builder.mChannelName;
            this.mChannelImportance = builder.mChannelImportance;
            this.mRemoteViewsLayout = builder.mRemoteViewsLayout;
            this.mViewCallBackListener = builder.mViewCallBackListener;
            this.mVibrate = builder.mVibrate;
            this.mSound = builder.mSound;
            this.mWhen = builder.mWhen;
            this.mAutoCancel = builder.autoCancel;
            this.mOnHandlerLoopListener = builder.mOnHandlerLoopListener;
        }
        createNotification();
    }

    /**
     * 创建消息的对象
     */
    private void createNotification() {
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
                mChannelName = mContext
                        .getResources()
                        .getString(R.string.app_name);
            }
            // 构建消息通知的对象
            mNotificationBuilder = new NotificationCompat.Builder(mContext, channelId);
            if (mWhen != 0) {
                mNotificationBuilder.setWhen(mWhen);
            }
            // 是否可以手动取消
            if (!mAutoCancel) {
                // 禁止自动取消
                mNotificationBuilder.setAutoCancel(false);
                mNotificationBuilder.setOngoing(true);
            } else {
                mNotificationBuilder.setAutoCancel(true);
            }
            // 首次出现的提示
            if (!TextUtils.isEmpty(mTickerText)) {
                mNotificationBuilder.setTicker(mTickerText);
            }
            // 消息的标题
            if (!TextUtils.isEmpty(mContentTitle)) {
                mNotificationBuilder.setContentTitle(mContentTitle);
            }
            // 消息的内容
            if (!TextUtils.isEmpty(mContentText)) {
                mNotificationBuilder.setContentText(mContentText);
            }
            // 消息的图标
            if (mContentSmallIcon != 0) {
                mNotificationBuilder.setSmallIcon(mContentSmallIcon);
            }
            // 消息的声音、灯光、震动
            if (mSound) {
                mNotificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            }
            // 设置消息的数量
            if (mNotificationNumber > 0) {
                mNotificationBuilder.setNumber(mNotificationNumber);
            }
            //点击之后的页面
            if (pendingIntent != null) {
                mNotificationBuilder.setContentIntent(pendingIntent);
            }
            // 横幅通知
            if (pendingIntent != null) {
                mNotificationBuilder.setFullScreenIntent(pendingIntent, true);
            }
            // 锁屏可见
            mNotificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            // 当SDK大于16且小于26的时候
            // 渠道对象
            NotificationChannel mChannel = new NotificationChannel(channelId, mChannelName, mChannelImportance);
            // 默认的渠道描述
            if (TextUtils.isEmpty(mChannelDescription)) {
                mChannelDescription = mChannelName + "的渠道";
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
            mNotification = mNotificationBuilder.setPriority(mNotificationLevel)// 设置优先级
                    .setChannelId(channelId)// 设置渠道
                    .build();
            // 通知栏的状态布局
            if (mRemoteViewsLayout != 0) {
                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), mRemoteViewsLayout);
                mNotification.contentView = remoteViews;
                // 把布局回调回去
                mViewCallBackListener.callBack(remoteViews);
            }
        }
    }

    /**
     * @return 获取builder
     */
    public NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    public Notification getNotification() {
        return mNotification;
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
        return this;
    }

    /**
     * <ol>
     *     注意：如果要开启前台服务，需要添加权限
     *     <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
     * </ol>
     *
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
        return this;
    }

    /**
     * <ol>
     *     注意：如果要开启前台服务，需要添加权限
     *     <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
     * </ol>
     * 开始轮询的发送服务的通知，避免间隔的时间长了，服务被误判，停止联网的操作
     *
     * @param intervalTime 每次间隔的时间
     * @param service      指定的服务
     */
    @SuppressLint("CheckResult")
    public NotificationUtil startLoopForeground(int id, long intervalTime, Service service) {
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
        return this;
    }

    public NotificationUtil startLoopNotification(int id, long intervalTime) {
        if (id > 0) {
            this.mIntervalTime = intervalTime;
            Message message = mHandler.obtainMessage();
            message.what = CODE_WHAT_SEND_START_NOTIFICATION_LOOP;
            message.arg1 = id;
            mHandler.sendMessage(message);
        } else {
            LogUtil.e("发送通知的id不能为0！");
        }
        return this;
    }

    /**
     * 停止所有的发送
     */
    public void stopAllLoop() {
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.e("停止了轮训消息的发送！");
    }

    /**
     * @return 取消指定的通知
     */
    public NotificationUtil stopNotification(int id) {
        if (manager != null) {
            manager.cancel(id);
        }
        return this;
    }

    /**
     * @return 取消指定的通知，如果使用了这个方法，就必须先调用一下{@link NotificationUtil#stopAllLoop()}
     * 避免因为轮询造成的再次启动
     */
    public NotificationUtil stopForeground() {
        if (mService != null) {
            mService.stopForeground(true);
        }
        return this;
    }

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
     * 跳转通知的设置页面，startActivityForResult的请求码为{CODE_REQUEST_ACTIVITY_NOTIFICATION}
     */
    public void goToSetNotify(Activity context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 26) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context
                    .getApplicationContext()
                    .getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        context.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_NOTIFICATION);
    }

    /**
     * 打开消息通知的渠道设置
     */
    public void openChannelNotification() {
        if (mContext != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String packageName = mContext.getPackageName();
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, packageName);
                mContext.startActivity(intent);
            }
        }
    }

    public void clear() {
        stopAllLoop();
        stopForeground();
        if (manager != null) {
            manager = null;
        }
        if (mContext != null) {
            mContext = null;
        }
        if (mIntentActivity != null) {
            mIntentActivity = null;
        }
        if (mIntentService != null) {
            mIntentService = null;
        }
        if (mTickerText != null) {
            mTickerText = null;
        }
        if (mContentTitle != null) {
            mContentTitle = null;
        }
        if (mContentText != null) {
            mContentText = null;
        }
        if (pendingIntent != null) {
            pendingIntent = null;
        }
        if (mChannelDescription != null) {
            mChannelDescription = null;
        }
        if (mChannelName != null) {
            mChannelName = null;
        }
        if (mViewCallBackListener != null) {
            mViewCallBackListener = null;
        }
        if (mNotificationBuilder != null) {
            mNotificationBuilder = null;
        }
        if (mNotification != null) {
            mNotification = null;
        }
        if (mBuilder != null) {
            mBuilder = null;
        }
        mNotificationNumber = 0;
        LogUtil.e("清空了所有的Notification的对象！");
    }

    public interface OnHandlerLoopListener {
        void onLoop();
    }
    public static class Builder {
        private final Context mContext;
        /**
         * 自定义震动
         * <p>
         * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
         * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
         */
        private final long[] vibrates = {0, 1000};
        boolean autoCancel = true;                          // 是否点击取消通知
        private NotificationManager manager;
        private Intent mIntentActivity;
        private Intent mIntentService;
        private String mTickerText;            //  消息设置首次出现的名字
        private String mContentTitle;          // 消息标题头
        private String mContentText;           // 消息内容
        private int mContentSmallIcon;         // 消息的图标
        private int mNotificationNumber;       // 消息的数量
        private int mNotificationLevel = NotificationCompat.PRIORITY_DEFAULT;        // 消息的等级
        private String mChannelDescription;                 // 渠道的描述
        private String mChannelName;                        // 渠道的名字
        private int mChannelImportance;                     // 渠道的等级,默认是等级3，会提示声音
        private int mRemoteViewsLayout;                     // 状态栏布局
        private ViewCallBackListener<RemoteViews> mViewCallBackListener;
        private boolean mVibrate;                           // 震动
        private boolean mSound = true;                      // 是否发出声音，默认发出
        private long mWhen;                                 // 出现的时间戳
        private OnHandlerLoopListener mOnHandlerLoopListener; // handler轮询的监听器

        public Builder(Context context) {
            mContext = context;
            if (context != null) {
                // 创建管理器
                manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            // 默认的渠道等级
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            }
        }

        /**
         * @param autoCancel true:自动取消，false：侧滑不会删除
         * @return 设置是否点击取消
         */
        public Builder setAutoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public Builder setActivityPendingIntent(Intent intent) {
            mIntentActivity = intent;
            return this;
        }

        public Builder setServicePendingIntent(Intent intent) {
            mIntentService = intent;
            return this;
        }

        public Builder setActivity(Class<? extends Activity> activityCls) {
            if (mContext != null) {
                mIntentActivity = new Intent(mContext, activityCls);
                mIntentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            return this;
        }

        public Builder setService(Class<? extends Service> serviceCls) {
            if (mContext != null) {
                mIntentService = new Intent(mContext, serviceCls);
                mIntentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            return this;
        }

        /**
         * 通知首次出现在通知栏，带上升动画效果的，可设置文字，图标
         */
        public Builder setTickerText(String tickerText) {
            //通知首次出现在通知栏，带上升动画效果的，可设置文字，图标
            this.mTickerText = tickerText;
            return this;
        }

        /**
         * 设置标题头
         *
         * @param title 标题头
         */
        public Builder setContentTitle(String title) {
            this.mContentTitle = title;
            return this;
        }

        public Builder setContentText(String text) {
            this.mContentText = text;
            return this;
        }

        public Builder setSmallIcon(@DrawableRes int icon) {
            this.mContentSmallIcon = icon;
            return this;
        }

        /**
         * @param description 渠道的描述
         * @return 可见的渠道的描述
         */
        public Builder setChannelDescription(String description) {
            this.mChannelDescription = description;
            return this;
        }

        /**
         * @param channelName 渠道的名字
         * @return 设置渠道的名字
         */
        public Builder setChannelName(String channelName) {
            this.mChannelName = channelName;
            return this;
        }

        /**
         * @param importance NotificationManager # IMPORTANCE_NONE 关闭通知
         *                   IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
         *                   IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
         *                   IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
         *                   IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
         * @return 设置渠道的等级
         */
        public Builder setChannelImportance(int importance) {
            this.mChannelImportance = importance;
            return this;
        }

        /**
         * 设置声音，使用这个方法的话，必须在资源目录下设置一个raw的目录，资源设置的话，也需要R.raw.xx,例如： R.raw.tougong
         *
         * @param sound 声音的路径，使用
         */
        private Builder setSound(@DrawableRes int sound) {
            if (mContext != null) {
                // 自定义声音
                Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + sound);
            }
            return this;
        }

        /**
         * @param sound true：发出声音提示，fasle:不发出声音提示
         * @return 是否震动，默认发出声音提示
         */
        public Builder setSound(boolean sound) {
            this.mSound = sound;
            return this;
        }

        /**
         * @return 设置通知右下角显示的数字
         */
        public Builder setNumber(int number) {
            this.mNotificationNumber = number;
            return this;
        }

        /**
         * <ol>
         *         // 设置等级
         *         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         *             builder.setNotificationLevel(NotificationManager.IMPORTANCE_HIGH);
         *         } else {
         *             builder.setNotificationLevel(Notification.PRIORITY_HIGH);
         *         }
         * </ol>
         *
         * @param level 消息通知的等级，7.0以下使用 {@link Notification#PRIORITY_HIGH } ，7.0以上使用 {@link NotificationManager#IMPORTANCE_HIGH }去设置
         * @RequiresApi(api = Build.VERSION_CODES.N)  7.0 以上使用
         */
        public Builder setNotificationLevel(int level) {
            this.mNotificationLevel = level;
            return this;
        }

        /**
         * @return 设置是否震动
         */
        public Builder setVibrate(boolean vibrate) {
            this.mVibrate = vibrate;
            return this;
        }

        /**
         * @return 设置通知上的时间戳
         */
        public Builder setWhen(long when) {
            this.mWhen = when;
            return this;
        }

        /**
         * @param layoutId         布局的资源
         * @param callBackListener 数据回调
         * @return 设置消息的通知栏布局，这里如果有动态变化的view，需要去不停的调用发送消息的刷新
         */
        public Builder setRemoteView(int layoutId, ViewCallBackListener<RemoteViews> callBackListener) {
            this.mRemoteViewsLayout = layoutId;
            this.mViewCallBackListener = callBackListener;
            return this;
        }

        /**
         * @return 设置轮询的监听器
         */
        public Builder setOnLoopListener(OnHandlerLoopListener onLoopListener) {
            mOnHandlerLoopListener = onLoopListener;
            return this;
        }

        public NotificationUtil build() {
            return new NotificationUtil(this);
        }
    }@SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 先停止之前的消息发送，避免数据的快速轮询
            stopAllLoop();
            if (mNotification != null) {
                int id = msg.arg1;
                switch (msg.what) {
                    case CODE_WHAT_SEND_START_FOREGROUND:
                        LogUtil.e("开始了服务消息的单独发送！");
                        if (mService != null) {
                            mService.startForeground(id, mNotification);
                        }
                        break;
                    case CODE_WHAT_SEND_START_FOREGROUND_LOOP:
                        LogUtil.e("开始了服务消息的轮询发送！");
                        if (mService != null) {
                            mService.startForeground(id, mNotification);
                            Message message = mHandler.obtainMessage();
                            message.what = CODE_WHAT_SEND_START_FOREGROUND_LOOP;
                            message.arg1 = id;
                            mHandler.sendMessageDelayed(message, mIntervalTime);
                        }
                        // 轮询的回调
                        if (mOnHandlerLoopListener != null) {
                            mOnHandlerLoopListener.onLoop();
                        }
                        break;
                    case CODE_WHAT_SEND_START_NOTIFICATION_LOOP:
                        LogUtil.e("开始了---消息---的轮询发送！");
                        removeMessages(CODE_WHAT_SEND_START_NOTIFICATION_LOOP);
                        Message message1 = mHandler.obtainMessage();
                        message1.what = CODE_WHAT_SEND_START_NOTIFICATION_LOOP;
                        message1.arg1 = id;
                        sendNotification(id);
                        mHandler.sendMessageDelayed(message1, mIntervalTime);
                        // 轮询的回调
                        if (mOnHandlerLoopListener != null) {
                            mOnHandlerLoopListener.onLoop();
                        }
                        break;
                }
            }
        }
    };


}
