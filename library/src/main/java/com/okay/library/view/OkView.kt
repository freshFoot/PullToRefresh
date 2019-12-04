package com.okay.library.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.okay.library.DensityUtil

/**
 * Create by zyl
 * @date 2019-11-05
 * 带动画效果的和对号的ok 刷新头部
 * 使用此类目的：优化性能 不再使用帧动画
 */
class OkView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * o 的默认宽度
     */
    private val defaultOWidth = DensityUtil.dip2px(context, 24F)

    private val defaultWidth = DensityUtil.dip2px(context, 40F)

    private val mStrokeWidth = DensityUtil.dip2px(context, 2F).toFloat()

    private val okStrokeWidth = DensityUtil.dip2px(context, 4F).toFloat()

    private val marginLeft = DensityUtil.dip2px(context, 3F)

    private val defaultHeight = defaultOWidth

    private val mPaint = Paint()

    private var centerX = 0F

    private var radius = 0F

    private var centerOY = centerX

    private var centerKY = centerX

    /**
     * 动画时间
     */
    private val mDuration = 400L

    /**
     * k的path
     */
    private val kPath = Path()

    /**
     * 画对号所需
     */
    private val rectF = RectF()

    private val trickPath = Path()

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var paintColor = -1

    /**
     * 默认是否画对号
     */
    private var canDrawTick = false

    /**
     * 延时动画的runnable
     */
    private val animRunnable = Runnable { startKAnim() }

    init {

        mPaint.apply {
            config()
            strokeWidth = okStrokeWidth
        }
        tickPaint.apply {
            config()
            strokeWidth = mStrokeWidth
        }
    }

    /**
     * 配置paint 通用属性
     */
    private fun Paint.config() {
        isAntiAlias = true
        color = paintColor
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    fun startOkAnim() {
        canDrawTick = false
        startOAnim()
        postDelayed(animRunnable, mDuration / 2)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = getMySize((defaultWidth + mStrokeWidth).toInt(), widthMeasureSpec)
        val mHeight = getMySize((defaultHeight + mStrokeWidth).toInt(), heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight * 3 / 2)
        centerX = defaultOWidth / 2F
        radius = centerX - mStrokeWidth
        centerOY = measuredHeight - centerX
        centerKY = centerOY

        //算出对号的位置
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight - defaultOWidth / 2
        //算出圆半径大小
        val tickRadius = (centerX - mStrokeWidth) / 2
//        Log.d("TickView","measuredWidth = $measuredWidth ,measuredHeight = $measuredHeight")
        rectF.set(
            (centerX - tickRadius), (centerY - tickRadius),
            (centerX + tickRadius), (centerY + tickRadius)
        )

        //对号的三个点

        val middleX = centerX
        val middleY = centerY + tickRadius / 3

        val startX = middleX - tickRadius / 2
        val startY = middleY - tickRadius / 2

        val endX = middleX + tickRadius
        val endY = middleY - tickRadius * 4 / 3

        trickPath.reset()
        trickPath.moveTo(startX, startY)
        trickPath.lineTo(middleX, middleY)
        trickPath.lineTo(endX, endY)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (canDrawTick) {
            //画对号 顺时针 0-90
            canvas.drawArc(rectF, -20F, 320F, false, tickPaint)
            canvas.drawPath(trickPath, tickPaint)
        } else {
            kPath.reset()
            val middleX = centerX * 2 + marginLeft
            val middleY = centerKY

            val startX = middleX + radius
            val startY = centerKY - radius

            val endX = middleX + radius
            val endY = centerKY + radius

            kPath.moveTo(startX, startY)
            kPath.lineTo(middleX, middleY)
            kPath.lineTo(endX, endY)
            canvas.drawPath(kPath, mPaint)
            canvas.drawCircle(centerX, centerOY, radius, mPaint)
        }
    }


    /**
     * 模版方法，用于测量view的宽高
     */
    private fun getMySize(defaultSize: Int, measureSpec: Int): Int {

        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        return when (mode) {
            //当控件的宽高在被定为wrap_content时
            MeasureSpec.AT_MOST,
                //当控件的宽高在被定为match_parent时
            MeasureSpec.UNSPECIFIED -> {
                defaultSize
            }
            else -> {
                size
            }
        }
    }

    /**
     * 属性动画使用的方法
     */
    fun setCenterOY(centerOY: Float) {
        this.centerOY = centerOY
        invalidate()
    }

    /**
     * 属性动画使用的方法
     */
    fun setCenterKY(centerKY: Float) {
        this.centerKY = centerKY
        invalidate()
    }

    private var oAnimator: ObjectAnimator? = null

    private var kAnimator: ObjectAnimator? = null

    fun stopAnim(showTick: Boolean) {
        cancelAnim(oAnimator)
        cancelAnim(kAnimator)
        removeCallbacks(animRunnable)
        canDrawTick = showTick
        invalidate()
    }

    private fun startOAnim() {
        cancelAnim(oAnimator)
        oAnimator = ObjectAnimator.ofFloat(this, "centerOY", centerOY, centerX)
        oAnimator?.apply {
            config()
        }?.start()
    }

    /**
     * 停止动画
     */
    private fun cancelAnim(objectAnimator: ObjectAnimator?) {
        if (objectAnimator != null && objectAnimator.isRunning) objectAnimator.cancel()
    }


    private fun startKAnim() {
        cancelAnim(kAnimator)
        kAnimator = ObjectAnimator.ofFloat(this, "centerKY", centerKY, centerX)
        kAnimator?.apply {
            config()
        }?.start()
    }

    /**
     * 配置相同的动画效果
     */
    private fun ObjectAnimator.config() {
        duration = mDuration
        interpolator = AccelerateDecelerateInterpolator()
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //停止动画
        stopAnim(false)
    }

    /**
     * 状态恢复
     */
    fun restoreState() {
        canDrawTick = false
        //恢复原状
        centerOY = measuredHeight - centerX
        centerKY = centerOY
        postInvalidate()
    }

}









