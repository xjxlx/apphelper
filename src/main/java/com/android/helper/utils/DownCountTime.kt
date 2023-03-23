package com.android.helper.utils

import kotlinx.coroutines.*

/**
 * @author : 流星
 * @CreateDate: 2023/3/23-11:34
 * @Description:
 */
class DownCountTime {

    private val TAG = this.javaClass.simpleName
    private var mTotal: Long = 0L
    private var mInterval: Long = 0L
    private var mCurrent: Long = 0
    private var mCount: Long = 0
    private val mScope = CoroutineScope(CoroutineName(TAG))
    private var mJob: Job? = null
    private var isPause = false
    private var mCallBackListener: CallBack? = null

    private val realTotal: Long
        get() {
            return mTotal * mInterval
        }

    fun setCountdown(total: Long = 0, interval: Long = 0, listener: CallBack) {
        this.mTotal = total
        this.mInterval = interval
        this.mCallBackListener = listener
    }

    fun start() {
        mJob?.let {
            if (it.isActive) {
                LogUtil.e(TAG, "down count time is running ...")
                return
            }
        }

        if (mTotal <= 0) {
            LogUtil.e(TAG, "the total  cannot be 0 ...")
            return
        }

        if (mInterval <= 0) {
            LogUtil.e(TAG, "the interval  cannot be 0 ...")
            return
        }

        if (isPause) {
            LogUtil.e(TAG, "the current status is pause ...")
            return
        }

        mJob = mScope.launch {
            while (!isPause && (mCount <= mTotal)) {
                mCurrent = realTotal - (mInterval * mCount)
                // LogUtil.e(TAG, "current ----> $mCurrent")
                mCallBackListener?.onTick(mCount, mCurrent)

                if (mCount == mTotal) {
                    mCount = 0
                    mCurrent = 0
                    isPause = false
                    cancel()
                    return@launch
                }

                delay(mInterval)
                mCount++
            }
        }
    }

    fun pause() {
        mJob?.let {
            if (it.isCompleted) {
                LogUtil.e(TAG, "the current status is completed, can't pause ...")
                return
            }
        }
        isPause = true
    }

    fun resume() {
        isPause = false
        if (mCount != 0L && mCurrent != 0L) {
            start()
        } else {
            LogUtil.e(TAG, "the current status is stop,can't be resume ...")
        }
    }

    interface CallBack {
        fun onTick(count: Long, current: Long)
        fun onFinish()
    }

}