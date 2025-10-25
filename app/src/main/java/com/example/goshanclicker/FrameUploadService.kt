package com.example.goshanclicker

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import com.chaquo.python.Python
import com.example.goshanclicker.FrameUploadService.Companion.EMULATOR_URL
import com.example.goshanclicker.FrameUploadService.Companion.MOBILE_URL
import com.example.goshanclicker.FrameUploadService.Companion.TEST_GOSHA
import com.example.goshanclicker.atomic.AppMetrics
import com.example.goshanclicker.atomic.FrameQueue
import com.example.goshanclicker.atomic.ResponseQueue
import com.example.goshanclicker.model.InputRequest
import com.example.goshanclicker.model.InputResponse
import com.google.gson.Gson
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis

class FrameUploadService : Service() {

    /*
    val py = Python.getInstance()
    val modelModule = py.getModule("model")
    val goshanClass = modelModule.get("GoshanBrain")
    val goshanInstance = goshanClass!!.call()
     */


    companion object {
        const val EMULATOR_URL = "http://10.0.2.16:5300/should-click"
        const val MOBILE_URL = "http://127.0.0.1:5300/should-click"

        const val TEST_GOSHA =
            "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"
    }

    private var running = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(2, buildNotification())

        Thread {
            while (running) {
                try {
                    val request = FrameQueue.get()
                    if (request == null) {
                        Thread.sleep(200)
                        continue
                    } else {
                        sendFrameToServer(request)
                        //sendToPythonModule(request)
                    }
                } catch (e: Exception) {
                    Log.e("FrameUploadService", "Error sending frame", e)
                    Thread.sleep(1000)
                }
            }
        }.start()

        return START_STICKY
    }

    private fun sendToPythonModule(request: InputRequest) {
        val elapsed = measureTimeMillis {

            // получаем интерпретатор

            val py = Python.getInstance()


            // загружаем модуль portable.model
            val modelModule = py.getModule("model")

            // создаём экземпляр класса GoshanBrain
            val goshanClass = modelModule.get("GoshanBrain")
            val goshanInstance = goshanClass!!.call()


            // вызываем метод predict(screenshot, ms_since_last)
            val result = goshanInstance.callAttr("predict", request.image, request.msSinceClick)

            val decision = result.toInt()

            ResponseQueue.set(InputResponse(status = decision))
        }
        Log.i("FrameUploadService", "ПИТОН ДУМАЛ $elapsed"+"ms")
    }


    override fun onDestroy() {
        running = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val chanId = "frame_upload"
        val channel = NotificationChannel(chanId, "FrameUpload", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        return Notification.Builder(this, chanId)
            .setContentTitle("Отправка кадров")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }
}

private fun sendFrameToServer(request1: InputRequest) {
    var request = request1
    /*
    request = InputRequest(
        image = TEST_GOSHA,
        msSinceClick = request1.msSinceClick,
        timestamp = request1.timestamp,
    )
     */
    val json = JSONObject().apply {
        put("image", request.image)
        request.msSinceClick?.let { put("ms_since_click", it) }
        request.timestamp?.let { put("timestamp", it) }
    }

    // Настраиваем соединение
    val url = URL(EMULATOR_URL)
    val connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        doOutput = true
        connectTimeout = 5000
        readTimeout = 5000
    }

    // Отправляем JSON
    val elapsed = measureTimeMillis {
        OutputStreamWriter(connection.outputStream).use {
            it.write(json.toString())
        }

        val responseCode = connection.responseCode
        val responseText = connection.inputStream.bufferedReader().readText()

        Log.i("FrameUploadService", "HTTP Response ($responseCode): $responseText")

        val response = Gson().fromJson(responseText, InputResponse::class.java)
        ResponseQueue.set(response)
    }

    // Регистрируем метрику
    AppMetrics.recordRequestTime(
        timeMillis = elapsed
    )
    Log.i("FrameUploadService", "HTTP Response TIME ${AppMetrics.getAverageRequestTime()}ms Last TIME ${AppMetrics.getLastRequestTime()}ms")

    FrameQueue.clear()
}

fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
    return android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.NO_WRAP)
}
