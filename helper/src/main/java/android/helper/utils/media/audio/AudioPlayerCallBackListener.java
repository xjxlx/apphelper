package android.helper.utils.media.audio;

/**
 * 音乐播放器的回调对象
 */
public interface AudioPlayerCallBackListener {

    /**
     * 数据加载完成了
     */
    void onPrepared();

    /**
     * 开始播放
     */
    void onStart();

    /**
     * 暂停播放
     */
    void onPause();

    /**
     * 停止播放
     */
    void onStop();

    /**
     * 播放错误
     */
    void onError(Exception e);

    /**
     * 播放结束
     */
    void onComplete();

}
