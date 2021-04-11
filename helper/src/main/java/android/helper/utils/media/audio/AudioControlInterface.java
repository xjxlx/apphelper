package android.helper.utils.media.audio;

import android.media.MediaPlayer;

public interface AudioControlInterface {

    MediaPlayer getMediaPlayer();

    int getStatus();

    /**
     * @param audioResource 音频播放的资源
     */
    void setAudioResource(String audioResource);

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    void sendProgress(boolean sendProgress);

    void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener);

    void clear();

}
