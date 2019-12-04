package com.okay.pulltorefresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.okay.library.PullState
import com.okay.library.callback.PullCallBack

/**
 * Create by zyl
 * @date 2019-11-27
 */
class HeadView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), PullCallBack {

    private val view = LayoutInflater.from(context).inflate(R.layout.ptr_demo_head, null)

    private var textView: TextView? = null

    init {
        textView = view.findViewById<TextView>(R.id.tv)
        addView(view)
    }

    fun loading() {
        textView?.text = resources.getString(R.string.lib_ptr_loading)
    }

    fun upLoad() {
        textView?.text = resources.getString(R.string.lib_ptr_up_load)
    }

    fun releaseRefresh() {
        textView?.text = resources.getString(R.string.lib_ptr_release_load)
    }

    fun loadDone() {
        textView?.text = resources.getString(R.string.lib_ptr_load_done)
    }


    override fun onPullStateChange(curState: PullState) {
        super.onPullStateChange(curState)
        when (curState) {
            PullState.RELEASE_REFRESH -> {
                releaseRefresh()
            }
            PullState.REFRESHING -> {
                loading()
            }
            PullState.NORMAL -> {
                upLoad()
            }
            PullState.REFRESH_DONE -> {
                loadDone()
            }
        }
    }


}