package com.android.helper.utils

import com.android.common.utils.LogUtil
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    private val mScope = CoroutineScope(Dispatchers.Main + CoroutineName(TAG))
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

    /**
     * The countdown runs on the main thread
     */
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
                if (mCurrent <= 0) {
                    mCallBackListener?.onFinish()
                }

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

    fun restart() {
        isPause = false
        mCount = 0
        mJob?.let {
            if (it.isActive) {
                it.cancel()
                LogUtil.e(TAG, "restart ... cancel job ...")
            } else {
                LogUtil.e(TAG, "restart the current status is not running, can't cancel ...")
            }
        }
    }

    fun destroy() {
        mJob?.let {
            if (it.isActive) {
                LogUtil.e(TAG, "destroy ---->")
            }
        }
    }

    interface CallBack {

        /**
         * @param current The current timer, starting at 0
         * @param countdown current countdown
         */
        fun onTick(current: Long, countdown: Long)
        fun onFinish()
    }
}