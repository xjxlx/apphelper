package android.helper.utils.media.audio;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * 音频播放的工具类
 */
public class AudioPlayerUtil {

    private AudioServiceConnection connection;
    private boolean bindService;
    private final Context context;
    private Intent intent;
    @SuppressLint("StaticFieldLeak")
    private static AudioService.AudioBinder audioBinder;
    private BindServiceListener mBindServiceListener;

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

    public void setAutoPlayer(boolean autoPlayer) {
        if (audioBinder != null) {
            audioBinder.autoPlayer(autoPlayer);
        }
    }

    public void clear() {
        if (audioBinder != null) {
            audioBinder.clear();
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
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    public void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener) {
        if (audioBinder != null) {
            audioBinder.setAudioCallBackListener(callBackListener);
        }
    }
}
