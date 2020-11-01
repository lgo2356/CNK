package com.hun.cnk_remote_controller.data

class ButtonItem {
    var number: Int = -1
    var name: String = "UNDEFINED"
    var mode: Boolean = false  // false: 수동, true: 자동
    var state: Boolean = false // true: 눌림, false: 안 눌림
}