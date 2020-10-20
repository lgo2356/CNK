package com.hun.cnk_remote_controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.hun.cnk_remote_controller.data.BtnRealmObject
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_setting_button.*
import kotlinx.android.synthetic.main.activity_setting_main.*
import java.lang.Exception

class BtnSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_button)

        var inputBtnCount = 0

        edit_button_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputBtnCount = try {
                    edit_button_count.text.toString().toInt()
                } catch (e: Exception) {
                    0
                }
            }
        })

        btn_next.setOnClickListener {
            val btnCount: Int = Realm.getDefaultInstance().where(BtnRealmObject::class.java).findAll().count()
            if (btnCount + inputBtnCount > 10) {
//                showSnackBar("최대 10개까지 입력할 수 있습니다. (현재: ${btnCount}개)")
                Toast.makeText(this, "최대 10개까지 입력할 수 있습니다. (현재: ${btnCount}개)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputBtnCount == 0) {
                return@setOnClickListener
            }

            val intent = Intent(this, BtnModifyActivity::class.java)
            intent.putExtra("req_code", Constant.REQ_NEW_BTN)
            intent.putExtra("btn_count", inputBtnCount)
            startActivityForResult(intent, Constant.REQ_NEW_BTN)
        }
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(main_container, message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("확인") { snackBar.dismiss() }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Constant.RES_OK) {
            setResult(Constant.RES_OK)
            finish()
        }
    }
}
