package com.hun.cnk_remote_controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hun.cnk_remote_controller.adapter.ModifyBtnAdapter
import com.hun.cnk_remote_controller.data.BtnRealmObject
import com.hun.cnk_remote_controller.data.ButtonItem
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_modification_button.*

class BtnModifyActivity : AppCompatActivity() {
    private val buttonItems: ArrayList<ButtonItem> = ArrayList()
    private val modifyBtnAdapter: ModifyBtnAdapter = ModifyBtnAdapter(buttonItems)
    private var reqCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_button)

        reqCode = intent.getIntExtra("req_code", -1)
        recycler_modification_list.adapter = modifyBtnAdapter
        listBtnItems()

        btn_done.setOnClickListener {
            for (i in 0 until buttonItems.size) {
                val btnNumber: Int = buttonItems[i].number
                val btnName: String = buttonItems[i].name
                val btnMode: Boolean = buttonItems[i].mode

                saveButtonToRealm(btnNumber, btnName, btnMode)
            }

            setResult(Constant.RES_OK)
            finish()
        }
    }

    private fun listBtnItems() {
        val btnResults = Realm.getDefaultInstance().where(BtnRealmObject::class.java).findAll()

        for (btnResult in btnResults) {
            modifyBtnAdapter.addItem(btnResult.number, btnResult.name, btnResult.mode)
        }

        // 버튼 새로 생성하기
        if (reqCode != Constant.REQ_NEW_BTN) {
            return
        }

        val btnCount = intent.getIntExtra("btn_count", -1)

        if (btnCount == -1) {
            return
        }

        val maxNumber = Realm.getDefaultInstance().where(BtnRealmObject::class.java).max("number") as Long

        for (number in maxNumber.toInt() + 1..btnCount + maxNumber.toInt()) {
            modifyBtnAdapter.addItem(number, "입력", false)
        }
    }

    private fun saveButtonToRealm(number: Int, name: String, mode: Boolean) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                val btnRealm = realm.where(BtnRealmObject::class.java).equalTo("number", number).findFirst()
                    ?: BtnRealmObject()

                btnRealm.number = number
                btnRealm.name = name
                btnRealm.mode = mode
                realm.copyToRealm(btnRealm)
            }
        }
    }
}
