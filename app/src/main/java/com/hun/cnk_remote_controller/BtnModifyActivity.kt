package com.hun.cnk_remote_controller

import android.content.Intent
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

        addBtnModifyItems()

        // finish -> Main 가게 -> Main에서 뒤로가기 누르면 바로 종료 물어보게
        btn_done.setOnClickListener {
            setResult(Constant.RES_OK)
            finish()
//            startActivity(intent)
        }
    }

    private fun addBtnModifyItems() {
        val btnCount = intent.getIntExtra("btn_count", -1)

        if (btnCount <= -1) {
            return
        }

        for (number in 1 until btnCount + 1) {
            btnRecyclerAdapter.addItem(number.toString() + "번")
        }
    }
}