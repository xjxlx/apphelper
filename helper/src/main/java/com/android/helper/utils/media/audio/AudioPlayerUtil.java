package com.android.helper.utils.media.audio;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.helper.utils.DateUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.TextViewUtil;

import static com.android.helper.utils.media.audio.AudioConstant.STATUS_ERROR;
import static com.android.helper.utils.media.audio.AudioConstant.STATUS_IDLE;

/**
 * 音频播放的工具类
 */
public class AudioPlayerUtil extends AudioPlayerCallBackListener {

    private AudioServiceConnection connection;
    private boolean bindService;
    private final Context context;
    private Intent intent;
    @SuppressLint("StaticFieldLeak")
    private static AudioService.AudioBinder audioBinder;
    private BindServiceListener mBindServiceListener;
    private SeekBar mSeekBar;
    private TextView mSeekBarProgressView, mSeekBarTotalView;
    private View mStartButton; // 开始按钮
    private AudioPlayerCallBackListener mCallBackListener;
    private String mAudioPath; // 播放的路径
    private boolean mAutoPlayer;// 是否自动播放

    public AudioPlayerUtil(Context context) {
        this.context = context;
    }

    /**
     * 绑定服务
     */
    public void bindService(BindServiceListener bindServiceListener) {
        this.mBindServiceListener = bindServiceListener;
        LogUtil.e(AudioConstant.TAG, "bindService--->开始绑定服务！");

        intent = new Intent(context, AudioService.class);
        if (connection == null) {
            connection = new AudioServiceConnection();
        }

        // 启动后台服务
        context.startService(intent);

        // 绑定前台的服务,禁止冲洗请的绑定
        if (!bindService) {
            bindService = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        LogUtil.e(AudioConstant.TAG, "bindService--->后台服务绑定成功：" + bindService);
    }

    /**
     * 解绑服务
     */
    public void unBindService() {
        if (bindService) {
            context.unbindService(connection);
            bindService = false;
        }
    }

    /**
     * @param audioPath 播放地址
     */
    public void setResource(String audioPath) {
        this.mAudioPath = audioPath;
        if (audioBinder != null) {
            audioBinder.setAudioResource(audioPath);
        }
    }

    /**
     * @param autoPlayer 是否自动播放
     */
    public void autoPlayer(boolean autoPlayer) {
        this.mAutoPlayer = autoPlayer;
    }

    public void start() {
        if (audioBinder != null) {
            audioBinder.start();
        }
    }

    public void pause() {
        if (audioBinder != null) {
            audioBinder.pause();
        }
    }

    public void stop() {
        if (audioBinder != null) {
            audioBinder.stop();
        }
    }

    /**
     * 页面停止不可见时候的处理
     */
    public void destroy() {
        if (bindService) {
            unBindService();
        }

        // 停止后台的服务
        context.stopService(intent);
        audioBinder = null;
        bindService = false;
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
            if (service instanceof AudioService.AudioBinder) {
                audioBinder = (AudioService.AudioBinder) service;
                LogUtil.e(AudioConstant.TAG, "onServiceConnected--->服务回调成功：" + (audioBinder));

                if (audioBinder != null) {

                    // 生命周期的回调
                    if (AudioPlayerUtil.this.mBindServiceListener != null) {
                        AudioPlayerUtil.this.mBindServiceListener.bindResult(bindService);
                    }

                    audioBinder.setAudioCallBackListener(AudioPlayerUtil.this);

                    setSeekBar(mSeekBar);
                    setStartButton(mStartButton);
                }

                // 绑定成功后自动播放
                if (mAutoPlayer) {
                    LogUtil.e(AudioConstant.TAG, "onServiceConnected--->服务回调成功,开始自动播放！");
                    if (!TextUtils.isEmpty(mAudioPath)) {
                        setResource(mAudioPath);
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

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

        if (audioBinder == null) {
            return;
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MediaPlayer mediaPlayer = audioBinder.getMediaPlayer();
                    if (mediaPlayer != null) {
                        int status = audioBinder.getStatus();
                        if ((status != STATUS_IDLE) && (status != STATUS_ERROR)) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                audioBinder.sendProgress(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioBinder.sendProgress(true);
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
        if (audioBinder == null) {
            return;
        }

        // 播放按钮的点击事件
        view.setOnClickListener(v -> {
            boolean initialized = audioBinder.initialized();
            if (initialized) {
                if (audioBinder.isPlaying()) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.e("onPause");
        switchStartButton(false);
        if (mCallBackListener != null) {
            mCallBackListener.onPause();
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
}
