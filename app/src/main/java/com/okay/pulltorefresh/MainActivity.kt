package com.okay.pulltorefresh

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.okay.library.PullState
import com.okay.library.callback.RefreshListener
import com.okay.loadlibrary.LoadMoreWrapper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var list = Data.getList().subList(0, 20)

    val myAdapter = MyAdapter(list)

    private val TAG = "MainActivity"

    var headView: HeadView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = myAdapter
        val loadMoreAdapter = LoadMoreWrapper.with(myAdapter)
            .setFooterView(R.layout.demo_loading)
            .setLoadFailedView(R.layout.demo_load_failed)
            .setShowNoMoreEnabled(true).setListener {
            val itemCount = myAdapter.itemCount
            //not enable load more
            if (itemCount >= 40) {
                it.loadMoreEnabled = false
            }
            if (Random().nextBoolean()) {
                recycleView.postDelayed(Runnable {
                    list.addAll(Data.getList().subList(20, 40))
                    myAdapter.update(list)
                }, 1200)
            } else {
                it.setLoadFailed(true)
                Log.d(TAG,"setLoadFailed")
            }

        }.into(recycleView)
//        headView = HeadView(this)
//        pullLayout.addHeadView(headView!!)
        pullLayout.setOnRefreshListener(object : RefreshListener {
            override fun onRefresh() {
                Log.d(TAG, "onRefresh")
                loadMoreAdapter.setLoadFailed(false)
                loadMoreAdapter.loadMoreEnabled = true
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
