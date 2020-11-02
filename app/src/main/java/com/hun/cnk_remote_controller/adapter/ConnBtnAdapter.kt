package com.hun.cnk_remote_controller.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hun.cnk_remote_controller.R
import com.hun.cnk_remote_controller.data.ButtonItem

class ConnBtnAdapter(private val items: ArrayList<ButtonItem>) :
    RecyclerView.Adapter<ConnBtnAdapter.ViewHolder>() {

    private val toggleManageList: Array<Boolean> =
        arrayOf(false, false, false, false, false, false, false, false, false, false)
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items[position].name
        holder.layoutInner.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemTouchListener?.onItemTouchActionDown(view, motionEvent, position)

                    if (!items[position].mode) {  // Default mode
                        holder.layoutInner.setBackgroundResource(R.drawable.item_conn_edge_pressed)
                        holder.tvName.setTextColor(
                            ContextCompat.getColor(
                                holder.tvName.context,
                                R.color.colorWhite
                            )
                        )
                    }

                    if (items[position].mode) {  // Toggle mode
                        holder.layoutInner.setBackgroundResource(R.drawable.item_conn_edge_pressed)
                        holder.tvName.setTextColor(
                            ContextCompat.getColor(
                                holder.tvName.context,
                                R.color.colorWhite
                            )
                        )
//                        holder.tvName.setBackgroundColor(
//                            ContextCompat.getColor(
//                                holder.tvName.context,
//                                R.color.colorMotorPressDown
//                            )
//                        )

                        if (toggleManageList[position]) {
                            holder.layoutInner.setBackgroundResource(R.drawable.item_conn_edge_normal)
                            holder.tvName.setTextColor(
                                ContextCompat.getColor(
                                    holder.tvName.context,
                                    R.color.colorConnEdge
                                )
                            )
                        }

                        toggleManageList[position] = !toggleManageList[position]
                    }
                }

                MotionEvent.ACTION_UP -> {
                    itemTouchListener?.onItemTouchActionUp(view, motionEvent, position)
                    if (!items[position].mode) {  // If not toggle mode (toggle: true, default: false)
                        holder.layoutInner.setBackgroundResource(R.drawable.item_conn_edge_normal)
                        holder.tvName.setTextColor(
                            ContextCompat.getColor(
                                holder.tvName.context,
                                R.color.colorConnEdge
                            )
                        )
                    }
                    view.performClick()
                }

                MotionEvent.ACTION_CANCEL -> {
                    itemTouchListener?.onItemTouchActionCancel(view, motionEvent, position)
                    holder.layoutInner.setBackgroundResource(R.drawable.item_conn_edge_normal)
                    holder.tvName.setTextColor(
                        ContextCompat.getColor(
                            holder.tvName.context,
                            R.color.colorConnEdge
                        )
                    )
                    if (!items[position].mode) {  // If not toggle mode (toggle: true, default: false)
                        holder.layoutInner.setBackgroundResource(R.drawable.item_conn_edge_normal)
                        holder.tvName.setTextColor(
                            ContextCompat.getColor(
                                holder.tvName.context,
                                R.color.colorConnEdge
                            )
                        )
//                        holder.tvName.setBackgroundColor(
//                            ContextCompat.getColor(
//                                holder.tvName.context, R.color.colorMotorItemBackgroundNormal
//                            )
//                        )
                    }
                    view.performClick()
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun setBackground(view: View, state: Boolean) {
        if (state) {
            view.setBackgroundResource(R.drawable.item_conn_edge_pressed)
        }
    }

    fun addItem(name: String, mode: Boolean) {
        val item = ButtonItem()
        item.name = name
        item.mode = mode
        items.add(item)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutInner: LinearLayout = itemView.findViewById(R.id.layout_inner)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
    }
}
