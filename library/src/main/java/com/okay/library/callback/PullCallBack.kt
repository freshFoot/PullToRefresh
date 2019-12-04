package com.okay.library.callback

import com.okay.library.PullState

/**
 * Create by zyl
 * @date 2019-11-27
 * 拖拽状态的回调
 */
interface PullCallBack {

    /**
     * 拖拽的方向和距离，不断变化的值，单位像素
     *  in pixels.
     *  下拉时scrollY为负值
     */
    fun pull(scrollX: Int, scrollY: Int){}

    /**
     * 拖拽的状态
     */
    fun onPullStateChange(curState: PullState){}
}