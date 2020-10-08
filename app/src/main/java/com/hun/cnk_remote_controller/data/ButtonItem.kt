package com.hun.cnk_remote_controller.data

data class ButtonItem(
    var number: String,
    var name: String = "",
    var mode: Boolean = false  // false: 수동, true: 자동
)