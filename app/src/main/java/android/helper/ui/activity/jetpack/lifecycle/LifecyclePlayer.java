package android.helper.ui.activity.jetpack.lifecycle;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.android.helper.interfaces.TagListener;
import com.android.helper.utils.ClassUtil;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.jetpack.lifecycle.BaseLifecycleObserver;

import java.io.IOException;

/**
 * 测试lifecycle监听生命周期的播放器
 */
public class LifecyclePlayer implements BaseLifecycleObserver, TagListener {

    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;// 是否已经准备好了

    @Override
    public void onCreate() {
        LogUtil.e(getTag(), "Create");
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onStart() {
        LogUtil.e(getTag(), "Start");
    }

    @Override
    public void onResume() {
        LogUtil.e(getTag(), "Resume");
    }

    @Override
    public void onPause() {
        LogUtil.e(getTag(), "Pause");
    }

    @Override
    public void onStop() {
        LogUtil.e(getTag(), "Stop");
    }

    @Override
    public void onDestroy() {
        LogUtil.e(getTag(), "Destroy");

        if (mMediaPlayer != null) {
            if (isPrepared) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public String getTag() {
        return ClassUtil.getClassName(this);
    }

    public void start(String url) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }

            mMediaPlayer.reset();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPrepared = true;
                    mp.start();
                }
            });

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);//为多媒体对象设置播放路径
            mMediaPlayer.prepareAsync();//异步准备（准备播放
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                if (isPrepared) {
                    mMediaPlayer.stop();
                }
            }
        }
    }

}
