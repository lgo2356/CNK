package com.hun.cnk_remote_controller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting_main.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_main)

        btn_button_setting.setOnClickListener {
            val intent = Intent(this, BtnSettingActivity::class.java)
            intent.putExtra("req_code", Constant.REQ_NEW_BTN)
            startActivityForResult(intent, Constant.REQ_NORMAL)
//            startActivity(intent)
        }

        btn_button_modification.setOnClickListener {
            val intent = Intent(this, BtnModifyActivity::class.java)
            intent.putExtra("req_code", Constant.REQ_MODIFY_BTN)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Constant.RES_OK) {
            finish()
        }
    }
}