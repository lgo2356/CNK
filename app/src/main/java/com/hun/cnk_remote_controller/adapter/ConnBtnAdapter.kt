package com.hun.cnk_remote_controller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hun.cnk_remote_controller.R
import com.hun.cnk_remote_controller.data.ButtonItem

class ConnBtnAdapter(private val items: ArrayList<ButtonItem>) :
    RecyclerView.Adapter<ConnBtnAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnBtnAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_conn_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvBtnName.text = items[position].name
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