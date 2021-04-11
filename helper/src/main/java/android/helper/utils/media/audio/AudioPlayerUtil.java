package android.helper.utils.media.audio;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.helper.utils.DateUtil;
import android.helper.utils.LogUtil;
import android.helper.utils.TextViewUtil;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.helper.utils.media.audio.AudioConstant.STATUS_ERROR;
import static android.helper.utils.media.audio.AudioConstant.STATUS_IDLE;

/**
 * 音频播放的工具类
 */
public class AudioPlayerUtil implements AudioProgressListener, AudioPlayerCallBackListener {

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

    public AudioPlayerUtil(Context context) {
        this.context = context;
    }

    /**
     * 绑定服务
     */
    public void bindService(BindServiceListener bindServiceListener) {
        this.mBindServiceListener = bindServiceListener;
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
    }

    /**
     * 解绑服务
     */
    public void unBindService() {
        if (bindService) {
            context.unbindService(connection);
            bindService = false;
        }
        // 停止后台的服务
        context.stopService(intent);
    }

    /**
     * @param audioPath 播放地址
     */
    public void setResource(String audioPath) {
        if (audioBinder != null) {
            audioBinder.setAudioResource(audioPath);
        }
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

    public void clear() {
        if (audioBinder != null) {
            audioBinder.clear();
            LogUtil.e("clear--->释放播放器");
        }
    }

    /**
     * 页面停止不可见时候的处理
     */
    public void destroy() {
        if (audioBinder != null) {
            boolean playing = audioBinder.isPlaying();
            if (!playing) {
                unBindService();

                clear();
            }
        }
    }

    class AudioServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof AudioService.AudioBinder) {
                audioBinder = (AudioService.AudioBinder) service;
                if ((audioBinder != null) && (AudioPlayerUtil.this.mBindServiceListener != null)) {
                    AudioPlayerUtil.this.mBindServiceListener.bindResult(bindService);

                    // 生命周期的回调
                    audioBinder.setAudioCallBackListener(AudioPlayerUtil.this);

                    // 回调进度
                    audioBinder.setProgressListener(AudioPlayerUtil.this);
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
        if (seekBar == null || audioBinder == null) {
            return;
        }
        this.mSeekBar = seekBar;

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
            CharSequence totalContent = mSeekBarTotalView.getText();
            if (TextUtils.isEmpty(totalContent)) {
                CharSequence charSequence = DateUtil.formatMillis(total);
                TextViewUtil.setText(mSeekBarTotalView, charSequence);
            }

            // 设置默认的进度
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

    @Override
    public void onProgress(int total, int current, String percent) {
        LogUtil.e("onProgress:-->total:" + total + "  --->current:" + current + " --->percent:" + percent);
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
    }

    @Override
    public void onPrepared() {
        if (mCallBackListener != null) {
            mCallBackListener.onPrepared();
        }
    }

    @Override
    public void onStart() {
        if (mCallBackListener != null) {
            mCallBackListener.onStart();
        }

        switchStartButton(true);
    }

    @Override
    public void onPause() {
        if (mCallBackListener != null) {
            mCallBackListener.onPause();
        }

        switchStartButton(false);
    }

    @Override
    public void onStop() {
        if (mCallBackListener != null) {
            mCallBackListener.onStop();
        }

        switchStartButton(false);
    }

    @Override
    public void onError(Exception e) {
        if (mCallBackListener != null) {
            mCallBackListener.onError(e);
        }

        switchStartButton(false);
    }

    @Override
    public void onComplete() {
        if (mCallBackListener != null) {
            mCallBackListener.onComplete();
        }
        switchStartButton(false);
    }
}
