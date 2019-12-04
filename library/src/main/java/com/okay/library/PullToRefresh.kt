package com.okay.library

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.OverScroller
import com.okay.library.callback.PullCallBack
import com.okay.library.callback.RefreshListener
import com.okay.library.view.OkHeaderView
import kotlin.math.abs

/**
 * Create by zyl
 * @date 2019-11-22
 * 此控件目前只支持有一个子孩子
 */
class PullToRefresh @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val TAG = PullToRefresh::class.java.simpleName

    /**
     * 记录手指按下位置
     */
    private var originY = 0F
    private var originX = 0F

    /**
     * 可配置信息 (之后通过style属性对外暴露)
     */
    private val config = RefreshConfig()

    private var nestScrollView: View? = null

    /**
     * 对外回调
     */
    private var callBacks = arrayListOf<PullCallBack>()
    private var listener: RefreshListener? = null

    /**
     * 最小滑动距离
     */
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * 做滚动回弹动画使用
     */
    private val overScroller = OverScroller(context)

    /**
     * 刷新状态,改变状态使用 setState方法 内部改变
     */
    private var state = PullState.NORMAL

    init {
        callBacks.clear()
        //默认垂直方向
        orientation = VERTICAL
    }

    /**
     * 支持外部自定义 添加头布局
     */
    fun addHeadView(view: View) {
        config.needHead = true
        removeAllViews()
        //给view 设置监听
        if (view is PullCallBack) {
            addOnPullListener(view)
        }
        addView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(nestScrollView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 1) {
            throw Exception("PullToRefresh must have only one child")
        }
        //得到可以支持嵌套滚动的view
        nestScrollView = getChildAt(0)
        if (config.needHead) {
            addHeadView(OkHeaderView(context))
        }
    }

    /**
     * getChildAt(0) 为head 因为内部就是这么排序的
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        when (orientation) {
            VERTICAL -> {
                if (config.needHead) {
                    val head = getChildAt(0)
                    head.layout(
                        measuredWidth / 2 - head.measuredWidth / 2,
                        -head.measuredHeight,
                        measuredWidth / 2 + head.measuredWidth / 2,
                        0
                    )
                }
                nestScrollView?.layout(0, 0, measuredWidth, measuredHeight)
            }
            else -> {
                //暂缓实现

            }
        }
    }


    /**
     * 拦截自己需要处理的事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                originY = ev.y
                originX = ev.x
                //重置状态，防止滑不动了
                if (scrollY==0){
                    setState(PullState.NORMAL)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val curY = ev.y
                if (state >= PullState.REFRESHING) return true
                if (nestScrollView!!.canScrollVertically((originY - curY).toInt())) {
                    return super.onInterceptTouchEvent(ev)
                }
                return if (abs(curY - originY) > touchSlop) {
                    true
                } else {
                    super.onInterceptTouchEvent(ev)
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }


    /**
     * 处理拦截下来的事件 滑动和手指抬起
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent : event = ${event.action}")
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (state >= PullState.REFRESHING) return true
                var dy = (originY - event.y).toInt()
                Log.d(
                    TAG,
                    "onTouchEvent : ACTION_MOVE; dy = $dy ,scrolly = $scrollY ，${nestScrollView!!.canScrollVertically(
                        dy
                    )}"
                )
                //滑动边界控制
                if (dy < 0) {
                    //往下滑
                    if (scrollY + dy >= 0 || !nestScrollView!!.canScrollVertically(dy)) {
                        scrollBy(0, dy)
                    }
                } else {
                    //往上滑
                    if (scrollY + dy <= 0 || !nestScrollView!!.canScrollVertically(dy)) {
                        scrollBy(0, dy)
                    }
                }
                //回调外部值
                callBacks.forEach {
                    it.pull(scrollX, scrollY)
                }
                val headView = getChildAt(0)
                //还原至刷新头的高度
                var stayHeight = config.headStayHeight
                if (stayHeight == 0) {
                    stayHeight = headView.measuredHeight
                    config.headStayHeight = headView.measuredHeight
                }
                if (-scrollY > stayHeight) {
                    setState(PullState.RELEASE_REFRESH)
                } else {
                    setState(PullState.NORMAL)
                }
                originY = event.y
            }

            MotionEvent.ACTION_UP -> {
                if (state == PullState.RELEASE_REFRESH) {
                    setState(PullState.REFRESHING)
                }
                //重置数值
                originY = 0F
                Log.d(TAG, "onTouchEvent : ACTION_UP ,scrollY= $scrollY")
                if (state == PullState.REFRESHING) {
                    overScroller.startScroll(
                        scrollX,
                        scrollY,
                        -scrollX,
                        -scrollY - config.headStayHeight
                    )
                    postInvalidate()
                } else {
                    //重置滑动位置
                    resetScrollPosition()
                }
            }
        }
        return true
    }


    private fun resetScrollPosition() {
        if (scrollY != 0) {
            //scrollBy(0,-scrollY)
            //使用overScroller 代替scrollBy 动画效果 体验好
            overScroller.startScroll(scrollX, scrollY, -scrollX, -scrollY)
            postInvalidate()
        }
    }

    override fun computeScroll() {
        //如果scroller还没停止，那么就还进行不停的绘制
        if (overScroller.computeScrollOffset()) {
            //注意这里的getCurrY()的源码获取的是进行微移后的当前的坐标，不是相对距离
            scrollTo(overScroller.currX, overScroller.currY)
            //重绘
            postInvalidate()
        }
    }

    /**
     * 设置拖动监听
     */
    private fun addOnPullListener(callBack: PullCallBack) {
        callBacks.add(callBack)
    }

    /**
     * 设置刷新监听
     */
    fun setOnRefreshListener(listener: RefreshListener) {
        this.listener = listener
    }


    /**
     * 设置刷新状态
     */
    fun setState(pullState: PullState) {
        if (this.state != pullState) {
            this.state = pullState
            notifyStateChanged()
        }
    }

    /**
     * 根据state做相应的变化 如果需要的话
     */
    private fun notifyStateChanged() {
        callBacks.forEach {
            it.onPullStateChange(state)
        }
        when (state) {
            PullState.REFRESH_DONE -> {
                postDelayed({
                    resetScrollPosition()
                }, config.headStayTime)
            }
            PullState.REFRESHING -> {
                listener?.onRefresh()
            }
            else -> {}
        }
    }

    /**
     * @param headStayHeight 刷新头停留的高度
     */
    private fun onRefreshDone(headStayHeight: Int) {
        setState(PullState.REFRESH_DONE)
        config.headStayHeight = headStayHeight
    }

    /**
     * @param headStayTime 刷新头停留的时间
     */
    fun onRefreshDone(headStayTime: Long) {
        setState(PullState.REFRESH_DONE)
        config.headStayTime = headStayTime
    }

    /**
     * 清除防止内存泄漏
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        callBacks.clear()
    }
}













