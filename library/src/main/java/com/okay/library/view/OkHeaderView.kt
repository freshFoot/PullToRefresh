package com.okay.library.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.okay.library.DensityUtil
import com.okay.library.PullState
import com.okay.library.R
import com.okay.library.callback.PullCallBack

/**
 * Create by zyl
 * @date 2019-11-06
 * 自定义的下拉刷新头
 */
class OkHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr),PullCallBack {

    private val okView = OkView(context,attrs)
    private val textView = TextView(context)

    private val marginLeft = DensityUtil.dip2px(context, 9F)

    private val marginBottom = DensityUtil.dip2px(context, 20F)

    /**
     * textView的向下偏移量
     */
    private val offset = DensityUtil.dip2px(context, 3F)

    private var parentHeight = 0

    init {

        addAnimView(okView)
        textView.apply {
            setTextColor(Color.GRAY)
            textSize = 20F
            text = context.getString(R.string.lib_ptr_up_load)
        }

        addTextView(textView)
    }

    private fun addTextView(textView: TextView) {
        addView(textView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun addAnimView(view: View) {
        addView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        var parentWidth = marginLeft
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (parentHeight < child.measuredHeight) {
                parentHeight = child.measuredHeight
            }
            parentWidth += child.measuredWidth
        }
        setMeasuredDimension(parentWidth, parentHeight + marginBottom)

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        okView.layout(0, 0, okView.measuredWidth, parentHeight)
        textView.layout(
            okView.measuredWidth + marginLeft,
            parentHeight - textView.measuredHeight + offset,
            measuredWidth,
            parentHeight
        )
    }
    private fun startAnim() {
        okView.startOkAnim()
    }

    private fun stopAnim(showTick: Boolean) {
        okView.stopAnim(showTick)
    }


    override fun onPullStateChange(curState: PullState) {
        super.onPullStateChange(curState)
        when (curState) {
            PullState.RELEASE_REFRESH -> {
                textView.text = resources.getString(R.string.lib_ptr_release_load)
            }
            PullState.REFRESHING -> {
                textView.text = resources.getString(R.string.lib_ptr_loading)
                startAnim()
            }
            PullState.NORMAL -> {
                textView.text = resources.getString(R.string.lib_ptr_up_load)
                okView.restoreState()

            }
            PullState.REFRESH_DONE -> {
                textView.text = resources.getString(R.string.lib_ptr_load_done)
                stopAnim(true)
            }
        }
    }


}