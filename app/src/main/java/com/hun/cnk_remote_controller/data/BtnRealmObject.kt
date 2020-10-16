package com.hun.cnk_remote_controller.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BtnRealmObject : RealmObject() {
    @PrimaryKey
    var id: Long = -1
    var number: Int = -1
    var name: String = "UNDEFINED"
    var mode: Boolean = false  // false: 수동, true: 자동
}