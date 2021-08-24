package com.android.helper.utils.media.audio;

import android.media.MediaPlayer;

public interface AudioControlInterface {

    MediaPlayer getMediaPlayer();

    int getStatus();

    /**
     * @return 是否初始化成功了
     */
    boolean initialized();

    /**
     * @param audioResource 音频播放的资源
     */
    void setAudioResource(String audioResource);

    boolean start();

    boolean pause();

    boolean stop();

    boolean isPlaying();

    void sendProgress(boolean sendProgress);

    void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener);

    AudioService getService();

    void clear();

}
