package com.hun.cnk_remote_controller

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hun.cnk_remote_controller.adapter.ConnBtnAdapter
import com.hun.cnk_remote_controller.data.BtnRealmObject
import com.hun.cnk_remote_controller.data.ButtonItem
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_connection.*
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.IllegalArgumentException

class ConnectionActivity : AppCompatActivity() {

    private val buttonItems: ArrayList<ButtonItem> = ArrayList()
    private val connBtnAdapter = ConnBtnAdapter(buttonItems)
    private var isRunning = false
    private lateinit var bluetoothService: BluetoothService
    private lateinit var bluetoothDialog: BluetoothDialog
    private lateinit var handler: Handler
    private val baseBytes: ByteArray = byteArrayOf(1, 2, 4, 8, 16, 32, 64, 128.toByte())
    private var sendBytes: ByteArray = byteArrayOf()
    private var upperPos: Byte = 0
    private var lowerPos: Byte = 0
    private var upperOnOff: Byte = 0
    private var lowerOnOff: Byte = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        setSupportActionBar(toolbar_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        handler = Handler {
            when (it.what) {
                Constant.MESSAGE_READ -> {
//                    val readBytes: ByteArray = it.obj as ByteArray
//                    val readString = String(readBytes, US_ASCII)
                    val readString: String = it.obj as String
//                    text_read.text = readString
                }

                Constant.MESSAGE_WRITE -> {
//                    val readBytes: ByteArray = it.obj as ByteArray
                    val readByte: Int = it.obj as Int
//                    val readString = String(readByte, US_ASCII)
                    val readString = readByte.toString()
//                    text_message.text = readString
                }

                Constant.MESSAGE_TOAST -> {
                    val message = it.obj.toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }

                Constant.MESSAGE_CONNECTED -> {
                    if (::bluetoothService.isInitialized) {
                        Toast.makeText(applicationContext, "디바이스에 연결되었습니다", Toast.LENGTH_SHORT).show()
//                        bluetoothService.startRead()

//                        setDeviceConnectingIcon(true)
                        bluetoothDialog.dismiss()
                    } else {
                        Toast.makeText(applicationContext, "Late init exception", Toast.LENGTH_SHORT).show()
                    }
                }

                Constant.MESSAGE_DISCONNECTED -> {
                    if (::bluetoothService.isInitialized) {
                        Toast.makeText(applicationContext, "디바이스와의 연결이 끊어졌습니다", Toast.LENGTH_SHORT).show()
                        bluetoothService.close()

//                        setDeviceConnectingIcon(false)
                    } else {
                        Toast.makeText(applicationContext, "Late init exception", Toast.LENGTH_SHORT).show()
                    }
                }

                Constant.MESSAGE_DEVICE -> {
                    val device: BluetoothDevice = it.obj as BluetoothDevice
                    bluetoothService.connect(device)
                }

                Constant.MESSAGE_ERROR -> {
                    val message = it.obj.toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

//                    setDeviceConnectingIcon(false)
                    bluetoothDialog.dismiss()
                }
            }
            true
        }

        bluetoothService = BluetoothService(handler)

        recycler_button_list.adapter = connBtnAdapter
        recycler_button_list.layoutManager = GridLayoutManager(this, 2)
        listBtnItems()

        connBtnAdapter.setOnItemTouchListener(object : ConnBtnAdapter.OnItemTouchListener {
            override fun onItemTouchActionDown(view: View, motionEvent: MotionEvent, position: Int) {
                val isToggleBtn = buttonItems[position].mode

                if (!isToggleBtn) {  // If this button is not a toggle button
                    setSendBytes(position, true)

                    if (::bluetoothService.isInitialized) {  // Checking BluetoothService class has been initialized
                        if (bluetoothService.isConnected()) {  // Checking bluetooth connection with server
                            sendPacketManually()
//                            if (!isRunning) {  // If not a scope is already running...
//                                sendPacketManually()
//                            }
                        } else {
                            showSnackBar("블루투스를 연결해주세요")
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "BluetoothService class is not initialized.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {  // If this button is a toggle button
//                    setSendBytes(position, buttonItems[position].state)
                }
            }

            override fun onItemTouchActionUp(view: View, motionEvent: MotionEvent, position: Int) {
                val isToggleBtn = buttonItems[position].mode

                if (isToggleBtn) {  // Toggle
                    buttonItems[position].state = !buttonItems[position].state  // Button state switching
                    setSendBytes(position, buttonItems[position].state)

                    if (::bluetoothService.isInitialized) {  // Checking BluetoothService class has been initialized
                        if (bluetoothService.isConnected()) {  // Checking bluetooth connection with server
                            sendPacketManually()
//                            if (!isRunning) {  // If not a scope is already running...
//                                sendPacketManually()
//                            }
                        } else {
                            showSnackBar("블루투스를 연결해주세요")
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "BluetoothService class is not initialized.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    setSendBytes(position, false)
                }
            }

            override fun onItemTouchActionCancel(view: View, motionEvent: MotionEvent, position: Int) {
                setSendBytes(position, false)
            }
        })
    }

    private fun setSendBytes(position: Int, onOff: Boolean) {
        if (onOff) {
            if (position < 8) {
                lowerPos = (lowerPos + baseBytes[position]).toByte()
                lowerOnOff = (lowerOnOff + baseBytes[position]).toByte()
            } else if (position < 16) {
                upperPos = (upperPos + baseBytes[position]).toByte()
                upperOnOff = (upperOnOff + baseBytes[position]).toByte()
            }
        } else {
            if (position < 8) {
                lowerPos = (lowerPos - baseBytes[position]).toByte()
                lowerOnOff = (lowerOnOff - baseBytes[position]).toByte()
            } else if (position < 16) {
                upperPos = (upperPos - baseBytes[position]).toByte()
                upperOnOff = (upperOnOff - baseBytes[position]).toByte()
            }
        }

        Log.d("Debug", "lowerPos: $lowerPos")
        Log.d("Debug", "lowerOnOff: $lowerOnOff")
        Log.d("Debug", "upperPos: $upperPos")
        Log.d("Debug", "upperOnOff: $upperOnOff")

        sendBytes = byteArrayOf(0x68, upperPos, lowerPos, upperOnOff, lowerOnOff, 0x7e)
    }

    private fun listBtnItems() {
        val btnResults = Realm.getDefaultInstance().where(BtnRealmObject::class.java).findAll()

        for (btnResult in btnResults) {
            connBtnAdapter.addItem(btnResult.name, btnResult.mode)
        }
    }

    private fun sendPacketManually() {
        try {
            val totalBytes = upperPos + lowerPos + upperOnOff + lowerOnOff
            Log.d("Debug", "Scope is running... bytes are - $totalBytes")
            Log.d("Debug", "lowerPos: $lowerPos")
            Log.d("Debug", "lowerOnOff: $lowerOnOff")
            Log.d("Debug", "upperPos: $upperPos")
            Log.d("Debug", "upperOnOff: $upperOnOff")

            bluetoothService.writeBytes(sendBytes)
        } catch (e: IOException) {
            Log.d("Debug", "Error from writeManuallyScope", e)
        }
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                isRunning = true
//                var count = 0
//
//                while (bluetoothService.isConnected()) {
//                    val totalBytes = upperPos + lowerPos + upperOnOff + lowerOnOff
////                    Log.d("Debug", "Scope is running... - ${count++}")
//                    if (totalBytes <= 0) {
//                        isRunning = false
//                        this.cancel()
//                    }
//                    bluetoothService.writeBytes(sendBytes)
//                    delay(5)
//
////                    if (isSendFlag) {
////                        Log.d("Debug", "Action down - ${count++}")
//////                        val bytes = makePacketBytes(position, true)
//////                        bluetoothService.writeBytes(sendBytes)
////                    } else {
////                        Log.d("Debug", "Action up - ${count++}")
//////                        val bytes = makePacketBytes(position, false)
//////                        bluetoothService.writeBytes(sendBytes)
////                        this.cancel()
////                    }
//                }
//            } catch (e: IOException) {
//                Log.d("Debug", "Error from writeManuallyScope", e)
//                isRunning = false
//                this.cancel()
//            }
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(container_main, message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("확인") { snackBar.dismiss() }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bluetooth_setting -> {
                bluetoothDialog = BluetoothDialog(handler)

                if (::bluetoothDialog.isInitialized) {
                    bluetoothDialog.show(supportFragmentManager, "missiles")
                }

                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Receiver
     */
    private fun registerBluetoothReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        this.registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Log.d("Debug", "Bluetooth device disconnected")
//                    setDeviceConnectingIcon(false)
                    bluetoothService.close()
                }
            }
        }
    }

    /**
     * Life cycle
     */
    override fun onResume() {
        super.onResume()
        Log.d("Debug", "onResume")
        registerBluetoothReceiver()
    }

    override fun onStop() {
        super.onStop()
        Log.d("Debug", "onStop")

        try {
            this.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            Log.d("Debug", "Failed to unregister receiver", e)
//            val errorMsg = handler.obtainMessage(Constants.MESSAGE_ERROR, "Failed to unregister receiver")
//            errorMsg.sendToTarget()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "onDestroy")

        if (::bluetoothService.isInitialized) {
            bluetoothService.close()
        } else {
            Log.d("Debug", "BluetoothService is not initialized")
        }
    }

    //    private fun makePacketBytes(onOff: Boolean): ByteArray {
//        val state: Byte = if (onOff) 0x01
//        else 0x00
//
//        val positionBytes: IntArray = getPositionByte(isToggledArray, onOff)
//
//        return byteArrayOf(0x68, positionBytes[0].toByte(), positionBytes[1].toByte(), state, 0x7E)
//    }
}
