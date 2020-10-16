package com.hun.cnk_remote_controller

class Constant {
    companion object {
        // Bluetooth
        const val REQUEST_ENABLE_BLUETOOTH = 1000
        const val REQUEST_PERMISSIONS = 2000

        // IO
        const val MESSAGE_READ: Int = 0
        const val MESSAGE_WRITE: Int = 1
        const val MESSAGE_TOAST: Int = 2
        const val MESSAGE_PROGRESS: Int = 3
        const val MESSAGE_CONNECTED: Int = 4
        const val MESSAGE_DEVICE: Int = 5
        const val MESSAGE_ERROR: Int = 6
        const val MESSAGE_DISCONNECTED: Int = 7

        // Activity request code
        const val REQ_NORMAL = 100
        const val REQ_NEW_BTN = 101
        const val REQ_MODIFY_BTN = 102

        // Activity result code
        const val RES_OK = 500

        // Common code

    }
}