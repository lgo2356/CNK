package com.hun.cnk_remote_controller.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hun.cnk_remote_controller.R
import com.hun.cnk_remote_controller.data.ButtonItem

class ModifyBtnAdapter(private val items: ArrayList<ButtonItem>) :
    RecyclerView.Adapter<ModifyBtnAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_modification_button, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNumber.text = items[position].number.toString() + "번"
        holder.editName.hint = items[position].name
        holder.editName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                inputBtnCount = edit_button_count.text.toString().toInt()
                items[position].name = holder.editName.text.toString()
            }
        })

        if (items[position].mode) {
            holder.btnMode.text = "유지"
        } else {
            holder.btnMode.text = "복귀"
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(number: Int, name: String, mode: Boolean) {
        val item = ButtonItem()
        item.number = number
        item.name = name
        item.mode = mode
        items.add(item)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.text_button_number)
        val editName: EditText = itemView.findViewById(R.id.edit_button_name)
        val btnMode: Button = itemView.findViewById(R.id.btn_mode)
    }
}