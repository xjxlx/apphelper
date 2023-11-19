package com.android.helper.utils

/**
 * @author : 流星
 * @CreateDate: 2022/12/2-14:56
 * @Description:
 */
object GradientUtil {

    fun getDistance(currentTime: Float, startTime: Float, endTime: Float, startDistance: Float, endDistance: Float,
                    isReverse: Boolean): Float {
        val distance: Float

        val intervalTim = endTime - startTime
        val distanceInterval = endDistance - startDistance
        // v = s / t
        val speed = (distanceInterval / intervalTim)
        if (!isReverse) {
            // s = v * t
            distance = speed * (currentTime - startTime) + startDistance
        } else {
            // s = t * v
            val currentDistance = (currentTime - endTime) * speed
            distance = endDistance + currentDistance
        }
        return distance
    }

    /**
     * small  to  big gradient
     */
    fun getBigToSmallGradientValue(currentTime: Float, startTime: Float, endTime: Float, startDistance: Float, endDistance: Float): Float {
        val gradient: Float
        val intervalTime = endTime - startTime
        val intervalDistance = startDistance - endDistance
        val speed = intervalDistance / intervalTime
        // s = v * t
        gradient = startDistance - speed * (currentTime - startTime)
        // LogUtil.e("gradient - currentTime:$currentTime startTime:$startTime  endTime: $endTime  startDistance:$startDistance endDistance: $endDistance sss: $gradient")
        return gradient
    }
}
