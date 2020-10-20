package com.hun.cnk_remote_controller.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hun.cnk_remote_controller.R
import com.hun.cnk_remote_controller.data.ButtonItem

class ConnBtnAdapter(private val items: ArrayList<ButtonItem>) :
    RecyclerView.Adapter<ConnBtnAdapter.ViewHolder>() {

    private var itemTouchListener: OnItemTouchListener? = null
    private val toggleManageList: Array<Boolean> =
        arrayOf(false, false, false, false, false, false, false, false, false, false)

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
        holder.tvName.text = items[position].name
        holder.itemView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemTouchListener?.onItemTouchActionUp(view, motionEvent, position)

                    if (!items[position].mode) {  // Default mode
                        holder.tvName.setBackgroundColor(
                            ContextCompat.getColor(
                                holder.tvName.context,
                                R.color.colorMotorPressDown
                            )
                        )
                    }

                    if (items[position].mode) {  // Toggle mode
                        holder.tvName.setBackgroundColor(
                            ContextCompat.getColor(
                                holder.tvName.context,
                                R.color.colorMotorPressDown
                            )
                        )

                        if (toggleManageList[position]) {
                            holder.tvName.setBackgroundColor(
                                ContextCompat.getColor(
                                    holder.tvName.context,
                                    R.color.colorMotorItemBackgroundNormal
                                )
                            )
                        }

                        toggleManageList[position] = !toggleManageList[position]
                    }
                }

                MotionEvent.ACTION_UP -> {
                    itemTouchListener?.onItemTouchActionUp(view, motionEvent, position)
                    if (!items[position].mode) {  // If not toggle mode (toggle: true, default: false)
                        holder.tvName.setBackgroundColor(
                            ContextCompat.getColor(
                                holder.tvName.context, R.color.colorMotorItemBackgroundNormal
                            )
                        )
                    }
                    view.performClick()
                }

                MotionEvent.ACTION_CANCEL -> {
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

    fun addItem(name: String, mode: Boolean) {
        val item = ButtonItem()
        item.name = name
        item.mode = mode
        items.add(item)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
    }
}
