package com.android.helper.utils.media.audio;

public class AudioConstant {

    public static final String TAG = "AudioPlayer";

    /**
     * 状态 --- 闲置的状态
     */
    public static final int STATUS_IDLE = 0;

    /**
     * 状态 --- 数据准备好了
     */
    public static int STATUS_PREPARED = 1;

    /**
     * 状态 --- 播放中
     */
    public static int STATUS_PLAYING = 2;

    /**
     * 状态 --- 暂停了播放
     */
    public static final int STATUS_PAUSE = 3;

    /**
     * 状态 --- 状态错误
     */
    public static final int STATUS_ERROR = 4;

    /**
     * 状态 --- 停止了播放
     */
    public static final int STATUS_STOP = 5;

    /**
     * 状态 --- 播放完成了
     */
    public static final int STATUS_COMPLETE = 6;

}
