package com.android.helper.utils.media.audio;

public class AudioPlayerCallBackListener {

    /**
     * 数据加载完成了
     */
    public void onPrepared() {

    }

    /**
     * 开始播放
     */
    public void onStart() {

    }

    /**
     * 暂停播放
     */
    public void onPause() {

    }

    ;

    /**
     * 停止播放
     */
    public void onStop() {

    }

    /**
     * 播放错误
     */
    public void onError(Exception e) {

    }

    /**
     * 播放结束
     */
    public void onComplete() {

    }

    /**
     * 当前缓冲的进度
     *
     * @param total   总长度
     * @param current 当前的进度
     * @param percent 百分比
     */
    public void onBufferProgress(int total, double current, int percent) {

    }

    /**
     * @param total   总长度
     * @param current 当前的进度
     * @param percent 百分比
     */
    public void onProgress(int total, int current, String percent) {

    }

}
