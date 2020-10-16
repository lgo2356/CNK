package com.hun.cnk_remote_controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_setting_button.*
import kotlinx.android.synthetic.main.activity_setting_main.*

class BtnSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_button)

        var inputBtnCount: Int = 0

        edit_button_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputBtnCount = edit_button_count.text.toString().toInt()
            }
        })

        btn_next.setOnClickListener {
            val intent = Intent(this, BtnModifyActivity::class.java)
            intent.putExtra("btn_count", inputBtnCount)
            startActivityForResult(intent, Constant.REQ_NEW_BTN)
//            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Constant.RES_OK) {
            setResult(Constant.RES_OK)
            finish()
        }
    }
}
