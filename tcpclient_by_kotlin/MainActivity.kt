package com.rgraylemon.example.tcpclient

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.channels.Channel
import java.nio.ByteBuffer


const val MSG_CONNECTION_SUCCESS = 111 // 接続成功
const val MSG_CONNECTION_FAILED = 222  // 接続失敗
const val MSG_IOEXCEPTION = 333        // 例外発生

class MainActivity : AppCompatActivity() {
    private var tcpcom: ComTcpClient? = null
    val ip = "10.0.2.2"
    //val ip = "192.168.3.10"
    val port = "2001"    // "55555"が代入される。
    private val TAG = MainActivity::class.java.simpleName
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connect()

        //val button = findViewById<Button>(R.id.sendButton)
        //button.setOnClickListener{
            val runnable = object: Runnable{
                override fun run(){
                    val sendArray = ByteBuffer.allocate(30)
                    sendArray.putInt(0x01020304)
                    val byteArray = ByteBuffer.allocate(30)
                    var returnSize = 0

                    try {
                        tcpcom?.sendOrReceive { outputStream, inputStream ->
                            println("output")
                            val startTime = System.currentTimeMillis()
                            outputStream.write(sendArray.array())
                            try{
                                println("input")
                                returnSize = inputStream.read(byteArray.array())

                                println("受信サイズ"+returnSize)
                                if(returnSize==-1){
                                    println("timeout")
                                }
                                else{
                                    println("get!")
                                }


                            } catch(e: java.net.SocketTimeoutException){
                                println("timeout")
                            }
                            val endTime = System.currentTimeMillis()
                            val doTime = endTime-startTime
                            println("処理時間："+doTime)

                            /*while(byteSize<1400000) {
                                inputStream.read(byteArray.array())

                                allByteArray.write(byteArray.array())
                                byteSize += 1024


                            }
                            //println("受信メッセージ："+byteArray.getInt())
                            val img = BitmapFactory.decodeByteArray(allByteArray.toByteArray(),0,allByteArray.toByteArray().size)
                            handler.post{
                                imgView.setImageBitmap(img)
                            }*/
                            //画像を外部ファイルに保存
                        }
                    }
                    catch(e: Exception){
                        Log.d(TAG,"Failed...")
                    }
                    handler.postDelayed(this,5000)
                }
            }
            handler.post(runnable)

        //}

    }

    fun connect(){
        val channel = Channel<Int>()
        if (!ip.isEmpty() && !port.isEmpty()) {
            tcpcom = ComTcpClient(ip, port.toInt(), channel)
            tcpcom?.connect()
        }
    }
}