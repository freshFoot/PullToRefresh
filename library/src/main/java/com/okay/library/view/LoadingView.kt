package com.okay.library.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import com.okay.library.DensityUtil
import com.okay.library.R
import kotlin.math.min


/**
 * Create by zyl
 * @date 2019/11/8
 * 加载中转菊花的view
 */
class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TAG = "LoadingView"

    /**
     * 默认宽高
     */
    private val defaultWidth = DensityUtil.dip2px(context,40F)
    private val defaultHeight = DensityUtil.dip2px(context,40F)

    private var mStrokeWidth =  DensityUtil.dip2px(context,5F).toFloat()

    private var LOADING_DURATION = 1000L

    /**
     * 线条开始颜色 默认白色
     *
     */
    private var mStartColor = Color.GRAY

    /**
     * 线条结束颜色 默认灰色
     */
    private var mEndColor = Color.WHITE


    /**
     * 渐变颜色
     */
    private var mColors = arrayListOf<Int>()

    /**
     * 花瓣个数
     */
    private var petalCount = 10

    /**
     * 花瓣长度
     */
    private var petalLength =  DensityUtil.dip2px(context,3F)

    private var mPaint = Paint()

    private var valueAnimator: ValueAnimator? = null

    private var mStartIndex = 0

    init {

        context.obtainStyledAttributes(attrs, R.styleable.LoadingView,0,R.style.DefaultLoadingView).apply {
            petalCount =  getInt(R.styleable.LoadingView_petalCount,-1)
            mStartColor = getColor(R.styleable.LoadingView_startColor,-1)
            mEndColor = getColor(R.styleable.LoadingView_endColor,-1)
            petalLength = getDimension(R.styleable.LoadingView_petalLength,0F).toInt()
            mStrokeWidth = getDimension(R.styleable.LoadingView_petalWidth,0F)
        }.recycle()

        mPaint.apply {
            isAntiAlias = true
            strokeWidth = mStrokeWidth
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        val evaluator = ArgbEvaluator()
        //颜色渐变计算器
        for (i in 0 until petalCount) {
            // alpha 0~1之间
            val alpha = (petalCount - i).toFloat() / petalCount
            val color = evaluator.evaluate(alpha, mStartColor, mEndColor) as Int
            Log.d("ArgbEvaluator", "color = $color,alpha=$alpha")
            mColors.add(color)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = getMySize(defaultWidth, widthMeasureSpec)
        val mHeight = getMySize(defaultHeight, heightMeasureSpec)
        val min = min(mWidth, mHeight)
        setMeasuredDimension(min, min)
    }


    override fun onDraw(canvas: Canvas) {
        val radius = (measuredWidth - mStrokeWidth) / 2
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight / 2F
        //摆正位置，为旋转动画作准备
        canvas.rotate(360F / petalCount - 90, centerX, centerY)
        for (i in 0 until petalCount) {
            // 获取颜色下标
            val index = (mStartIndex + i) % petalCount
            mPaint.color = mColors[petalCount - index - 1]
            canvas.drawLine(
                centerX + radius - petalLength,
                centerY,
                centerX + radius,
                centerY,
                mPaint
            )
            canvas.rotate(360F / petalCount, centerX, centerY)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    /**
     * 开始动画
     *
     * @param duration 动画时间
     */
    fun startAnimation(duration: Long?=null) {
        var time = duration
        if (time ==null) time = LOADING_DURATION
        //禁止频繁调用
        if (valueAnimator!=null && valueAnimator!!.isRunning) return
        valueAnimator = ValueAnimator.ofInt(petalCount, 0).apply {
            this.duration = time
            repeatMode =ValueAnimator.RESTART
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        valueAnimator?.addUpdateListener { animation ->
            // 此处会回调3次 需要去除后面的两次回调
            if (mStartIndex != animation.animatedValue as Int) {
                mStartIndex = animation.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator?.start()
    }

    /**
     * 停止动画
     */
    fun stopAnimation(){
        mStartIndex = 0
        valueAnimator?.cancel()
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
}


















