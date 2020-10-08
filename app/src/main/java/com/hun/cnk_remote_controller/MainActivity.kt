package com.hun.cnk_remote_controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_connection.setOnClickListener {
            val intent = Intent(this, BtnModActivity::class.java)
            startActivity(intent)
        }
    }
}