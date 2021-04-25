package com.android.helper.utils.media.audio;

import android.media.MediaPlayer;

import androidx.annotation.DrawableRes;

import java.util.List;

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

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    void sendProgress(boolean sendProgress);

    void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener);

    /**
     * @param notificationStart notification开始的按钮
     * @param notificationPause notification暂停的按钮
     * @param notificationLeft  notification左侧的按钮
     * @param notificationRight notification右侧的按钮
     */
    void setNotificationIcon(@DrawableRes int notificationStart, @DrawableRes int notificationPause, @DrawableRes int notificationLeft, @DrawableRes int notificationRight);

    /**
     * @param notificationImage notification左侧的图标
     * @param notificationTitle notification的标题
     */
    void setNotificationMessage(String notificationImage, String notificationTitle);

    void setNotificationList(List<AudioEntity> list);

    void clear();

}
