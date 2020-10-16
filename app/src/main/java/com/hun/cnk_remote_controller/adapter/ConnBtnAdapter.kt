package com.hun.cnk_remote_controller.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hun.cnk_remote_controller.R
import com.hun.cnk_remote_controller.data.ButtonItem

class ConnBtnAdapter(private val items: ArrayList<ButtonItem>) :
    RecyclerView.Adapter<ConnBtnAdapter.ViewHolder>() {

    private var itemTouchListener: OnItemTouchListener? = null

    interface OnItemTouchListener {
        fun onItemTouchActionDown(view: View, motionEvent: MotionEvent, position: Int)
        fun onItemTouchActionUp(view: View, motionEvent: MotionEvent, position: Int)
        fun onItemTouchActionCancel(view: View, motionEvent: MotionEvent, position: Int)
    }

    fun setOnItemTouchListener(listener: OnItemTouchListener) {
        this.itemTouchListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_conn_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvBtnName.text = items[position].name

        holder.itemView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("Debug", "Action down")
                    itemTouchListener?.onItemTouchActionDown(view, motionEvent, position)
                }

                MotionEvent.ACTION_UP -> {
                    Log.d("Debug", "Action up")
                    itemTouchListener?.onItemTouchActionUp(view, motionEvent, position)
                    view.performClick()
                }

                MotionEvent.ACTION_CANCEL -> {
                    Log.d("Debug", "Action cancel")
                    itemTouchListener?.onItemTouchActionCancel(view, motionEvent, position)
                    view.performClick()
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(name: String) {
        val item = ButtonItem()
        item.name = name
        items.add(item)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBtnName: TextView = itemView.findViewById(R.id.text_button_name)
    }
}