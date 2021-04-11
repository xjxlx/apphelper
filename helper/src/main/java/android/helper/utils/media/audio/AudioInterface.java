package android.helper.utils.media.audio;

public interface AudioInterface {
    /**
     * @param audioResource 音频播放的资源
     */
    void setAudioResource(String audioResource);

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    void autoPlayer(boolean autoPlayer);

    void setAudioCallBackListener(AudioPlayerCallBackListener callBackListener);

    void clear();
}
