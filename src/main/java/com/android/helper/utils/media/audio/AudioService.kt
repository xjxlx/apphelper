package com.android.helper.utils.media.audio

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import com.android.common.utils.LogUtil
import com.android.common.utils.ToastUtil.show
import com.android.helper.httpclient.RxUtil
import io.reactivex.Flowable
import io.reactivex.functions.Predicate
import io.reactivex.subscribers.DisposableSubscriber
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class AudioService : Service() {
    // 当前的状态，默认等于闲置的状态
    private var STATUS_TYPE = AudioConstant.STATUS_IDLE

    private var context: Context? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var mAudioPath: String? = null
    private var mOldAudioPath: String? = null
    private var mCallBackListener: AudioPlayerCallBackListener? = null
    private var mDuration = 0 // 时长
    private var disposableSubscriber: DisposableSubscriber<Long?>? = null
    private var mSendProgress = true // 是否正常发送当前的进度，默认为true

    /** 播放完成的监听 */
    private val onCompletionListener: MediaPlayer.OnCompletionListener =
        object : MediaPlayer.OnCompletionListener {
            override fun onCompletion(iMediaPlayer: MediaPlayer?) {
                STATUS_TYPE = AudioConstant.STATUS_COMPLETE
                LogUtil.e(AudioConstant.TAG, "onCompletion--->播放完成了！")
                // 暂停轮询的统计
                mSendProgress = false
                /*
                 * 置空临时变量，让程序重新开始,这一步非常关键，
                 * 如果不置空，那么就会出现在播放完成之后，点击播放按钮程序无法重新开始的情况
                 */
                mCallBackListener?.onComplete()
            }
        }
    private var initialized = false // 是否已经完成了初始化
    private val onErrorListener: MediaPlayer.OnErrorListener =
        object : MediaPlayer.OnErrorListener {
            override fun onError(
                iMediaPlayer: MediaPlayer?,
                what: Int,
                extra: Int,
            ): Boolean {
                LogUtil.e(AudioConstant.TAG, "onError--->发生了错误！ what:$what")
                STATUS_TYPE = AudioConstant.STATUS_ERROR
                when (what) {
                    MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                        // 未指定的媒体播放器错误。
                        setErrorData(Exception("未指定的媒体播放器错误"))
                    }

                    MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                        // 媒体服务器死了。在这种情况下，应用程序必须释放
                        if (mediaPlayer != null) {
                            mediaPlayer!!.release()
                            mediaPlayer = null
                        }
                        setErrorData(Exception("媒体服务器死了"))
                    }

                    MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                        // 视频流，其容器对逐行扫描无效。
                        setErrorData(Exception("视频流，其容器对逐行扫描无效"))
                    }

                    MediaPlayer.MEDIA_ERROR_IO -> {
                        setErrorData(Exception("IO刘错误"))
                    }

                    MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                        // 位流不符合相关编码标准或文件规范。
                        setErrorData(Exception("位流不符合相关编码标准或文件规范"))
                    }

                    MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                        // 超时
                        setErrorData(Exception("链接超时"))
                    }

                    else -> {
                        // 如果一旦遇到了这个错误，则需要整体重置一下播放器
                        clear()
                    }
                }
                LogUtil.e("what:$what --- extra:$extra")
                return true
            }
        }

    // 播放信息的回调
    private val mInfoListener: MediaPlayer.OnInfoListener =
        object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, arg1: Int, arg2: Int): Boolean {
                when (arg1) {
                    MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> {}

                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {}

                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        LogUtil.e("开始缓冲！")
                    }

                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        LogUtil.e("缓冲结束！")
                    }

                    MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {}

                    MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {}

                    MediaPlayer.MEDIA_INFO_METADATA_UPDATE -> {}

                    MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE -> {}

                    MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT -> {}

                    MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                        // LogUtil.e("media_error_timed_out:网络连接超时！");
                        setErrorData(Exception("网络连接超时"))
                    }

                    MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                        // LogUtil.e("media_error_unsupported:数据不支持！");
                        setErrorData(Exception("数据不支持"))
                    }

                    MediaPlayer.MEDIA_ERROR_IO -> {
                        // LogUtil.e("media_error_unsupported:IO错误！");
                        setErrorData(Exception("IO流错误"))
                    }

                    MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                        // LogUtil.e("media_error_unsupported:视频中断，一般是视频源异常或者不支持的视频类型！");
                        setErrorData(Exception("视频中断，音频源异常"))
                    }

                    -1000 -> {
                        // LogUtil.e("一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的！");
                        setErrorData(Exception("视频中断，音频格式错误"))
                    }

                    MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {}
                }
                return true
            }
        }

    // 播放进度的回调
    private val onBufferingUpdateListener: MediaPlayer.OnBufferingUpdateListener =
        object : MediaPlayer.OnBufferingUpdateListener {
            override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
                LogUtil.e(
                    AudioConstant.TAG,
                    "onBufferingUpdate--->播放进度的回调，当前的缓冲百分比为:$percent",
                )
                if (mDuration <= 0) {
                    mDuration = duration
                }
                // 计算出当前的缓冲比例
                val percentFloat = percent / 100.0 // 注意：两个整数相除的结果是整数
                // 缓冲比例 乘以 总数大小 == 当前缓冲的进度
                val currentProgress = mDuration * percentFloat
                mCallBackListener?.onBufferProgress(mDuration, currentProgress, percent)
            }
        }

    override fun onCreate() {
        super.onCreate()
        initialized = false
        if (context == null) {
            context = application
        }
        if (audioManager == null) {
            audioManager = context?.getSystemService(AUDIO_SERVICE) as AudioManager?
        }
        // 创建对象
        mediaPlayer = getMediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 创建对象
        mediaPlayer = getMediaPlayer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = AudioBinder()

    fun getMediaPlayer(): MediaPlayer? {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            LogUtil.e(AudioConstant.TAG, "getMediaPlayer--->重新构建了MediaPlayer")
        }
        return mediaPlayer
    }

    /** 初始化监听器 */
    fun initListener() {
        mediaPlayer?.setOnPreparedListener(onPreparedListener)
        mediaPlayer?.setOnErrorListener(onErrorListener)
        mediaPlayer?.setOnCompletionListener(onCompletionListener)
        mediaPlayer?.setOnInfoListener(mInfoListener)
        mediaPlayer?.setOnBufferingUpdateListener(onBufferingUpdateListener)
    }

    /** @param audioPath 设置播放资源 */
    fun setResource(audioPath: String?) {
        LogUtil.e(AudioConstant.TAG, "走入到setResource方法中！")
        if (TextUtils.isEmpty(audioPath)) {
            show("播放地址不能为空！")
            LogUtil.e(AudioConstant.TAG, "setResource--->播放地址为空")
            return
        }
        mAudioPath = audioPath
        LogUtil.e(AudioConstant.TAG, "setResource--->播放地址为:$audioPath")
        // 开始去播放资源
        player()
    }

    /** 开始去准备播放音频 */
    fun player() {
        // 清空数据
        LogUtil.e(AudioConstant.TAG, "走入到player方法中！")
        mediaPlayer = getMediaPlayer()
        if (!TextUtils.equals(mAudioPath, mOldAudioPath)) {
            LogUtil.e(AudioConstant.TAG, "player--->播放地址不相同，执行后续的逻辑！")
            initResource()
        } else {
            // 如果路径相同的时候，才去判读当前的状态
            LogUtil.e(AudioConstant.TAG, "play--->播放地址相同，开始去判定开始还是暂停的操作！")
            start()
        }
    }

    private fun initResource() {
        LogUtil.e(AudioConstant.TAG, "走入了initResource方法中！")
        if (!TextUtils.isEmpty(mAudioPath)) {
            try {
                reset()
                // 清空对象
                initialized = false
                // 初始化监听
                initListener()
                // 指定参数为音频文件
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer?.setDataSource(mAudioPath) // 为多媒体对象设置播放路径
                mediaPlayer?.prepareAsync() // 异步准备（准备播放
                LogUtil.e(AudioConstant.TAG, "player--->重新重置了资源，并设置了数据！")
            } catch (e: IOException) {
                e.printStackTrace()
                LogUtil.e(AudioConstant.TAG, "player--->设置数据异常：" + e.message)
                mCallBackListener?.onError(Exception("player--->" + e.message))
            }
        } else {
            show("播放地址不能为空！")
        }
    }

    /** 开始播放 */
    fun start(): Boolean {
        LogUtil.e(AudioConstant.TAG, "走入到了start的方法中！")
        try {
            mediaPlayer = getMediaPlayer()
            if (mediaPlayer != null) {
                // 无论是开始还是暂停，都不能是在闲置状态的时候去执行，否则就会异常
                LogUtil.e(AudioConstant.TAG, "start--->initialized：$initialized")
                if (initialized) {
                    if (this.isPlaying) { // 播放中的状态
                        // 暂停播放
                        pause()
                    } else {
                        LogUtil.e(AudioConstant.TAG, "start--->开始播放！")
                        // 暂停状态或者其他状态下，直接开始
                        mediaPlayer?.start()
                        STATUS_TYPE = AudioConstant.STATUS_PLAYING
                        // 开始轮询的统计
                        mSendProgress = true
                        if (mCallBackListener != null) {
                            mCallBackListener?.onStart()
                            // 路径更换
                            mOldAudioPath = mAudioPath
                        }
                        LogUtil.e(AudioConstant.TAG, "start--->initialized：正常进行了播放！")
                        return true
                    }
                } else {
                    LogUtil.e(AudioConstant.TAG, "start--->initialized 为空，停止后续的操作！")
                    // 重新初始化
                    initResource()
                }
            }
        } catch (e: Exception) {
            LogUtil.e(AudioConstant.TAG, "start--->开始播放失败--->" + e.message)
            mCallBackListener?.onError(Exception("start--->" + e.message))
        }
        return false
    }

    /** 暂停 */
    fun pause(): Boolean {
        mediaPlayer = getMediaPlayer()
        val playing = this.isPlaying
        LogUtil.e(AudioConstant.TAG, "pause--->走入了暂停的方法中，playing:$playing")
        if (playing) {
            try {
                mediaPlayer!!.pause()
                // 更改状态
                STATUS_TYPE = AudioConstant.STATUS_PAUSE
                mCallBackListener?.onPause()
                // 暂停轮询的统计
                mSendProgress = false
                LogUtil.e(AudioConstant.TAG, "pause--->走入了暂停的方法中，成功暂停了！")
                return true
            } catch (e: Exception) {
                LogUtil.e("暂停失败")
                mCallBackListener?.onError(Exception("pause--->" + e.message))
                LogUtil.e(AudioConstant.TAG, "pause--->走入了暂停的方法中，暂停异常了--->" + e.message)
            }
        }
        return false
    }

    fun stop(): Boolean {
        LogUtil.e(AudioConstant.TAG, "stop--->走入了停止的方法中，initialized：$initialized")
        mediaPlayer = getMediaPlayer()
        try {
            if (mediaPlayer != null) {
                if (this.isPlaying) {
                    if (initialized) {
                        mediaPlayer?.pause()
                        mediaPlayer?.seekTo(0)
                        STATUS_TYPE = AudioConstant.STATUS_STOP
                        // 暂停轮询的统计
                        mSendProgress = false
                        mCallBackListener?.onStop()
                        // 重新去执行播放的资源
                        mOldAudioPath = ""
                        LogUtil.e(AudioConstant.TAG, "stop--->正常停止了播放")
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            LogUtil.e(AudioConstant.TAG, "stop--->停止失败--->" + e.message)
            mCallBackListener?.onError(Exception("stop--->" + e.message))
        }
        return false
    }

    val isPlaying: Boolean
        /** @return 当前是否是在播放中 */
        get() {
            mediaPlayer = getMediaPlayer()
            var playing = false
            // LogUtil.e(AudioConstant.TAG, "isPlaying--->走入了isPlaying的方法中，initialized:" +
            // initialized);
            try {
                if (mediaPlayer != null) {
                    if (initialized) {
                        playing = mediaPlayer!!.isPlaying
                    }
                }
            } catch (e: Exception) {
                LogUtil.e(AudioConstant.TAG, "isPlaying--->播放状态异常--->" + e.message)
                mCallBackListener?.onError(Exception("isPlaying--->" + e.message))
            }
            return playing
        }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        clear()
    }

    /** @param callBackListener 回调的监听 */
    fun setAudioCallBackListener(callBackListener: AudioPlayerCallBackListener?) {
        mCallBackListener = callBackListener
    }

    /** @param e 错误的处理 */
    private fun setErrorData(e: Exception) {
        LogUtil.e(AudioConstant.TAG, "setErrorData--->音频播放器错误：" + e.message)
        // 清空播放器，然后重新去搞一次
        clear()
        mCallBackListener?.onError(e)
        // 暂停轮询的统计
        mSendProgress = false
    }

    val duration: Int
        /** @return 获取资源的时长 */
        get() {
            LogUtil.e(AudioConstant.TAG, "getDuration--->走入了获取视频时长的方法中！")
            var result = 0
            try {
                if (mediaPlayer != null) {
                    if (initialized) {
                        result = mediaPlayer!!.duration
                        LogUtil.e(AudioConstant.TAG, "getDuration--->正常获取视频的时长:$result")
                    }
                }
            } catch (e: Exception) {
                LogUtil.e(AudioConstant.TAG, "getDuration--->异常了--->" + e.message)
            }
            return result
        }

    /** 加载的回调 */
    private val onPreparedListener: MediaPlayer.OnPreparedListener =
        object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(iMediaPlayer: MediaPlayer?) {
                STATUS_TYPE = AudioConstant.STATUS_PREPARED
                initialized = true
                LogUtil.e(AudioConstant.TAG, "onPrepared--->数据准备完成了！")
                mCallBackListener?.onPrepared()
                // 加载完毕，就开始播放
                start()
            }
        }

    /** 清空资源 */
    fun reset() {
        LogUtil.e(AudioConstant.TAG, "reset--->走入了清空资源的方法中！")
        mDuration = 0
        mediaPlayer?.reset()
        LogUtil.e(AudioConstant.TAG, "reset--->清空了资源！")
    }

    val progress: Unit
        /** 每隔1秒轮询一次当前的进度 */
        get() {
            disposableSubscriber?.dispose()
            disposableSubscriber =
                Flowable.interval(1000, TimeUnit.MILLISECONDS)
                    .filter(
                        object : Predicate<Long?> {
                            @Throws(Exception::class)
                            override fun test(aLong: Long): Boolean =
                                mSendProgress && initialized
                        }
                    )
                    .filter(
                        object : Predicate<Long?> {
                            @Throws(Exception::class)
                            override fun test(aLong: Long): Boolean =
                                (mediaPlayer != null) && isPlaying
                        }
                    )
                    .compose<Long?>(RxUtil.getSchedulerFlowable<Long?>())
                    .subscribeWith(
                        object : DisposableSubscriber<Long?>() {
                            override fun onNext(aLong: Long?) {
                                try {
                                    if (mCallBackListener != null) {
                                        val value: String?
                                        val currentPosition =
                                            mediaPlayer!!.currentPosition
                                        // 百分比 = 当前 / 总数
                                        if (currentPosition <= 0) {
                                            value = "0"
                                        } else {
                                            if (mDuration <= 0) {
                                                mDuration = duration
                                            }
                                            value =
                                                String.format(
                                                    Locale.CHINA,
                                                    "%.2f",
                                                    ((currentPosition * 1.0) / mDuration),
                                                )
                                        }
                                        mCallBackListener!!.onProgress(
                                            mDuration,
                                            currentPosition,
                                            value,
                                        )
                                    }
                                } catch (e: Exception) {
                                    LogUtil.e("getProgress ---->:" + e.message)
                                }
                            }

                            override fun onError(t: Throwable?) {}

                            override fun onComplete() {}
                        }
                    )
        }

    /** 清空资源 */
    fun clear() {
        LogUtil.e(AudioConstant.TAG, "clear--->走入了clear的方法中！")
        if (mediaPlayer != null) {
            if (initialized) {
                disposableSubscriber?.dispose()
                stop()
                mediaPlayer!!.release()
                LogUtil.e(AudioConstant.TAG, "clear--->正常清空了mediaPlayer！")
                mSendProgress = false
                STATUS_TYPE = AudioConstant.STATUS_IDLE
                initialized = false
                mediaPlayer = null
            }
        }
    }

    inner class AudioBinder : Binder(), AudioControlInterface {
        override fun getMediaPlayer(): MediaPlayer? = this@AudioService.getMediaPlayer()

        override fun getStatus(): Int = STATUS_TYPE

        override fun initialized(): Boolean = this@AudioService.initialized

        override fun setAudioResource(audioResource: String?) {
            this@AudioService.setResource(audioResource)
        }

        override fun start(): Boolean = this@AudioService.start()

        override fun pause(): Boolean = this@AudioService.pause()

        override fun stop(): Boolean = this@AudioService.stop()

        override fun isPlaying(): Boolean = this@AudioService.isPlaying

        override fun sendProgress(sendProgress: Boolean) {
            this@AudioService.mSendProgress = sendProgress
        }

        override fun setAudioCallBackListener(
            callBackListener: AudioPlayerCallBackListener?
        ) {
            this@AudioService.setAudioCallBackListener(callBackListener)
        }

        override fun getService(): AudioService = this@AudioService

        override fun clear() {
            this@AudioService.clear()
        }
    }
}
