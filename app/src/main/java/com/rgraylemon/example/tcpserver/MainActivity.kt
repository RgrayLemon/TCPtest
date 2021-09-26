package com.rgraylemon.example.tcpserver

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

const val MSG_CONNECTION_SUCCESS = 111 // 接続成功
const val MSG_CONNECTION_FAILED = 222  // 接続失敗
const val MSG_IOEXCEPTION = 333        // 例外発生

//一応こっちは接続できた
/*class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViwe = findViewById<TextView>(R.id.textview)

        GlobalScope.launch(Dispatchers.Default) {
            // データ受信準備
            var receiveString = "NG"
            var serverSocket: ServerSocket? = null
            var reader: BufferedReader? = null

            // データ受信
            try {
                serverSocket = ServerSocket(2001)

                println("running...")
                // ここでデータを受信するまで待機
                val socket = serverSocket.accept()

                // 受信したデータを格納
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                receiveString = reader.readLine()
                println(receiveString)
                //textViwe.setText(receiveString)
                reader.close()
                socket.close()
                serverSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }
}*/

//こっちも接続できた
class MainActivity : AppCompatActivity() {
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serverSocket = ServerSocket()
        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress(Constant.Connection.PORT_NUMBER))

        val textView = findViewById<TextView>(R.id.textView)
        val imgView = findViewById<ImageView>(R.id.imageView)
        textView.setText("waiting...")

        var cnt = 0

        //別スレッドで動かす必要がある
        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                try {
                    //接続待ち
                    val socket: Socket = serverSocket.accept()
                    println("Welcome $cnt")
                    handler.post {
                        textView.setText("Conneting!")
                    }

                    val connectToClient = ConnectToClient(socket = socket, id = cnt)
                    connectToClient.start()
                    cnt++


                } catch (e: Exception) {
                    println("Error(3)")
                    println(e.toString())
                    break
                }

            }

        }
    }

    fun main() {
        val serverSocket = ServerSocket()
        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress(Constant.Connection.PORT_NUMBER))
        println("running")

        var cnt = 0

        while (true) {
            try {
                //接続待ち
                val socket: Socket = serverSocket.accept()
                println("Welcome $cnt")
                val connectToClient = ConnectToClient(socket = socket, id = cnt)
                connectToClient.start()
                cnt++
            } catch (e: Exception) {
                println("Error(3)")
                println(e.toString())
                break
            }
        }
    }
}