package com.android.helper.utils;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimer extends Timer {

    private long interval; // time in milliseconds
    private long totalTime; // time in milliseconds
    private long remainTime;
    private long elapsedTime = 0;
    private Callback callback;
    public boolean pause = false;
    private CountdownTimer this_;

    public CountdownTimer(int interval, int countdownTime, Callback callback) {
        this.interval = interval;
        this.totalTime = countdownTime;
        this.callback = callback;
        this_ = this;
    }

    public void start() {
        super.schedule(new CountdownTask(), 0, interval);
    }

    public void pause() {
        this.pause = true;
    }

    public void resume() {
        this.pause = false;
    }

    public boolean isPause() {
        return this.pause;
    }

    public interface Callback {
        void onTick(long elapsedTime, long remainTime);

        void onFinish();
    }

    public class CountdownTask extends TimerTask {

        @Override
        public void run() {
            if (!pause) {
                elapsedTime = interval + elapsedTime;
                remainTime = totalTime - elapsedTime;
            }
            if (elapsedTime > totalTime) {
                this_.cancel();
                callback.onFinish();
            } else if (!pause) {
                callback.onTick(elapsedTime, remainTime);
            }
        }
    }
}
