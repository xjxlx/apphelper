package com.android.helper.utils.account

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.android.common.utils.LogUtil
import com.android.common.utils.WriteLogUtil
import com.android.helper.R
import com.android.helper.common.CommonConstants
import com.android.helper.utils.NotificationUtil
import com.android.helper.utils.ServiceUtil

/**
 * 账号拉活的服务类，用来后台拉活
 */
class AppLifecycleService : Service() {
    private val mWriteUtil: WriteLogUtil =
        WriteLogUtil(CommonConstants.FILE_LIFECYCLE_NAME + ".txt")

    @SuppressLint("StaticFieldLeak")
    private val CODE_NOTIFICATION = 19900713
    private val CODE_INTERVAL = 5 * 1000

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mWriteUtil.init(baseContext)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        mWriteUtil.write("onStartCommand --->")

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
        val builder =
            NotificationUtil
                .Builder(applicationContext)
                .setChannelName(CommonConstants.KEY_LIFECYCLE_NOTIFICATION_CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_launcher)

        if (TextUtils.equals(type, LifecycleAppEnum.From_Intent.from)) {
            builder.setContentText("我是后台服务，我是被直接启动的")
            mWriteUtil.write("我是后台服务，我是被直接启动的")
        } else if (TextUtils.equals(type, LifecycleAppEnum.FROM_JOB.from)) {
            builder.setContentText("我是后台服务，我是被JobService启动的")
            mWriteUtil.write("我是后台服务，我是被JobService启动的")
        } else if (TextUtils.equals(type, LifecycleAppEnum.FROM_ACCOUNT.from)) {
            builder.setContentText("我是后台服务，我是被账号拉活的")
            mWriteUtil.write("我是后台服务，我是被账号拉活的")
        }
        builder.setWhen(System.currentTimeMillis())
        val notificationUtil = builder.build()
        notificationUtil.sendNotification(111)
    }

    private fun startNotificationForeground() {
        NotificationUtil
            .Builder(baseContext)
            .setWhen(System.currentTimeMillis())
            .setChannelName("应用保活")
            .setChannelImportance(NotificationManager.IMPORTANCE_HIGH)
            .setChannelDescription("应用保活的服务")
            .setContentTitle("应用全局保活")
            .setAutoCancel(false)
            .setContentText("应用全局保活进行中...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setNotificationLevel(NotificationManager.IMPORTANCE_HIGH)
            .setService(AppLifecycleService::class.java)
            .build()
            .startLoopForeground(CODE_NOTIFICATION, CODE_INTERVAL.toLong(), this)
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler =
        object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

                if (msg.what == CODE_NOTIFICATION) { // 1：删除原有的消息通知，避免重复性发送消息
                    removeMessages(CODE_NOTIFICATION)
                    removeCallbacksAndMessages(null)

                    // 2:启动jobService
                    val jobServiceName: String =
                        LifecycleManager
                            .getInstance()
                            .jobServiceName
                    val jobServiceRunning =
                        ServiceUtil.isJobServiceRunning(
                            applicationContext,
                            jobServiceName
                        )
                    mWriteUtil?.write(
                        "☆☆☆☆☆---我是后台服务，当前jobService的状态为:$jobServiceRunning"
                    )

                    if (!jobServiceRunning) {
                        AppJobService.startJob(
                            applicationContext,
                            LifecycleAppEnum.FROM_SERVICE
                        )
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
