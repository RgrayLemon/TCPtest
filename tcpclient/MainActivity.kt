package com.rgraylemon.example.tcpclient

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


const val MSG_CONNECTION_SUCCESS = 111 // 接続成功
const val MSG_CONNECTION_FAILED = 222  // 接続失敗
const val MSG_IOEXCEPTION = 333        // 例外発生

class MainActivity : AppCompatActivity() {
    private var tcpcom: ComTcpClient? = null
    //val ip = "10.0.2.2"
    val ip = "192.168.3.10"
    val port = "2001"    // "55555"が代入される。
    private val TAG = MainActivity::class.java.simpleName
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connect()

        val imgView = findViewById<ImageView>(R.id.imageView)
        val button = findViewById<Button>(R.id.sendButton)
        button.setOnClickListener{
            //todo なんかメッセージ送る
            val sendArray = ByteBuffer.allocate(30)
            sendArray.putInt(0x01020304)
            sendArray.putInt(0x05060708)
            val byteArray = ByteBuffer.allocate(1024)
            val allByteArray = ByteArrayOutputStream()
            var byteSize = 0
            try {
                tcpcom?.sendOrReceive { outputStream, inputStream ->
                    outputStream.write(sendArray.array())
                    while(byteSize<1400000) {
                        inputStream.read(byteArray.array())

                        allByteArray.write(byteArray.array())
                        byteSize += 1024


                    }
                    //println("受信メッセージ："+byteArray.getInt())
                    val img = BitmapFactory.decodeByteArray(allByteArray.toByteArray(),0,allByteArray.toByteArray().size)
                    handler.post{
                        imgView.setImageBitmap(img)
                    }
                    //画像を外部ファイルに保存

                    
                }
                //imgView.setImageBitmap(img)
            }
            catch(e: Exception){
                Log.d(TAG,"Failed...")
            }
        }

        val channel = Channel<Int>()
        GlobalScope.launch(Dispatchers.Main) {
            when (channel.receive()) {
                MSG_CONNECTION_SUCCESS -> {
                    Log.d(TAG,"Success!")
                }
                MSG_CONNECTION_FAILED -> {
                    Log.d(TAG,"Failed...")
                    // エラー処理
                }
                MSG_IOEXCEPTION -> {
                    Log.d(TAG,"Exception!")
                    //エラー処理
                }
            }
        }
    }

    fun connect(){
        val channel = Channel<Int>()
        if (!ip.isEmpty() && !port.isEmpty()) {
            tcpcom = ComTcpClient(ip, port.toInt(), channel)
            tcpcom?.connect()
        }
    }
}