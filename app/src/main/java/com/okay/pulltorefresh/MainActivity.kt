package com.okay.pulltorefresh

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.okay.library.PullState
import com.okay.library.callback.RefreshListener
import com.okay.loadlibrary.LoadMoreWrapper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var list = Data.getList().subList(0, 20)

    val myAdapter = MyAdapter(list)

    var headView:HeadView? = null

    // use for demo, please ignore
    private var mShowLoadFailedEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = myAdapter
        LoadMoreWrapper.with(myAdapter).setShowNoMoreEnabled(true).setListener {
            val itemCount = myAdapter.itemCount
            if (itemCount >= 20 && mShowLoadFailedEnabled) {
                mShowLoadFailedEnabled = false
                recycleView.postDelayed(Runnable { it.setLoadFailed(true) }, 800)
            } else {
                //not enable load more
                if (itemCount >= 40) {
                    it.loadMoreEnabled = false
                }

                recycleView.postDelayed(Runnable {
                    list.addAll(Data.getList().subList(20, 40))
                    myAdapter.update(list)
                }, 1200)
            }
        }.into(recycleView)
//        headView = HeadView(this)
//        pullLayout.addHeadView(headView!!)
        pullLayout.setOnRefreshListener(object : RefreshListener {
            override fun onRefresh() {
                Log.d("MainActivity", "onRefresh")
                mockNet()
            }
        })

    }

    private fun mockNet() {
        pullLayout.postDelayed({
            var list = Data.getList().subList(0, 20)
            myAdapter.update(list)
            pullLayout.setState(PullState.REFRESH_DONE)
        }, 2000)
    }
}
