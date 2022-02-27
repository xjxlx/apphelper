package com.android.helper.utils.account

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.text.TextUtils
import com.android.helper.R
import com.android.helper.common.CommonConstants
import com.android.helper.utils.LogUtil
import com.android.helper.utils.NotificationUtil
import com.android.helper.utils.ServiceUtil

/**
 * 账号拉活的服务类，用来后台拉活
 */
class AppLifecycleService : Service() {

    @SuppressLint("StaticFieldLeak")
    private val CODE_NOTIFICATION = 19900713
    private val CODE_INTERVAL = 3 * 1000

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.writeLifeCycle("onStartCommand --->")

        // 标记打开服务的来源
        val fromType = intent!!.getStringExtra(CommonConstants.KEY_LIFECYCLE_FROM)
        if (fromType != null) {
            sendNotification(fromType)
        }

        // 启动变为前台服务
        startNotificationForeground()

        // 轮询发送
        val message: Message = mHandler.obtainMessage()
        message.what = CODE_NOTIFICATION
        mHandler.sendMessage(message)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendNotification(type: String) {
        val builder = NotificationUtil.Builder(applicationContext)
            .setChannelName(CommonConstants.KEY_LIFECYCLE_NOTIFICATION_CHANNEL_NAME)
            .setSmallIcon(R.mipmap.ic_launcher)

        if (TextUtils.equals(type, LifecycleAppEnum.From_Intent.from)) {
            builder.setContentText("我是后台服务，我是被直接启动的")
            LogUtil.writeLifeCycle("我是后台服务，我是被直接启动的")
        } else if (TextUtils.equals(type, LifecycleAppEnum.FROM_JOB.from)) {
            builder.setContentText("我是后台服务，我是被JobService启动的")
            LogUtil.writeLifeCycle("我是后台服务，我是被JobService启动的")
        } else if (TextUtils.equals(type, LifecycleAppEnum.FROM_ACCOUNT.from)) {
            builder.setContentText("我是后台服务，我是被账号拉活的")
            LogUtil.writeLifeCycle("我是后台服务，我是被账号拉活的")
        }
        builder.setWhen(System.currentTimeMillis())
        val notificationUtil = builder.build()
        notificationUtil.sendNotification(111)
    }

    private fun startNotificationForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "前台服务"
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val Channel = NotificationChannel(CHANNEL_ID, "主服务", NotificationManager.IMPORTANCE_HIGH)
            Channel.enableLights(true) //设置提示灯
            Channel.lightColor = Color.RED //设置提示灯颜色
            Channel.setShowBadge(true) //显示logo
            Channel.description = "应用保活的服务" //设置描述
            Channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC //设置锁屏可见 VISIBILITY_PUBLIC=可见
            manager.createNotificationChannel(Channel)
            val notification: Notification = Notification.Builder(this)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(false)
                .setContentTitle("应用全局保活") //标题
                .setContentText("应用全局保活进行中...") //内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher) //小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .build()
            startForeground(CODE_NOTIFICATION, notification) //服务前台化只能使用startForeground()方法,不能使用 notificationManager.notify(1,notification); 这个只是启动通知使用的,使用这个方法你只需要等待几秒就会发现报错了
        }
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == CODE_NOTIFICATION) {
                // 1：删除原有的消息通知，避免重复性发送消息
                removeMessages(CODE_NOTIFICATION)
                removeCallbacksAndMessages(null)

                // 2:启动jobService
                val jobServiceName: String = LifecycleManager.getInstance().jobServiceName
                val jobServiceRunning = ServiceUtil.isJobServiceRunning(applicationContext, jobServiceName)
                LogUtil.writeLifeCycle("☆☆☆☆☆---我是后台服务，当前jobService的状态为:$jobServiceRunning")
                if (!jobServiceRunning) {
                    AppJobService.startJob(applicationContext, LifecycleAppEnum.FROM_SERVICE)
                }

                // 3：轮询发送消息通知
                LogUtil.e("---> 开始发送了消息的轮询！")
                val message = this.obtainMessage()
                message.what = CODE_NOTIFICATION
                sendMessageDelayed(message, CODE_INTERVAL.toLong())
            }
        }
    }
}