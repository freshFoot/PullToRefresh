package com.okay.pulltorefresh

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import java.util.*

/**
 * Create by zyl
 *
 * @date 2019/6/26
 */
class MyAdapter(list: List<String>) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    private val TAG = "MyAdapter"

    private val mList = ArrayList<String>()

    init {
        mList.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    fun update(list: List<String>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyHolder {
        Log.d(TAG, "onCreateViewHolder = $viewType")
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycle_item, viewGroup, false)
        return MyHolder(v)
    }

    override fun onBindViewHolder(myHolder: MyHolder, i: Int) {
        myHolder.tv.text = mList[i]

        myHolder.itemView.setOnClickListener {
            Toast.makeText(myHolder.itemView.context,"item click =$i",Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "getItemViewType =$position")
        return position
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv: TextView

        init {
            tv = itemView.findViewById(R.id.tv)
        }
    }
}