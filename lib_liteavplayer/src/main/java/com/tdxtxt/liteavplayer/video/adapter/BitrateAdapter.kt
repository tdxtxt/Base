package com.tdxtxt.liteavplayer.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.video.bean.BitrateItem

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/7/14
 *     desc   :
 * </pre>
 */
class BitrateAdapter : RecyclerView.Adapter<BitrateHolder>() {
    private var mData: List<BitrateItem> = mutableListOf()
    private var mListener: ((position: Int, item: BitrateItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BitrateHolder {
        val holder = BitrateHolder(LayoutInflater.from(parent.context).inflate(R.layout.liteavlib_item_bitrate, parent, false))
        holder.itemView.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            if(position in 0 until itemCount){
                mListener?.invoke(position, mData[position])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: BitrateHolder, position: Int) {
        val item = mData[position]
        holder.tvBitrate.text = item.formatBitrate()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setItemClickListenter(listener: (position: Int, item: BitrateItem) -> Unit){
        this.mListener = listener
    }

    fun setData(data: List<BitrateItem>?){
        this.mData = data?: mutableListOf()
        notifyDataSetChanged()
    }
}

class BitrateHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    val tvBitrate: TextView = view.findViewById(R.id.tv_bitrate)
}