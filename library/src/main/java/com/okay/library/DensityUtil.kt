package com.okay.library

import android.content.Context

/**
 * Create by zyl
 * @date 2019-11-02
 */
internal object DensityUtil {

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale =
            context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dip2px(context: Context,dpValue: Float): Int {
        val scale =
            context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}