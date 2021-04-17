package android.helper.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.helper.R
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.android.helper.utils.ConvertUtil

/**
 * 自定义进度条
 */
class ProgressView2(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // 背景的画笔
    private val mPaintBackground: Paint = Paint()

    // 进度条的画笔
    private val mPaintProgress: Paint = Paint()

    // 文字的画笔
    private val mPaintText: Paint = Paint()

    // 背景的高度
    private val mPaintBackgroundHeight = ConvertUtil.toDp(7f)

    // 背景的边距
    private val mPaintBackgroundPadding = ConvertUtil.toDp(44f)
    private var mTextContext: String = "20" // 文字的内容
    private val mBitmapWidth = resources.getDimension(R.dimen.dp_17)
    private val mBitmapHeight = resources.getDimension(R.dimen.dp_22)
    private var progress: Int = 20  // 当前的进度
    private var mEndX: Float = 0f   // 当前进度条的值
    private var oldProgress: Int = 0 // 老的进度条的值
    private var isCharging = false // 是否充电中
    private var mTextHeight: Int = 0 // textView文字的高度

    init {
        // 设置背景
        mPaintBackground.color = ContextCompat.getColor(context, R.color.black_14)
        mPaintBackground.strokeWidth = mPaintBackgroundHeight
        mPaintBackground.isAntiAlias = true
        mPaintBackground.strokeCap = Paint.Cap.ROUND

        // 设置进度条
        mPaintProgress.strokeWidth = mPaintBackgroundHeight
        mPaintProgress.strokeCap = Paint.Cap.ROUND
        mPaintProgress.isAntiAlias = true

        // 设置文字
        mPaintText.color = ContextCompat.getColor(getContext(), R.color.white)
        mPaintText.textSize = resources.getDimension(R.dimen.sp_20)
        mPaintText.isAntiAlias = true

        if (!isInEditMode) {
            //得到AssetManager
            val assets = context.assets
            val fromAsset = Typeface.createFromAsset(assets, "DINCondensedBold.ttf")
            mPaintText.typeface = fromAsset
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = resolveSize(View.MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec)

        // 高度 =  文字高度 + (drawable 高度 和 背景的高度 中最高的一个)
        val heightValue = mTextHeight + (Math.max(mBitmapHeight, mPaintBackgroundHeight))

        setMeasuredDimension(width, heightValue.toInt())
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {

            /***************************** 绘制进度文字 ****************************************/
            // 求出文字的高度
            val rect = Rect()
            mTextContext = "$progress%"
            mPaintText.getTextBounds(mTextContext, 0, mTextContext.length, rect)
            val mTextWidth = rect.width()
            mTextHeight = rect.height()

            /***************************** 绘制背景框 ****************************************/
            // 开始X坐标等于 左侧预留的padding的边距
            val startX: Float = mPaintBackgroundPadding
            // 开始Y坐标等于 = 文字的顶部 + bitmap的高度的一半
            val lineY: Float = mTextHeight + (mBitmapHeight / 2)
            // 结束的X轴坐标
            val endX = measuredWidth - mPaintBackgroundPadding;

            // 绘制背景
            it.drawLine(startX, lineY, endX, lineY, mPaintBackground)

            /***************************** 设置进度条 ****************************************/
            // 计算出百分比的值
            if (progress >= 100) {
                progress = 100
            }

            oldProgress = progress
            val percentage: Float = (progress.toFloat() / 100)
            // 当前百分比所占据的宽度值
            val value = (percentage * (measuredWidth - mPaintBackgroundPadding))
            // 结束X坐标等于 view的总宽度减去右侧预留的padding
            if (value <= startX) {
                mEndX = startX
            } else {
                mEndX = value
            }

            // 设置渐变
            val shaderStartX: Float = 0f + mPaintBackgroundPadding
            val shaderEndX: Float = measuredWidth - mPaintBackgroundPadding
            val shader = LinearGradient(
                    shaderStartX,
                    mTextHeight.toFloat(),
                    shaderEndX,
                    mTextHeight.toFloat(),
                    intArrayOf(ContextCompat.getColor(context, R.color.blue_3), ContextCompat.getColor(context, R.color.blue_4)),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
            )
            mPaintProgress.shader = shader

            if (isCharging()) {
                // 绘制阴影
                mPaintProgress.maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.SOLID)
            } else {
                mPaintProgress.maskFilter = null
            }

            // 绘制进度条
            canvas.drawLine(startX, lineY, mEndX, lineY, mPaintProgress)

            // 这里取文字宽度的4分之一，是为了让圆角看着舒服点
            canvas.drawText(mTextContext, mEndX - (mTextWidth / 4), mTextHeight.toFloat(), mPaintText)

            /***************************** 绘制bitmap ****************************************/
            if (isCharging()) {
                val dLeft = (mEndX - (mBitmapWidth / 3)).toInt()
                val dTop = (mTextHeight).toInt()
                val dRight = (mEndX + (mBitmapWidth - (mBitmapWidth / 3))).toInt()
                val dBottom = (mTextHeight + (mBitmapHeight)).toInt()

                // 充电状态下绘制闪电标志
                // view裁剪的大小
                val src = Rect(0, 0, mBitmapWidth.toInt(), mBitmapHeight.toInt())
                // 目标view显示的区域
                val dst = Rect(dLeft, dTop, dRight, dBottom)
                canvas.drawBitmap(bitmap!!, src, dst, null)
            }
        }
    }

    // 获取bitmap
    val bitmap: Bitmap? by lazy {
        // 获取进度的图片
        val icon = ContextCompat.getDrawable(context, R.mipmap.icon_c62_progress_image)
        if (icon is BitmapDrawable) {
            return@lazy icon.bitmap
        } else {
            return@lazy null
        }
    }

    public fun startAnimation(@IntRange(from = 0, to = 100) progress: Int) {

        setProgress(progress)

        val valueAnimation: ObjectAnimator = ObjectAnimator.ofInt(this, "progress", oldProgress, progress)
        valueAnimation.duration = 1000
        valueAnimation.interpolator = LinearInterpolator()
        valueAnimation.start()
        oldProgress = progress
    }

    fun getProgress(): Int {
        return progress
    }

    @Keep
    @SuppressLint("ObjectAnimatorBinding")
    fun setProgress(@IntRange(from = 0, to = 100) progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun isCharging(): Boolean {
        return isCharging
    }

    fun setCharging(charging: Boolean) {
        isCharging = charging
        invalidate()
    }

}