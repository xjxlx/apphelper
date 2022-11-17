package com.android.helper.utils;

/**
 * @author : 流星
 * @CreateDate: 2022/10/27-16:55
 * @Description:倒计时工具类
 */
public class CountDownUtil {
    private CountdownTimer mCountdownTimer;

    public static CountDownUtil getInstance() {
        return new CountDownUtil();
    }

    /**
     * 开始倒计时
     *
     * @param interval      间隔时间：秒
     * @param countdownTime 总时长：秒
     * @param callback      回调
     */
    public void startCountDown(int interval, int countdownTime, CountdownTimer.Callback callback) {
        if (mCountdownTimer == null) {
            mCountdownTimer = new CountdownTimer(interval, countdownTime, callback);
            mCountdownTimer.start();
        }
    }

    public boolean isPause() {
        return mCountdownTimer.isPause();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mCountdownTimer != null) {
            mCountdownTimer.pause();

            mCountdownTimer.cancel();
        }
    }

    public void cancel() {
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
        }
    }

    /**
     * 重新开始
     */
    public void resume() {
        if (mCountdownTimer != null) {
            mCountdownTimer.resume();
        }
    }

}
