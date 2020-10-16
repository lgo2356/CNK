package com.hun.cnk_remote_controller

import android.bluetooth.BluetoothDevice
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
import com.hun.cnk_remote_controller.adapter.ConnBtnAdapter
import com.hun.cnk_remote_controller.data.BtnRealmObject
import com.hun.cnk_remote_controller.data.ButtonItem
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_connection.*
import kotlinx.coroutines.*
import java.io.IOException

class ConnectionActivity : AppCompatActivity() {

    private val buttonItems: ArrayList<ButtonItem> = ArrayList()
    private val connBtnAdapter = ConnBtnAdapter(buttonItems)
    private var isBtnActionDown = false
    private lateinit var bluetoothService: BluetoothService
    private lateinit var bluetoothDialog: BluetoothDialog
    private lateinit var handler: Handler

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
                isBtnActionDown = true

                if (::bluetoothService.isInitialized) {
                    if (bluetoothService.isConnected()) {
                        sendPacketManually(position)
                    } else {
//                        showSnackBar("블루투스를 연결해주세요")
                    }
                } else {
                    Toast.makeText(applicationContext, "Late init exception", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onItemTouchActionUp(view: View, motionEvent: MotionEvent, position: Int) {
                isBtnActionDown = false
            }

            override fun onItemTouchActionCancel(view: View, motionEvent: MotionEvent, position: Int) {
                isBtnActionDown = false
            }
        })
    }

    private fun listBtnItems() {
        val btnResults = Realm.getDefaultInstance().where(BtnRealmObject::class.java).findAll()

        for (btnResult in btnResults) {
            connBtnAdapter.addItem(btnResult.name)
        }
    }

    private fun sendPacketManually(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var count = 0

                while (bluetoothService.isConnected()) {
                    if (isBtnActionDown) {
                        Log.d("Debug", "Action down - ${count++}")

                        val bytes = makePacketBytes(position, true)
                        bluetoothService.writeBytes(bytes)
                    } else {
                        Log.d("Debug", "Action up - ${count++}")

                        val bytes = makePacketBytes(position, false)
                        bluetoothService.writeBytes(bytes)
                        this.cancel()
                    }

                    delay(5)
                }
            } catch (e: IOException) {
                Log.d("Debug", "Error from writeManuallyScope", e)
                this.cancel()
            }
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

    private fun makePacketBytes(position: Int, onOff: Boolean): ByteArray {
        val bytes: Array<Int> = arrayOf(1, 2, 4, 8, 16, 32, 64, 128)
        var upperByte = 0
        var lowerByte = 0

        val state: Byte = if (onOff) 0x01
        else 0x00

        if (position < 8) {
            lowerByte = bytes[position]
        } else if (position < 16) {
            upperByte = bytes[position - 8]
        }

        return byteArrayOf(0x68, upperByte.toByte(), lowerByte.toByte(), state, 0x7E)
    }

    private fun getPositionByte(toggledArray: Array<Boolean>, onOff: Boolean): IntArray {
        val upperBytes: Array<Int> = arrayOf(1, 2, 4, 8, 16, 32, 64, 128)
        val lowerBytes: Array<Int> = arrayOf(1, 2, 4, 8, 16, 32, 64, 128)
        var positionUpperByte = 0
        var positionLowerByte = 0

        for (i in 0 until 8) {
            if (onOff) {
                if (toggledArray[i]) {
                    positionLowerByte += lowerBytes[i]
                }
            } else {
                if (!toggledArray[i]) {
                    positionLowerByte += lowerBytes[i]
                }
            }
        }

        for (i in 0 until 8) {
            if (onOff) {
                if (toggledArray[i + 8]) {
                    positionUpperByte += upperBytes[i]
                }
            } else {
                if (!toggledArray[i + 8]) {
                    positionUpperByte += upperBytes[i]
                }
            }
        }

        return intArrayOf(positionUpperByte, positionLowerByte)
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
}
