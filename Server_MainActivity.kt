package com.rgraylemon.example.tcpserver

import android.graphics.BitmapFactory
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
import java.nio.ByteBuffer


//一応こっちは接続できた
class MainActivity : AppCompatActivity() {
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val imgView = findViewById<ImageView>(R.id.imageView)

        GlobalScope.launch(Dispatchers.Default) {
            // データ受信準備
            var receiveString = "NG"
            var serverSocket: ServerSocket? = null
            var reader: BufferedReader? = null

            val byteArray = ByteBuffer.allocate(1024)
            val allByteArray = ByteArrayOutputStream()
            var byteSize = 0

            // データ受信
            try {
                serverSocket = ServerSocket(2001)

                // ここでデータを受信するまで待機
                val socket = serverSocket.accept()
                handler.post {
                    textView.setText("Conneting!")
                }
                allByteArray.reset()
                // 受信したデータを格納
                socket.inputStream.read(byteArray.array())

                val mes = byteArray.getInt()
                val imgSize = byteArray.getInt()
                val headImage = byteArray.getInt()
                println(headImage)

                allByteArray.write(byteArray.array())
                byteSize += 1024

                while(byteSize<imgSize){
                    socket.inputStream.read(byteArray.array())

                    allByteArray.write(byteArray.array())
                    byteSize += 1024
                }

                val img = BitmapFactory.decodeByteArray(allByteArray.toByteArray(),8,allByteArray.toByteArray().size-8)
                handler.post{
                    imgView.setImageBitmap(img)
                }

                /*+reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                receiveString = reader.readLine()
                println(receiveString)
                textView.setText(receiveString)
                reader.close()*/
                socket.close()
                serverSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }
}