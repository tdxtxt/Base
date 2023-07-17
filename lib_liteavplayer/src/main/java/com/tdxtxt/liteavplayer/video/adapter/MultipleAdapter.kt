package com.tdxtxt.liteavplayer.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tdxtxt.liteavplayer.R
import com.tdxtxt.liteavplayer.utils.LiteavPlayerUtils


/**
 * <pre>
 *     author : ton
 *     time   : 2023/3/2
 *     desc   :
 * </pre>
 */
class MultipleAdapter : RecyclerView.Adapter<MultipleHolder>() {
    private var mSelectedPosition = -1
    private var mData: List<Float> = mutableListOf(1f)
    private var mListener: ((value: Float) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleHolder {
        val holder = MultipleHolder(LayoutInflater.from(parent.context).inflate(R.layout.liteavlib_item_multiple, parent, false))
        holder.itemView.setOnClickListener {
//            val lastSelectedPosition = mSelectedPosition
//            mSelectedPosition = position
//            if(lastSelectedPosition == mSelectedPosition) return@setOnClickListener
//            notifyItemChanged(mSelectedPosition)
//            notifyItemChanged(lastSelectedPosition)
            val position = holder.absoluteAdapterPosition
            if(position in 0 until itemCount){
                mListener?.invoke(mData[position])
            }
        }
        return holder
    }
    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: MultipleHolder, position: Int) {
        val item = LiteavPlayerUtils.formatMultiple(mData.get(position))
        holder.tvMultiple.setText(item)
//        if(mSelectedPosition == position){
//            holder.itemView.setBackgroundResource(R.drawable.liteavlib_item_multiple_selected)
//            holder.tvMultiple.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.liteavlib_theme))
//        }else{
//            holder.itemView.setBackgroundResource(R.drawable.liteavlib_item_multiple_normal)
//            holder.tvMultiple.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
//        }
    }

    fun setItemClickListenter(listener: (value: Float) -> Unit){
        this.mListener = listener
    }

    fun setMultiple(value: Float){
        val selectedPosition = mData.indexOf(value)
        if(selectedPosition == mSelectedPosition) return
        mSelectedPosition = selectedPosition
        notifyDataSetChanged()
    }

    fun setData(data: List<Float>?){
        this.mData = data?: mutableListOf(1f)
        notifyDataSetChanged()
    }
}

class MultipleHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    val tvMultiple: TextView = view.findViewById(R.id.tv_multiple)
}