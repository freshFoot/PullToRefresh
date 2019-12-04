package com.okay.library

/**
 * Create by zyl
 * @date 2019-11-27
 * 下拉刷新的状态
 */
enum class PullState {
    NORMAL,RELEASE_REFRESH,REFRESHING,REFRESH_DONE;

    /**
     * 返回当前的状态
     */
    fun getCurrentState() = this
}