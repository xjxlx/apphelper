package com.android.helper.utils;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

public class ValueAnimationUtil {
    
    private ValueAnimationListener mListener;
    private ValueAnimator animator;
    
    /**
     * @param duration 持续的时长 ，但是是秒
     * @param values   int类型数据的变化范围
     */
    public ValueAnimationUtil setAnimationInt(final int duration, int... values) {
        animator = ValueAnimator.ofInt(values);
        animator.setDuration(duration * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mListener != null) {
                //  LogUtil.e("animatedValue :  " + animatedValue + "  duration:" + duration);
                if (duration > animatedValue) {
                    mListener.onValueChange(animatedValue);
                } else {
                    mListener.onFinish();
                }
            }
        });
        return this;
    }
    
    /**
     * @param duration 持续的时长
     * @param values   float类型数据的变化范围
     */
    public ValueAnimationUtil setAnimationFloat(final int duration, float... values) {
        animator = ValueAnimator.ofFloat(values);
        animator.setDuration(duration * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mListener != null) {
                if (duration > animatedValue) {
                    mListener.onValueChange(animatedValue);
                } else {
                    mListener.onFinish();
                }
            }
        });
        return this;
    }
    
    /**
     * @param duration 时长
     * @param values   int类型的数据变化范围
     * @return 倒叙的倒计时
     */
    public ValueAnimationUtil setAnimationIntFlashBack(final int duration, int... values) {
        animator = ValueAnimator.ofInt(values);
        animator.setDuration(duration * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            if (mListener != null) {
                if (animatedValue <= 0) {
                    mListener.onFinish();
                } else {
                    mListener.onValueChange(animatedValue);
                }
            }
        });
        return this;
    }
    
    /**
     * 开始执行
     */
    public void start() {
        if (animator != null) {
            animator.start();
            if (mListener != null) {
                mListener.onStart();
            }
        }
    }
    
    /**
     * 取消
     */
    public void cancel() {
        if (animator != null) {
            animator.cancel();
            if (mListener != null) {
                mListener.onCancel();
            }
        }
    }
    
    /**
     * 监听动画
     *
     * @param valueListener 监听器
     */
    public ValueAnimationUtil setValueListener(ValueAnimationListener valueListener) {
        if (valueListener != null) {
            mListener = valueListener;
        }
        return this;
    }
    
    public interface ValueAnimationListener {
        void onValueChange(Object value);
        
        void onStart();
        
        void onCancel();
        
        void onFinish();
    }
}
