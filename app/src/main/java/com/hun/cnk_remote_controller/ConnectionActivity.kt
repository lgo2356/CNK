package com.hun.cnk_remote_controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.hun.cnk_remote_controller.adapter.ConnBtnAdapter
import com.hun.cnk_remote_controller.data.BtnRealmObject
import com.hun.cnk_remote_controller.data.ButtonItem
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_connection.*

class ConnectionActivity : AppCompatActivity() {

    private val buttonItems: ArrayList<ButtonItem> = ArrayList()
    private val connBtnAdapter = ConnBtnAdapter(buttonItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        recycler_button_list.adapter = connBtnAdapter
        recycler_button_list.layoutManager = GridLayoutManager(this, 2)
        listBtnItems()
    }

    private fun listBtnItems() {
        val btnResults = Realm.getDefaultInstance().where(BtnRealmObject::class.java).findAll()

        for (btnResult in btnResults) {
            connBtnAdapter.addItem(btnResult.name)
        }
    }
}