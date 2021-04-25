package com.android.helper.utils.media.audio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.android.helper.R;
import com.android.helper.httpclient.RxUtil;
import com.android.helper.utils.BitmapUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.NotificationUtil;
import com.android.helper.utils.ToastUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.DisposableSubscriber;

import static android.media.MediaPlayer.MEDIA_ERROR_IO;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_LEFT;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_PAUSE;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_RIGHT;
import static com.android.helper.utils.media.audio.AudioConstant.ACTION_START;
import static com.android.helper.utils.media.audio.AudioConstant.CODE_SEND_BROADCAST_RECEIVER;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_COMPLETE;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_ERROR;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_IDLE;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_PAUSE;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_PLAYING;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_PREPARED;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_STOP;

public class AudioService extends Service {

    // 当前的状态，默认等于闲置的状态
    private int STATUS_TYPE = STATUS_IDLE;

    private Context context;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private String mAudioPath, mOldAudioPath;
    private AudioPlayerCallBackListener mCallBackListener;
    private int mDuration; // 时长
    private DisposableSubscriber<Long> disposableSubscriber;
    private boolean mSendProgress = true;// 是否正常发送当前的进度，默认为true
    private boolean initialized; // 是否已经完成了初始化
    private NotificationUtil mNotificationUtil;
    private AudioReceiver mAudioReceiver;
    private RemoteViews mRemoteViews;

    private int mNotificationStart;         // 消息通知栏开始的按钮
    private int mNotificationPause;         // 消息通知栏暂停的按钮
    private int mNotificationLeft;          // 消息通知栏左侧的按钮
    private int mNotificationRight;         // 消息通知栏右侧的按钮

    private String mNotificationImage;      // 消息通知栏左侧的图标
    private String mNotificationTitle;      // 消息通知栏上方的标题
    private List<AudioEntity> mAudioList;   // 消息通知栏使用到的数据列表
    private int mAudioPosition = -1;             // 消息通知栏当前按播放音频的角标,默认的值是-1

    public AudioService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialized = false;

        if (context == null) {
            context = getApplication();
        }

        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        // 创建对象
        mediaPlayer = getMediaPlayer();

        // 动态注册广播接收者
        if (mAudioReceiver == null) {
            mAudioReceiver = new AudioReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AudioConstant.ACTION_START);
            intentFilter.addAction(AudioConstant.ACTION_PAUSE);
            intentFilter.addAction(AudioConstant.ACTION_LEFT);
            intentFilter.addAction(AudioConstant.ACTION_RIGHT);
            // 注册
            registerReceiver(mAudioReceiver, intentFilter);
        }

        try {
            if (mNotificationUtil == null) {
                mNotificationUtil = NotificationUtil.getInstance(getBaseContext());
                mNotificationUtil
                        .setTickerText("首次出现在通知栏")
                        .setContentTitle("消息通知栏")
                        .setContentText("消息的内容")
                        .setSmallIcon(R.drawable.icon_left_right)
                        .setLockScreenVisibility(true)
                        .setNotificationLevel(NotificationManager.IMPORTANCE_MAX)
                        .setRemoteView(R.layout.notification_audio, (view, remoteViews) -> {
                            if (remoteViews != null) {
                                this.mRemoteViews = remoteViews;

                                // 左侧的按钮
                                if (mNotificationLeft != 0) {
                                    remoteViews.setImageViewResource(R.id.iv_to_left, mNotificationLeft);
                                }

                                // 中间的按钮
                                if (isPlaying()) {
                                    if (mNotificationPause != 0) {
                                        mRemoteViews.setImageViewResource(R.id.iv_start, mNotificationPause);
                                    }
                                } else {
                                    if (mNotificationStart != 0) {
                                        mRemoteViews.setImageViewResource(R.id.iv_start, mNotificationStart);
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
                                PendingIntent btPendingIntentStart = PendingIntent.getBroadcast(context, CODE_SEND_BROADCAST_RECEIVER, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
                                remoteViews.setOnClickPendingIntent(R.id.iv_start, btPendingIntentStart);

                                // 左侧按钮点击事件的处理
                                Intent intentLeft = new Intent();
                                intentLeft.setAction(ACTION_LEFT);
                                PendingIntent btPendingIntentLeft = PendingIntent.getBroadcast(context, CODE_SEND_BROADCAST_RECEIVER, intentLeft, PendingIntent.FLAG_UPDATE_CURRENT);
                                remoteViews.setOnClickPendingIntent(R.id.iv_to_left, btPendingIntentLeft);

                                // 左侧按钮点击事件的处理
                                Intent intentRight = new Intent();
                                intentRight.setAction(ACTION_RIGHT);
                                PendingIntent btPendingIntentRight = PendingIntent.getBroadcast(context, CODE_SEND_BROADCAST_RECEIVER, intentRight, PendingIntent.FLAG_UPDATE_CURRENT);
                                remoteViews.setOnClickPendingIntent(R.id.iv_to_right, btPendingIntentRight);
                            }
                        })
                        .createNotification()
                        .sendNotification(1)
                        .startForeground(1, this);
            }
        } catch (Exception e) {
            LogUtil.e("------------->:" + e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AudioBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 创建对象
        mediaPlayer = getMediaPlayer();

        return super.onStartCommand(intent, flags, startId);
    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            LogUtil.e(AudioConstant.TAG, "getMediaPlayer--->重新构建了MediaPlayer");
        }
        return mediaPlayer;
    }

    /**
     * 初始化监听器
     */
    public void initListener() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            mediaPlayer.setOnErrorListener(onErrorListener);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnInfoListener(mInfoListener);
            mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);

            // 获取当前的进度
            getProgress();
        }
    }

    /**
     * @param audioPath 设置播放资源
     */
    public void setResource(String audioPath) {

        if (TextUtils.isEmpty(audioPath)) {
            ToastUtil.show("播放地址不能为空！");
            LogUtil.e(AudioConstant.TAG, "setResource--->播放地址为空");
            return;
        }
        mAudioPath = audioPath;
        LogUtil.e(AudioConstant.TAG, "setResource--->播放地址为:" + audioPath);

        // 开始去播放资源
        player();
    }

    /**
     * 开始去准备播放音频
     */
    public void player() {
        // 清空数据

        mediaPlayer = getMediaPlayer();

        if (!TextUtils.equals(mAudioPath, mOldAudioPath)) {
            LogUtil.e(AudioConstant.TAG, "player--->播放地址不相同，执行后续的逻辑！");

            initResource();
        } else {
            // 如果路径相同的时候，才去判读当前的状态
            start();
        }
    }

    private void initResource() {
        if (!TextUtils.isEmpty(mAudioPath)) {
            try {
                reset();

                // 初始化监听
                initListener();

                // 指定参数为音频文件
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(mAudioPath);//为多媒体对象设置播放路径
                mediaPlayer.prepareAsync();//异步准备（准备播放
                LogUtil.e(AudioConstant.TAG, "player--->重新重置了资源，并设置了数据！");

            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e(AudioConstant.TAG, "player--->设置数据异常：" + e.getMessage());

                if (mCallBackListener != null) {
                    mCallBackListener.onError(new Exception("player--->" + e.getMessage()));
                }
            }
        } else {
            ToastUtil.show("播放地址不能为空！");
        }
    }

    /**
     * 开始播放
     */
    public void start() {
        try {
            mediaPlayer = getMediaPlayer();
            if (mediaPlayer != null) {
                // 无论是开始还是暂停，都不能是在闲置状态的时候去执行，否则就会异常
                LogUtil.e(AudioConstant.TAG, "start--->initialized：" + initialized);
                if (initialized) {
                    if (isPlaying()) { // 播放中的状态
                        // 暂停播放
                        pause();
                    } else {
                        // 暂停状态或者其他状态下，直接开始
                        mediaPlayer.start();
                        STATUS_TYPE = STATUS_PLAYING;

                        // 开始轮询的统计
                        mSendProgress = true;

                        if (mCallBackListener != null) {
                            mCallBackListener.onStart();
                            // 路径更换
                            mOldAudioPath = mAudioPath;
                        }
                        LogUtil.e(AudioConstant.TAG, "start--->initialized：正常进行了播放！");

                        // 获取当前view的角标
                        getCurrentPositionForUrl();

                        // 更改view的图标
                        if (mRemoteViews != null) {
                            mRemoteViews.setImageViewResource(R.id.iv_start, mNotificationPause);

                            // 左侧图片的资源
                            BitmapUtil.getBitmapForService(context, mNotificationImage, (successful, tag, bitmap) -> {
                                if (tag != null && tag.equals(BitmapUtil.STATUS_SUCCESS)) {
                                    mRemoteViews.setImageViewBitmap(R.id.iv_launcher, bitmap);
                                }
                            });

                            // 中间的标题头
                            if (!TextUtils.isEmpty(mNotificationTitle)) {
                                mRemoteViews.setTextViewText(R.id.tv_title, mNotificationTitle);
                                mRemoteViews.setTextColor(R.id.tv_title, Color.BLACK);
                                mRemoteViews.setTextViewTextSize(R.id.tv_title, TypedValue.COMPLEX_UNIT_SP, 16);
                            }
                        }

                        // 发送间隔的轮询
                        if (mNotificationUtil != null) {
                            mNotificationUtil.startLoopForeground(1, 5000, this);
                        }

                    }
                } else {
                    LogUtil.e(AudioConstant.TAG, "start--->initialized 为空，停止后续的操作！");

                    // 重新初始化
                    initResource();
                }
            }
        } catch (Exception e) {
            LogUtil.e(AudioConstant.TAG, "start--->开始播放失败--->" + e.getMessage());
            if (mCallBackListener != null) {
                mCallBackListener.onError(new Exception("start--->" + e.getMessage()));
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        mediaPlayer = getMediaPlayer();
        boolean playing = isPlaying();
        LogUtil.e(AudioConstant.TAG, "pause--->走入了暂停的方法中，playing:" + playing);
        if (playing) {
            try {
                mediaPlayer.pause();
                // 更改状态
                STATUS_TYPE = STATUS_PAUSE;

                if (mCallBackListener != null) {
                    mCallBackListener.onPause();
                }

                // 暂停轮询的统计
                mSendProgress = false;

                LogUtil.e(AudioConstant.TAG, "pause--->走入了暂停的方法中，成功暂停了！");

                if (mRemoteViews != null) {
                    if (mNotificationStart != 0) {
                        mRemoteViews.setImageViewResource(R.id.iv_start, mNotificationStart);
                    }
                }

                // 发送间隔的轮询
                if (mNotificationUtil != null) {
                    mNotificationUtil.startForeground(1, AudioService.this);
                }

            } catch (Exception e) {
                LogUtil.e("暂停失败");
                if (mCallBackListener != null) {
                    mCallBackListener.onError(new Exception("pause--->" + e.getMessage()));
                }
                LogUtil.e(AudioConstant.TAG, "pause--->走入了暂停的方法中，暂停异常了--->" + e.getMessage());
            }
        }
    }

    public void stop() {
        LogUtil.e(AudioConstant.TAG, "stop--->走入了停止的方法中，initialized：" + initialized);
        mediaPlayer = getMediaPlayer();
        try {
            if (mediaPlayer != null) {
                if (isPlaying()) {
                    if (initialized) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);

                        STATUS_TYPE = STATUS_STOP;

                        // 暂停轮询的统计
                        mSendProgress = false;

                        if (mCallBackListener != null) {
                            mCallBackListener.onStop();
                        }

                        // 重新去执行播放的资源
                        mOldAudioPath = "";
                        LogUtil.e(AudioConstant.TAG, "stop--->正常停止了播放");
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(AudioConstant.TAG, "stop--->停止失败--->" + e.getMessage());
            if (mCallBackListener != null) {
                mCallBackListener.onError(new Exception("stop--->" + e.getMessage()));
            }
        }
    }

    /**
     * @return 当前是否是在播放中
     */
    public boolean isPlaying() {
        mediaPlayer = getMediaPlayer();
        boolean playing = false;
        LogUtil.e(AudioConstant.TAG, "isPlaying--->走入了isPlaying的方法中，initialized:" + initialized);
        try {
            if (mediaPlayer != null) {
                if (initialized) {
                    playing = mediaPlayer.isPlaying();
                    LogUtil.e(AudioConstant.TAG, "isPlaying--->正常获取了播放的状态，状态为：" + playing);
                }
            }
        } catch (Exception e) {
            LogUtil.e(AudioConstant.TAG, "isPlaying--->播放状态异常--->" + e.getMessage());
            if (mCallBackListener != null) {
                mCallBackListener.onError(new Exception("isPlaying--->" + e.getMessage()));
            }
        }
        return playing;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 停止间隔的轮询
        if (mNotificationUtil != null) {
            mNotificationUtil.stopLoopForeground();
        }

        // 解除注册广播接收者
        if (mAudioReceiver != null) {
            unregisterReceiver(mAudioReceiver);
        }

        stop();
        clear();
    }

    class AudioBinder extends Binder implements AudioControlInterface {
        @Override
        public MediaPlayer getMediaPlayer() {
            return AudioService.this.getMediaPlayer();
        }

        @Override
        public int getStatus() {
            return STATUS_TYPE;
        }

        @Override
        public boolean initialized() {
            return AudioService.this.initialized;
        }

        @Override
        public void setAudioResource(String audioResource) {
            AudioService.this.setResource(audioResource);
        }

        @Override
        public void start() {
            AudioService.this.start();
        }

        @Override
        public void pause() {
            AudioService.this.pause();
        }

        @Override
        public void stop() {
            AudioService.this.stop();
        }

        @Override
        public boolean isPlaying() {
            return AudioService.this.isPlaying();
        }

        @Override
        public void sendProgress(boolean sendProgress) {
            AudioService.this.mSendProgress = sendProgress;
        }

        @Override
        public void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener) {
            AudioService.this.setAudioCallBackListener(callBackListener);
        }

        @Override
        public void setNotificationIcon(int notificationStart, int notificationPause, int notificationLeft, int notificationRight) {
            AudioService.this.mNotificationStart = notificationStart;
            AudioService.this.mNotificationPause = notificationPause;
            AudioService.this.mNotificationLeft = notificationLeft;
            AudioService.this.mNotificationRight = notificationRight;
        }

        @Override
        public void setNotificationMessage(String notificationImage, String notificationTitle) {
            AudioService.this.mNotificationImage = notificationImage;
            AudioService.this.mNotificationTitle = notificationTitle;
        }

        @Override
        public void setNotificationList(List<AudioEntity> list) {
            AudioService.this.mAudioList = list;
        }

        @Override
        public void clear() {
            AudioService.this.clear();
        }
    }

    /**
     * @param callBackListener 回调的监听
     */
    public void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener) {
        mCallBackListener = callBackListener;
    }

    /**
     * 加载的回调
     */
    private final MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer iMediaPlayer) {
            STATUS_TYPE = STATUS_PREPARED;
            initialized = true;
            LogUtil.e(AudioConstant.TAG, "onPrepared--->数据准备完成了！");

            if (mCallBackListener != null) {
                mCallBackListener.onPrepared();
            }

            // 加载完毕，就开始播放
            start();
        }
    };

    private final MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer iMediaPlayer, int what, int extra) {
            LogUtil.e(AudioConstant.TAG, "onError--->发生了错误！ what:" + what);

            STATUS_TYPE = STATUS_ERROR;

            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    // 未指定的媒体播放器错误。
                    setErrorData(new Exception("未指定的媒体播放器错误"));
                    break;

                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    // 媒体服务器死了。在这种情况下，应用程序必须释放
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    setErrorData(new Exception("媒体服务器死了"));
                    break;

                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    // 视频流，其容器对逐行扫描无效。
                    setErrorData(new Exception("视频流，其容器对逐行扫描无效"));
                    break;

                case MEDIA_ERROR_IO:// IO刘错误。
                    setErrorData(new Exception("IO刘错误"));
                    break;

                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    // 位流不符合相关编码标准或文件规范。
                    setErrorData(new Exception("位流不符合相关编码标准或文件规范"));
                    break;

                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    //超时
                    setErrorData(new Exception("链接超时"));
                    break;

                default: //  默认的错误
                    // 如果一旦遇到了这个错误，则需要整体重置一下播放器
                    clear();
                    break;
            }
            LogUtil.e("what:" + what + " --- extra:" + extra);
            return true;
        }
    };

    /**
     * @param e 错误的处理
     */
    private void setErrorData(Exception e) {
        LogUtil.e(AudioConstant.TAG, "setErrorData--->音频播放器错误：" + e.getMessage());
        // 清空播放器，然后重新去搞一次
        clear();

        if (mCallBackListener != null) {
            mCallBackListener.onError(e);
        }
        // 暂停轮询的统计
        mSendProgress = false;
    }

    /**
     * 播放完成的监听
     */
    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer iMediaPlayer) {
            STATUS_TYPE = STATUS_COMPLETE;
            LogUtil.e(AudioConstant.TAG, "onCompletion--->播放完成了！");
            // 暂停轮询的统计
            mSendProgress = false;
            /*
             * 置空临时变量，让程序重新开始,这一步非常关键，
             * 如果不置空，那么就会出现在播放完成之后，点击播放按钮程序无法重新开始的情况
             */
            if (mCallBackListener != null) {
                mCallBackListener.onComplete();
            }
        }
    };

    //  播放信息的回调
    private final MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer mp, int arg1, int arg2) {

            switch (arg1) {
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    //   LogUtil.e("media_info_video_track_lagging:");
                    break;

                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    //   LogUtil.e("视频准备渲染!");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    LogUtil.e("开始缓冲！");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    LogUtil.e("缓冲结束！");
                    break;

                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    //    LogUtil.e("media_info_bad_interleaving:");
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    // LogUtil.e("media_info_not_seekable:");
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    //   LogUtil.e("media_info_metadata_update:");
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    //   LogUtil.e("media_info_unsupported_subtitle:");
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    //   LogUtil.e("media_info_subtitle_timed_out:");
                    break;

                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    //   LogUtil.e("media_error_timed_out:网络连接超时！");
                    setErrorData(new Exception("网络连接超时"));
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    //     LogUtil.e("media_error_unsupported:数据不支持！");
                    setErrorData(new Exception("数据不支持"));
                    break;
                case MEDIA_ERROR_IO:
                    //     LogUtil.e("media_error_unsupported:IO错误！");
                    setErrorData(new Exception("IO流错误"));
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    //     LogUtil.e("media_error_unsupported:视频中断，一般是视频源异常或者不支持的视频类型！");
                    setErrorData(new Exception("视频中断，音频源异常"));
                    break;
                case -1000:
                    //      LogUtil.e("一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的！");
                    setErrorData(new Exception("视频中断，音频格式错误"));
                    break;
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    //     LogUtil.e("数据错误没有有效的回收！");
                    break;
            }
            return true;
        }
    };

    // 播放进度的回调
    private final MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

            LogUtil.e(AudioConstant.TAG, "onBufferingUpdate--->播放进度的回调，当前的缓冲百分比为:" + percent);

            if (mDuration <= 0) {
                mDuration = getDuration();
            }
            // 计算出当前的缓冲比例
            double percentFloat = percent / 100d;// 注意：两个整数相除的结果是整数
            // 缓冲比例 乘以 总数大小 ==  当前缓冲的进度
            double currentProgress = mDuration * percentFloat;

            if (mCallBackListener != null) {
                mCallBackListener.onBufferProgress(mDuration, currentProgress, percent);
            }
        }
    };

    /**
     * @return 获取资源的时长
     */
    public int getDuration() {
        LogUtil.e(AudioConstant.TAG, "getDuration--->走入了获取视频时长的方法中！");
        int result = 0;
        try {
            if (mediaPlayer != null) {
                if (initialized) {
                    result = mediaPlayer.getDuration();
                    LogUtil.e(AudioConstant.TAG, "getDuration--->正常获取视频的时长:" + result);
                }
            }
        } catch (Exception e) {
            LogUtil.e(AudioConstant.TAG, "getDuration--->异常了--->" + e.getMessage());
        }

        return result;
    }

    /**
     * 清空资源
     */
    public void reset() {
        LogUtil.e(AudioConstant.TAG, "reset--->走入了清空资源的方法中！");
        mDuration = 0;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            LogUtil.e(AudioConstant.TAG, "reset--->清空了资源！");
        }
    }

    /**
     * 清空资源
     */
    public void clear() {
        LogUtil.e(AudioConstant.TAG, "clear--->走入了clear的方法中！");

        if (mediaPlayer != null) {
            if (initialized) {
                if (disposableSubscriber != null) {
                    disposableSubscriber.dispose();
                }

                stop();
                mediaPlayer.release();
                LogUtil.e(AudioConstant.TAG, "clear--->正常清空了mediaPlayer！");
                mSendProgress = false;
                STATUS_TYPE = STATUS_IDLE;
                initialized = false;
                mediaPlayer = null;
            }
        }

    }

    /**
     * 每隔1秒轮询一次当前的进度
     */
    public void getProgress() {
        disposableSubscriber = Flowable.interval(1000, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(@NonNull Long aLong) throws Exception {
                        return mSendProgress && initialized;
                    }
                })
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(@NonNull Long aLong) throws Exception {
                        return (mediaPlayer != null) && isPlaying();
                    }
                }).compose(RxUtil.getScheduler())
                .subscribeWith(new DisposableSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        try {
                            if (mCallBackListener != null) {
                                String value;

                                int currentPosition = mediaPlayer.getCurrentPosition();
                                // 百分比 = 当前 / 总数
                                if (currentPosition <= 0) {
                                    value = "0";
                                } else {
                                    if (mDuration <= 0) {
                                        mDuration = getDuration();
                                    }
                                    value = String.format(Locale.CHINA, "%.2f", ((currentPosition * 1.0d) / mDuration));
                                }
                                mCallBackListener.onProgress(mDuration, currentPosition, value);
                            }
                        } catch (Exception e) {
                            LogUtil.e("getProgress ---->:" + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

    public class AudioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                boolean playing = isPlaying();
                LogUtil.e("-------------------------------->AudioReceiver ---> onReceive:" + action + "   --->player:" + playing);

                switch (action) {
                    case ACTION_START:
                    case ACTION_PAUSE:

                        if (initialized) {
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
}