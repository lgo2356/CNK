package com.hun.cnk_remote_controller

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.*
import kotlinx.coroutines.*

class BluetoothService(private val handler: Handler) {

//    companion object {
//        var mSocket: BluetoothSocket? = null
//        var mOutputStream: OutputStream? = null
//        var mInputStream: InputStream? = null
//    }

    private val insecureUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mSocket: BluetoothSocket? = null
    private var mOutputStream: OutputStream? = null
    private var mInputStream: InputStream? = null

//    private val readJob = Job()
//    private val writeByteJob = Job()
//    private val writeBytesJob = Job()
//    private val readScope = CoroutineScope(Dispatchers.Main + readJob)

    fun connect(device: BluetoothDevice) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                mSocket = device.createInsecureRfcommSocketToServiceRecord(insecureUuid)

                if (mSocket != null) {
                    mSocket?.connect()
                    mOutputStream = mSocket?.outputStream
                    mInputStream = mSocket?.inputStream

                    handler.sendEmptyMessage(Constant.MESSAGE_CONNECTED)
                } else {
                    sendErrorMessage("블루투스 연결에 실패했습니다")
                }
            } catch (e: IOException) {
                Log.d("Debug", "Couldn't connect to your device", e)
                sendErrorMessage("블루투스 연결에 실패했습니다")
            }
        }
    }

    fun writeBytes(bytes: ByteArray) {
        try {
            mOutputStream?.write(bytes) ?: sendErrorMessage("패킷 전송에 실패했습니다")
        } catch (e: IOException) {
            Log.d("Debug", "패킷 전송에 실패했습니다")
            val msg = handler.obtainMessage(Constant.MESSAGE_ERROR, "데이터 전송에 실패했습니다")
            msg.sendToTarget()
            throw IOException()
        }
    }

//    fun startRead() {
//        readScope.launch(Dispatchers.IO) {
//            val buffer = ByteArray(1024)
//            var bufferPosition = 0
//            val delimiter: Byte = 0x0A
//
//            try {
//                while (mSocket != null) {
//                    var bytesAvailable = 0
//
//                    if (mSocket != null && mSocket?.isConnected == true) {
//                        bytesAvailable = mInputStream?.available() ?: 0
//                    }
//
//                    if (bytesAvailable > 0) {
//                        val packetBytes = ByteArray(bytesAvailable)
//                        mSocket?.inputStream?.read(packetBytes)
//
//                        for (i in 0 until bytesAvailable) {
//                            val byte: Byte = packetBytes[i]
//
//                            if (byte == delimiter) {
//                                val encodedBytes = ByteArray(bufferPosition)
//                                System.arraycopy(buffer, 0, encodedBytes, 0, encodedBytes.size)
//
//                                val readString = String(encodedBytes, StandardCharsets.US_ASCII)
//                                bufferPosition = 0
//
//                                val readMsg = handler.obtainMessage(Constant.MESSAGE_READ, readString)
//                                readMsg.sendToTarget()
//                            } else {
//                                buffer[bufferPosition++] = byte
//                            }
//                        }
//                    }
//                }
//            } catch (e: IOException) {
//                Log.d("Debug", "Input stream was disconnected", e)
//                readJob.cancel()
//                close()
//            } catch (e: UnsupportedEncodingException) {
//                Log.d("Debug", "Unsupported encoding format", e)
//                val errorMsg = handler.obtainMessage(Constant.MESSAGE_TOAST, "Unsupported encoding format")
//                errorMsg.sendToTarget()
//            } catch (e: KotlinNullPointerException) {
//                val errorMsg = handler.obtainMessage(Constant.MESSAGE_TOAST, "Kotlin null pointer exception")
//                errorMsg.sendToTarget()
//                e.printStackTrace()
//            }
//        }
//    }

    fun close() {
        try {
//            writeByteJob.cancel()
            mOutputStream?.close()
            mOutputStream = null
            Log.d("Debug", "Output stream closed")

//            readJob.cancel()
            mInputStream?.close()
            mInputStream = null
            Log.d("Debug", "Input stream closed")

            mSocket?.close()
            mSocket = null
            Log.d("Debug", "Socket closed")
        } catch (e: IOException) {
            Log.d("Debug", "Couldn't close the client socket", e)
            throw IOException("Couldn't close the client socket")
        }
    }

    private fun sendErrorMessage(msg: String) {
        val errorMsg = handler.obtainMessage(Constant.MESSAGE_ERROR, msg)
        errorMsg.sendToTarget()
    }

    fun isConnected(): Boolean {
        return if (mSocket == null) {
            false
        } else {
            mSocket?.isConnected ?: false
        }
    }
}