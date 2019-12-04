package com.okay.pulltorefresh

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.okay.library.PullState
import com.okay.library.callback.RefreshListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var list = Data.getList().subList(0, 20)

    var headView:HeadView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = MyAdapter(list)
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
            pullLayout.setState(PullState.REFRESH_DONE)
        }, 2000)
    }
}
