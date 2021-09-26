package com.rgraylemon.example.tcpserver

import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.Socket
import java.nio.ByteBuffer


class ConnectToClient(private val socket: Socket, private val id: Int): Thread() {

    private lateinit var inputStream: InputStream
    private lateinit var bufferedReader: BufferedReader
    private lateinit var printWriter: PrintWriter
    val handler = Handler()

    override fun run() {
        try {
            inputStream = socket.getInputStream()
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            printWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            while (inputStream.available() == 0) {}

            val byteArray = ByteBuffer.allocate(1024)
            val allByteArray = ByteArrayOutputStream()
            var byteSize = 0

            println("start!")
            sendOrReceive { _, inputStream ->
                while (byteSize < 1400000) {
                    inputStream.read(byteArray.array())

                    allByteArray.write(byteArray.array())
                    byteSize += 1024

                }
                //println("受信メッセージ："+byteArray.getInt())
                val img = BitmapFactory.decodeByteArray(
                    allByteArray.toByteArray(),
                    0,
                    allByteArray.toByteArray().size
                )
                handler.post{

                }
            }
                /*val received = bufferedReader.readLine()
                println("received data is $received")
                printWriter.println("[From Server] Received data is {$received}")
                printWriter.flush()*/

        } catch (e: Exception) {
            e.printStackTrace()

            try {
                close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        println("Bye $id")
    }

    fun sendOrReceive(callback: (OutputStream, InputStream) -> Unit) {
        if (socket == null) throw java.lang.IllegalStateException()
        socket?.also { socket ->
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    if (socket.isConnected) {
                        callback(socket.outputStream, socket.inputStream)
                    } else {

                    }
                } catch (e: IOException) {

                }
            }
        }
    }

    private fun close() {
        bufferedReader.close()
        printWriter.close()
        inputStream.close()
        socket.close()
    }
}