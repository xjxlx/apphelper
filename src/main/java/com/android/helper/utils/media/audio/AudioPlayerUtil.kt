package com.android.helper.utils.media.audio

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.os.IBinder
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.android.common.utils.LogUtil
import com.android.common.utils.TextViewUtil
import com.android.helper.R
import com.android.helper.interfaces.listener.CallBackListener
import com.android.helper.interfaces.listener.ViewCallBackListener
import com.android.helper.utils.BitmapUtil
import com.android.helper.utils.DateUtil
import com.android.helper.utils.NotificationUtil
import com.android.helper.utils.ServiceUtil
import com.android.helper.utils.dialog.DialogClickListener
import com.android.helper.utils.dialog.DialogUtil

/** 音频播放的工具类 */
class AudioPlayerUtil(private val mContext: FragmentActivity) :
    AudioPlayerCallBackListener() {
    private var connection: AudioServiceConnection? = null
    private var mBindService = false
    private var mBindServiceListener: BindServiceListener? = null
    private var intent: Intent? = null
    private var mSeekBar: SeekBar? = null
    private var mSeekBarProgressView: TextView? = null
    private var mSeekBarTotalView: TextView? = null
    private var mStartButton: View? = null // 开始按钮
    private var mCallBackListener: AudioPlayerCallBackListener? = null
    private var mAudioPath: String? = null // 播放的路径
    private var mAutoPlayer = false // 是否自动播放

    private var mNotificationStart = 0 // 消息通知栏开始的按钮
    private var mNotificationPause = 0 // 消息通知栏暂停的按钮
    private var mNotificationLeft = 0 // 消息通知栏左侧的按钮
    private var mNotificationRight = 0 // 消息通知栏右侧的按钮

    private var mNotificationImage: String? = null // 消息通知栏左侧的图标
    private var mNotificationTitle: String? = null // 消息通知栏上方的标题
    private var mNotificationSmallIcon = 0 // 设置小标题
    private var mAudioList: MutableList<AudioEntity?>? = null // 消息通知栏使用到的数据列表
    private var mPendingIntentActivity: Class<out Activity?>? = null

    // 点击【悬浮按钮通知】或者【锁屏通知】或者【状态栏】跳转的页面
    private var mNotificationUtil: NotificationUtil? = null
    private var mAudioReceiver: AudioReceiver? = null
    private var mNotificationLoopInterVal = 10000 // 消息轮训的时间，默认间隔是10秒
    private var mAudioPosition = -1 // 消息通知栏当前按播放音频的角标,默认的值是-1
    private var mRemoteViews: RemoteViews? = null
    private var mAudioService: AudioService? = null // 音乐播放器的服务类
    private var mDialogUtil: DialogUtil? = null

    /** 绑定服务 */
    fun bindService(bindServiceListener: BindServiceListener?) {
        this.mBindServiceListener = bindServiceListener
        LogUtil.e(AudioConstant.TAG, "bindService--->开始绑定服务！")
        intent = Intent(mContext, AudioService::class.java)
        if (connection == null) {
            connection = AudioServiceConnection()
        }
        // 启动后台服务
        try {
            ServiceUtil.startService(mContext, intent)
        } catch (e: Exception) {
            LogUtil.e("开启服务失败:" + e.message)
        }
        // 绑定前台的服务,禁止冲洗请的绑定
        if (!mBindService) {
            mBindService =
                mContext.bindService(intent!!, connection!!, Context.BIND_AUTO_CREATE)
        }
        LogUtil.e(AudioConstant.TAG, "bindService--->后台服务绑定成功：$mBindService")
    }

    /** 解绑服务 */
    fun unBindService() {
        if (mBindService) {
            connection?.let { mContext.unbindService(it) }
            mBindService = false
        }
    }

    /** @param audioPath 播放地址 */
    fun setResource(audioPath: String?) {
        this.mAudioPath = audioPath
        mAudioBinder?.setAudioResource(audioPath)
    }

    /** @param autoPlayer 是否自动播放 */
    fun autoPlayer(autoPlayer: Boolean) {
        this.mAutoPlayer = autoPlayer
    }

    fun start() {
        mAudioBinder?.start()
    }

    fun pause() {
        mAudioBinder?.pause()
    }

    fun stop() {
        mAudioBinder?.stop()
    }

    /** 页面停止不可见时候的处理 */
    fun destroy() {
        if (mBindService) {
            unBindService()
        }
        // 解除注册广播接收者
        if (mAudioReceiver != null) {
            mContext.unregisterReceiver(mAudioReceiver)
            LogUtil.e("解除了动态广播的注册！")
        }
        // 停止间隔的轮询
        mNotificationUtil?.stopAllLoop()
        // 停止后台的服务
        mContext.stopService(intent)
        mAudioBinder = null
        mBindService = false
        mBindServiceListener = null
        mSeekBar = null
        mSeekBarProgressView = null
        mSeekBarTotalView = null
        mStartButton = null
        mCallBackListener = null
        mAudioPath = null
        mAutoPlayer = false
    }

    /** 轮询间隔的时间 */
    fun setLoopInterval(loopInterval: Int) {
        this.mNotificationLoopInterVal = loopInterval
    }

    private fun initNotification() {
        mNotificationUtil =
            NotificationUtil.Builder(mContext)
                .setSmallIcon(mNotificationSmallIcon)
                .setNotificationLevel(Notification.PRIORITY_DEFAULT)
                .setActivity(mPendingIntentActivity)
                .setVibrate(true)
                .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
                .setRemoteView(
                    R.layout.notification_audio,
                    ViewCallBackListener { remoteViews: RemoteViews? ->
                        if (remoteViews != null) {
                            mRemoteViews = remoteViews
                            // 左侧的按钮
                            if (mNotificationLeft != 0) {
                                remoteViews.setImageViewResource(
                                    R.id.iv_to_left,
                                    mNotificationLeft,
                                )
                            }
                            // 中间的按钮
                            if (mAudioBinder?.isPlaying() == true) {
                                if (mNotificationPause != 0) {
                                    remoteViews.setImageViewResource(
                                        R.id.iv_start,
                                        mNotificationPause,
                                    )
                                }
                            } else {
                                if (mNotificationStart != 0) {
                                    remoteViews.setImageViewResource(
                                        R.id.iv_start,
                                        mNotificationStart,
                                    )
                                }
                            }
                            // 右侧的按钮
                            if (mNotificationRight != 0) {
                                remoteViews.setImageViewResource(
                                    R.id.iv_to_right,
                                    mNotificationRight,
                                )
                            }
                            // 播放按钮点击事件的处理
                            val intentStart = Intent()
                            intentStart.setAction(AudioConstant.ACTION_PAUSE)
                            intentStart.setAction(AudioConstant.ACTION_START)
                            val btPendingIntentStart =
                                PendingIntent.getBroadcast(
                                    mContext,
                                    AudioConstant.CODE_SEND_BROADCAST_RECEIVER,
                                    intentStart,
                                    PendingIntent.FLAG_UPDATE_CURRENT,
                                )
                            remoteViews.setOnClickPendingIntent(
                                R.id.iv_start,
                                btPendingIntentStart,
                            )
                            // 左侧按钮点击事件的处理
                            val intentLeft = Intent()
                            intentLeft.setAction(AudioConstant.ACTION_LEFT)
                            val btPendingIntentLeft =
                                PendingIntent.getBroadcast(
                                    mContext,
                                    AudioConstant.CODE_SEND_BROADCAST_RECEIVER,
                                    intentLeft,
                                    PendingIntent.FLAG_UPDATE_CURRENT,
                                )
                            remoteViews.setOnClickPendingIntent(
                                R.id.iv_to_left,
                                btPendingIntentLeft,
                            )
                            // 左侧按钮点击事件的处理
                            val intentRight = Intent()
                            intentRight.setAction(AudioConstant.ACTION_RIGHT)
                            val btPendingIntentRight =
                                PendingIntent.getBroadcast(
                                    mContext,
                                    AudioConstant.CODE_SEND_BROADCAST_RECEIVER,
                                    intentRight,
                                    PendingIntent.FLAG_UPDATE_CURRENT,
                                )
                            remoteViews.setOnClickPendingIntent(
                                R.id.iv_to_right,
                                btPendingIntentRight,
                            )
                        }
                    },
                )
                .build()
                .sendNotification(1)
                .startForeground(1, mAudioService)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NotificationUtil.CODE_REQUEST_ACTIVITY_NOTIFICATION) {
            mNotificationUtil?.let {
                if (it.checkOpenNotify(mContext)) {
                    initNotification()
                }
            }
        }
    }

    /** @param callBackListener 数据回调 */
    fun setAudioCallBackListener(callBackListener: AudioPlayerCallBackListener?) {
        if (callBackListener != null) {
            this.mCallBackListener = callBackListener
        }
    }

    fun setSeekBar(seekBar: SeekBar?) {
        if (seekBar == null) {
            return
        }
        this.mSeekBar = seekBar
        // 设置默认的进度
        seekBar.progress = 0
        if (mAudioBinder == null) {
            return
        }
        seekBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (fromUser) {
                        mAudioBinder?.let {
                            val mediaPlayer: MediaPlayer? = it.getMediaPlayer()
                            if (mediaPlayer != null) {
                                val status: Int = it.getStatus()
                                if (
                                    (status != AudioConstant.STATUS_IDLE) &&
                                        (status != AudioConstant.STATUS_ERROR)
                                ) {
                                    mediaPlayer.seekTo(progress)
                                }
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    mAudioBinder?.sendProgress(false)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mAudioBinder?.sendProgress(true)
                }
            }
        )
    }

    /** @param progressTimeView 设置SeekBar不停变换进度的view */
    fun setSeekBarProgressTime(progressTimeView: TextView?) {
        this.mSeekBarProgressView = progressTimeView
        // 设置默认的进度
        TextViewUtil.setText(mSeekBarProgressView, "00:00")
    }

    /** @param totalTimeView 设置SeekBar固定不变的view */
    fun setSeekBarTotalTime(totalTimeView: TextView?) {
        if (totalTimeView == null) {
            return
        }
        this.mSeekBarTotalView = totalTimeView
        // 设置默认的进度
        TextViewUtil.setText(mSeekBarTotalView, "00:00")
    }

    /** @param view 设置开关按钮的变换 */
    fun setStartButton(view: View?) {
        if (view == null) {
            return
        }
        this.mStartButton = view
        if (mAudioBinder == null) {
            return
        }
        // 播放按钮的点击事件
        view.setOnClickListener(
            View.OnClickListener { v: View? ->
                mAudioBinder?.let {
                    val initialized: Boolean = it.initialized()
                    if (initialized) {
                        if (it.isPlaying()) {
                            pause()
                        } else {
                            start()
                        }
                    } else {
                        setResource(mAudioPath)
                    }
                }
            }
        )
    }

    fun switchStartButton(selector: Boolean) {
        mStartButton?.isSelected = selector
    }

    override fun onBufferProgress(total: Int, current: Double, percent: Int) {
        LogUtil.e(
            "onBufferProgress:-->total:$total  --->current:$current --->percent:$percent"
        )
        mSeekBar?.setMax(total)
        mSeekBar?.setSecondaryProgress(current.toInt())
        if (total > 0) {
            // 设置总的进度
            if (mSeekBarTotalView != null) {
                val totalContent = mSeekBarTotalView!!.getText()
                if (TextUtils.isEmpty(totalContent)) {
                    val charSequence = DateUtil.formatMillis(total.toLong())
                    TextViewUtil.setText(mSeekBarTotalView, charSequence)
                }
                // 设置默认的进度
                if (mSeekBarProgressView != null) {
                    val text = mSeekBarProgressView!!.getText()
                    if (TextUtils.isEmpty(text)) {
                        val length = totalContent.length
                        if (length == 3) {
                            TextViewUtil.setText(mSeekBarProgressView, "00:00:00")
                        } else {
                            TextViewUtil.setText(mSeekBarProgressView, "00:00")
                        }
                    }
                }
            }
        }
    }

    override fun onProgress(total: Int, current: Int, percent: String?) {
        mSeekBar?.setMax(total)
        mSeekBar?.progress = current
        // 设置总的进度
        if (mSeekBarTotalView != null && total > 0) {
            val charSequence = DateUtil.formatMillis(total.toLong())
            TextViewUtil.setText(mSeekBarTotalView, charSequence)
        }
        // 设置默认的进度
        if (current > 0) {
            val charSequence = DateUtil.formatMillis(current.toLong())
            TextViewUtil.setText(mSeekBarProgressView, charSequence)
        }
        // 更正按钮的转改,加到这个地方，更加靠谱
        switchStartButton(true)
    }

    override fun onPrepared() {
        super.onPrepared()
        LogUtil.e("onPrepared")
        mCallBackListener?.onPrepared()
    }

    override fun onStart() {
        super.onStart()
        LogUtil.e("onStart")
        switchStartButton(true)
        mCallBackListener?.onStart()
        setNotificationInfo()
    }

    /** 设置消息通知的具体数据 */
    private fun setNotificationInfo() {
        // 获取当前view的角标
        this.currentInfo
        // 更改view的图标
        if (mRemoteViews != null) {
            mRemoteViews?.setImageViewResource(R.id.iv_start, mNotificationPause)
            if (!TextUtils.isEmpty(mNotificationImage)) {
                // 左侧图片的资源
                BitmapUtil.getBitmapForService(
                    mContext,
                    mNotificationImage,
                    CallBackListener { successful: Boolean, tag: Any?, bitmap: Bitmap? ->
                        if (tag != null && tag == BitmapUtil.STATUS_SUCCESS) {
                            mRemoteViews?.setImageViewBitmap(R.id.iv_launcher, bitmap)
                            // 发送间隔的轮询
                            mNotificationUtil?.startForeground(1, mAudioService)
                        }
                    },
                )
            }
            // 中间的标题头
            if (!TextUtils.isEmpty(mNotificationTitle)) {
                mRemoteViews?.setTextViewText(R.id.tv_title, mNotificationTitle)
                mRemoteViews?.setTextColor(R.id.tv_title, Color.BLACK)
                mRemoteViews?.setTextViewTextSize(
                    R.id.tv_title,
                    TypedValue.COMPLEX_UNIT_SP,
                    16f,
                )
            }
        }
        // 发送间隔的轮询
        mNotificationUtil?.startLoopForeground(
            1,
            mNotificationLoopInterVal.toLong(),
            mAudioService,
        )
    }

    override fun onPause() {
        super.onPause()
        LogUtil.e("onPause")
        switchStartButton(false)
        mCallBackListener?.onPause()
        if (mNotificationStart != 0) {
            mRemoteViews?.setImageViewResource(R.id.iv_start, mNotificationStart)
        }
        // 发送间隔的轮询
        mNotificationUtil?.startForeground(1, mAudioService)
    }

    override fun onStop() {
        super.onStop()
        LogUtil.e("onStop")
        switchStartButton(false)
        mCallBackListener?.onStop()
    }

    override fun onError(e: Exception?) {
        super.onError(e)
        LogUtil.e("onError")
        switchStartButton(false)
        mCallBackListener?.onError(e)
    }

    override fun onComplete() {
        super.onComplete()
        LogUtil.e("onComplete")
        switchStartButton(false)
        mCallBackListener?.onComplete()
    }

    /**
     * @param notificationStart notification开始的按钮
     * @param notificationPause notification暂停的按钮
     * @param notificationLeft notification左侧的按钮
     * @param notificationRight notification右侧的按钮
     */
    fun setNotificationIcon(
        @DrawableRes notificationStart: Int,
        @DrawableRes notificationPause: Int,
        @DrawableRes notificationLeft: Int,
        @DrawableRes notificationRight: Int,
    ) {
        mNotificationStart = notificationStart
        mNotificationPause = notificationPause
        mNotificationLeft = notificationLeft
        mNotificationRight = notificationRight
    }

    /**
     * @param notificationImage notification左侧的图标
     * @param notificationTitle notification的标题
     */
    fun setNotificationMessage(notificationImage: String?, notificationTitle: String?) {
        mNotificationImage = notificationImage
        mNotificationTitle = notificationTitle
    }

    fun setNotificationList(list: MutableList<AudioEntity?>?) {
        mAudioList = list
        // 首次拿到数据也是需要设置一下的
        setNotificationInfo()
    }

    fun setNotificationSmallIcon(smallIcon: Int) {
        this.mNotificationSmallIcon = smallIcon
    }

    fun setPendingIntentActivity(cls: Class<out Activity?>?) {
        this.mPendingIntentActivity = cls
    }

    /** 下一曲 */
    fun nextPage() {
        LogUtil.e("播放下一首的方法")
        if ((mAudioList != null) && (mAudioList!!.isNotEmpty())) {
            LogUtil.e(
                "播放下一首的方法--->对象不为空，数据不为空，当前的position为：$mAudioPosition --->当前的url：$mAudioPath"
            )
            if (mAudioPosition != -1) {
                if (mAudioPosition < mAudioList!!.size - 1) {
                    mAudioPosition += 1
                } else { // 无限循环
                    mAudioPosition = 0
                }
                val audioEntity = mAudioList!![mAudioPosition]
                if (audioEntity != null) {
                    val url = audioEntity.audio
                    if (!TextUtils.isEmpty(url)) {
                        mAudioPath = url
                        setResource(mAudioPath)
                    }
                }
                LogUtil.e("播放下一首的方法--->next--->$mAudioPosition --->当前的url：$mAudioPath")
            } else {
                LogUtil.e("nextPage--->角标异常,暂停播放！")
            }
        } else {
            LogUtil.e("播放下一首的方法--->集合为空")
        }
    }

    /** 上一曲 */
    fun onPage() {
        LogUtil.e("播放上一首的方法")
        if ((mAudioList != null) && (mAudioList!!.isNotEmpty())) {
            LogUtil.e(
                "播放上一首的方法--->对象不为空，数据不为空，当前的position为：$mAudioPosition --->当前的url：$mAudioPath"
            )
            if (mAudioPosition != -1) {
                if (mAudioPosition > 0) {
                    mAudioPosition -= 1
                } else { // 无限循环
                    mAudioPosition = mAudioList!!.size - 1
                }
                val audioEntity = mAudioList!![mAudioPosition]
                if (audioEntity != null) {
                    val url = audioEntity.audio
                    if (!TextUtils.isEmpty(url)) {
                        mAudioPath = url
                        setResource(mAudioPath)
                    }
                }
                LogUtil.e("播放上一首的方法--->next--->$mAudioPosition --->当前的url：$mAudioPath")
            } else {
                LogUtil.e("onPage--->角标异常,暂停播放！")
            }
        } else {
            LogUtil.e("播放上一首的方法--->集合为空")
        }
    }

    val currentInfo: Unit
        /** 获取当前音频播放对应的角信息 */
        get() {
            mAudioPosition = -1
            if ((mAudioList != null) && (mAudioList!!.isNotEmpty())) {
                for (i in mAudioList!!.indices) {
                    val audioEntity = mAudioList!![i]
                    if (audioEntity != null) {
                        val ur = audioEntity.audio
                        if (TextUtils.equals(ur, mAudioPath)) {
                            mAudioPosition = i
                            this.mNotificationImage = audioEntity.cover
                            this.mNotificationTitle = audioEntity.name
                            // 把当前数据的对象返回出去
                            if (mCallBackListener != null) {
                                mCallBackListener!!.onNotificationCallInfo(i, audioEntity)
                            }
                            break
                        }
                    }
                }
            }
            LogUtil.e(
                "当前的角标为：$mAudioPosition  ---> mNotificationImage:$mNotificationImage   mNotificationTitle:$mNotificationTitle"
            )
        }

    internal inner class AudioServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogUtil.e("-----@@@@@@----> onServiceConnected!")
            if (service is AudioService.AudioBinder) {
                mAudioBinder = service
                LogUtil.e(AudioConstant.TAG, "onServiceConnected--->服务回调成功：$mAudioBinder")
                // 生命周期的回调
                mBindServiceListener?.bindResult(mBindService)
                if (mAudioBinder != null) {
                    mAudioService = mAudioBinder?.getService()
                    mAudioBinder?.setAudioCallBackListener(this@AudioPlayerUtil)
                    setSeekBar(mSeekBar)
                    setStartButton(mStartButton)
                    // 绑定成功后自动播放
                    if (mAutoPlayer) {
                        LogUtil.e(
                            AudioConstant.TAG,
                            "onServiceConnected--->服务回调成功,开始自动播放！",
                        )
                        if (!TextUtils.isEmpty(mAudioPath)) {
                            setResource(mAudioPath)
                        }
                    }
                    // 动态注册广播接收者
                    if (mAudioReceiver == null) {
                        mAudioReceiver = AudioReceiver()
                        val intentFilter = IntentFilter()
                        intentFilter.addAction(AudioConstant.ACTION_START)
                        intentFilter.addAction(AudioConstant.ACTION_PAUSE)
                        intentFilter.addAction(AudioConstant.ACTION_LEFT)
                        intentFilter.addAction(AudioConstant.ACTION_RIGHT)
                        // 注册
                        ContextCompat.registerReceiver(
                            mContext,
                            mAudioReceiver,
                            intentFilter,
                            ContextCompat.RECEIVER_NOT_EXPORTED,
                        )
                    }
                    // 在数据回调成功的时候去创建消息通知工具
                    try {
                        if (mNotificationUtil == null) {
                            // 此处无论如何都要初始化一次的，否则会nar异常
                            initNotification()
                            val openNotify = mNotificationUtil!!.checkOpenNotify(mContext)
                            if (!openNotify) {
                                mDialogUtil =
                                    DialogUtil.Builder(
                                            mContext,
                                            R.layout.base_default_dialog,
                                        )
                                        .setClose(R.id.tv_qx)
                                        .Build()
                                        .setText(R.id.tv_title, "是否打开通知权限？")
                                        .setText(
                                            R.id.tv_msg,
                                            "如果不打开通知权限，则可能后台播放的时候会断开连接！",
                                        )
                                        .setOnClickListener(
                                            R.id.tv_qd,
                                            DialogClickListener {
                                                v: View?,
                                                builder: DialogUtil? ->
                                                mNotificationUtil!!.goToSetNotify(
                                                    mContext
                                                )
                                            },
                                        )
                            }
                        }
                    } catch (e: Exception) {
                        LogUtil.e("------------->:" + e.message)
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtil.e("-----@@@@@@----> onServiceDisconnected!")
        }
    }

    inner class AudioReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if ((intent != null) && (mAudioBinder != null)) {
                val action = intent.action
                val playing: Boolean = mAudioBinder!!.isPlaying()
                LogUtil.e(
                    "-------------------------------->AudioReceiver ---> onReceive:$action   --->player:$playing"
                )
                when (action) {
                    AudioConstant.ACTION_START,
                    AudioConstant.ACTION_PAUSE -> {
                        if (mAudioBinder!!.initialized()) {
                            if (playing) {
                                pause()
                            } else {
                                start()
                            }
                        } else {
                            setResource(mAudioPath)
                        }
                    }

                    AudioConstant.ACTION_LEFT -> {
                        onPage()
                    }

                    AudioConstant.ACTION_RIGHT -> {
                        nextPage()
                    }
                }
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mAudioBinder: AudioService.AudioBinder? = null
    }
}
