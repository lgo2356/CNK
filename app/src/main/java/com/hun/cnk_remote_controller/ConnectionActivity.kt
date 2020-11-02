package com.hun.cnk_remote_controller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
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

    private lateinit var preferences: SharedPreferences
    private val sharedPrefFile: String = "com.hun.cnk_remote_controller"
    private var deviceAddress: String = ""
    private val buttonItems: ArrayList<ButtonItem> = ArrayList()
    private val connBtnAdapter = ConnBtnAdapter(buttonItems)
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
                Constant.MESSAGE_TOAST -> {
                    val message = it.obj.toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }

                Constant.MESSAGE_CONNECTED -> {
                    if (::bluetoothService.isInitialized) {
                        Toast.makeText(applicationContext, "디바이스에 연결되었습니다", Toast.LENGTH_SHORT).show()
//                        bluetoothDialog.dismiss()
                    } else {
                        Toast.makeText(applicationContext, "Late init exception", Toast.LENGTH_SHORT).show()
                    }
                }

                Constant.MESSAGE_DISCONNECTED -> {
                    if (::bluetoothService.isInitialized) {
                        Toast.makeText(applicationContext, "디바이스와의 연결이 끊어졌습니다", Toast.LENGTH_SHORT).show()
                        bluetoothService.close()
                    } else {
                        Toast.makeText(applicationContext, "Late init exception", Toast.LENGTH_SHORT).show()
                    }
                }

                Constant.MESSAGE_DEVICE -> {
                    val device: BluetoothDevice = it.obj as BluetoothDevice
                    deviceAddress = device.address
                    bluetoothService.connect(device)
                }

                Constant.MESSAGE_ERROR -> {
                    val message = it.obj.toString()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
//                    bluetoothDialog.dismiss()
                }
            }
            true
        }

        bluetoothService = BluetoothService(handler)

        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        val address: String? = preferences.getString("address", "") ?: ""

        if (address?.isNotEmpty() == true) {
            val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device = btAdapter.getRemoteDevice(address)
            bluetoothService.connect(device)
        }

        recycler_button_list.adapter = connBtnAdapter
        recycler_button_list.layoutManager = GridLayoutManager(this, 2)
        listBtnItems()

        connBtnAdapter.setOnItemTouchListener(object : ConnBtnAdapter.OnItemTouchListener {
            override fun onItemTouchActionDown(view: View, motionEvent: MotionEvent, position: Int) {
                val isToggleBtn = buttonItems[position].mode

                if (!isToggleBtn) {
                    setSendBytes(position, true)
                    // Sending packet
                    if (::bluetoothService.isInitialized) {
                        if (bluetoothService.isConnected()) {
                            sendPacket()
                        } else {
                            showSnackBar("블루투스를 연결해주세요")
                        }
                    }
                }
            }

            override fun onItemTouchActionUp(view: View, motionEvent: MotionEvent, position: Int) {
                val isToggleBtn = buttonItems[position].mode

                if (isToggleBtn) {  // Toggle
                    buttonItems[position].state = !buttonItems[position].state  // Button state switching
                    setSendBytes(position, buttonItems[position].state)
                    // Sending packet
                    if (::bluetoothService.isInitialized) {
                        if (bluetoothService.isConnected()) {
                            sendPacket()
                        } else {
                            showSnackBar("블루투스를 연결해주세요")
                        }
                    }
                } else {  // Default
                    setSendBytes(position, false)
                    // Sending packet
                    if (::bluetoothService.isInitialized) {
                        if (bluetoothService.isConnected()) {
                            sendPacket()
                        } else {
                            showSnackBar("블루투스를 연결해주세요")
                        }
                    }
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
        sendBytes = byteArrayOf(0x68, upperPos, lowerPos, upperOnOff, lowerOnOff, 0x7e)
    }

    private fun listBtnItems() {
        val btnResults = Realm.getDefaultInstance().where(BtnRealmObject::class.java).findAll()

        for (btnResult in btnResults) {
            connBtnAdapter.addItem(btnResult.name, btnResult.mode)
        }
    }

    private fun sendPacket() {
        try {
            bluetoothService.writeBytes(sendBytes)
        } catch (e: IOException) {
            Log.d("Debug", "Error from writeManuallyScope", e)
        }
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

        try {
            this.registerReceiver(receiver, filter)
        } catch (e: IllegalAccessException) {
            Log.d("Debug", "Failed to register receiver", e)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Log.d("Debug", "Bluetooth device disconnected")
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

    override fun onPause() {
        super.onPause()

        if (deviceAddress.isNotEmpty()) {
            val preferencesEditor: SharedPreferences.Editor = preferences.edit()
            preferencesEditor.putString("address", deviceAddress)
            preferencesEditor.apply()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("Debug", "onStop")

        try {
            this.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            Log.d("Debug", "Failed to unregister receiver", e)
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
}
