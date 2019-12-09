package com.okay.pulltorefresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.demo_loading.view.*

/**
 * Create by zyl
 * @date 2019-12-09
 */
class FooterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val TAG = "FooterView"

    init {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG,"onAttachedToWindow")
        loading.startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG,"onDetachedFromWindow")
        loading.stopAnimation()
    }
}