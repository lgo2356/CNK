package com.hun.cnk_remote_controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hun.cnk_remote_controller.adapter.BtnRecyclerAdapter
import com.hun.cnk_remote_controller.data.ButtonItem
import kotlinx.android.synthetic.main.activity_modification_button.*

class BtnModifyActivity : AppCompatActivity() {
    private val buttonItems: ArrayList<ButtonItem> = ArrayList()
    private val btnRecyclerAdapter: BtnRecyclerAdapter = BtnRecyclerAdapter(buttonItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_button)

        recycler_modification_list.adapter = btnRecyclerAdapter

        for (number in 1..10) {
            btnRecyclerAdapter.addItem("번")
        }
    }
}