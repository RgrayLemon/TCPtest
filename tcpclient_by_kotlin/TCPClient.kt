package com.rgraylemon.example.tcpclient

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.UnknownHostException

class ComTcpClient(val ip: String, val port: Int, val channel: Channel<Int>) {
    private var socket: Socket? = null
    private val TAG = ComTcpClient::class.java.simpleName

    fun connect() {
        GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "接続開始...")
            try {
                socket = Socket(ip, port)
                channel.send(MSG_CONNECTION_SUCCESS)

            } catch (e: IOException) {
                Log.e(TAG, "IOException", e)
                channel.send(MSG_IOEXCEPTION)

            } catch (e: UnknownHostException) {
                Log.e(TAG, "UnknownHostException", e)
                channel.send(MSG_CONNECTION_FAILED)
            }
        }
    }

    fun sendOrReceive(callback: (OutputStream, InputStream) -> Unit) {
        socket?.soTimeout = 1000
        if (socket == null) throw java.lang.IllegalStateException()
        val startTime = System.currentTimeMillis()
        socket?.also { socket ->
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    if (socket.isConnected) {
                        println("SOCKET!")
                        callback(socket.outputStream, socket.inputStream)
                    } else {
                        channel.send(MSG_CONNECTION_FAILED)
                        println("disconnect...")
                    }
                } catch(e: java.net.SocketTimeoutException){
                    println("Timeout!!")
                    print(e)
                } catch(e: java.net.SocketException){
                    println("socket error")
                    print(e)
                } catch (e: IOException) {
                    channel.send(MSG_IOEXCEPTION)
                    println("ioexception")
                } catch(e: Exception){
                    println("error")
                }
            }
        }
        val endTime = System.currentTimeMillis()
        val doTime = endTime-startTime
        println("socket処理時間："+doTime)
    }

    fun close() {
        if (socket == null) throw java.lang.IllegalStateException()
        socket?.also { socket ->
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    if (socket.isConnected) socket.close()
                } catch (e: IOException) {
                    channel.send(MSG_IOEXCEPTION)
                }
            }
        }
    }
}