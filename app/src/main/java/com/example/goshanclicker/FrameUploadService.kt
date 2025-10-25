package com.example.goshanclicker

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class FrameUploadService : Service() {

    companion object {
        const val EMULATOR_URL = "http://10.0.2.16:5300/should-click"
        const val MOBILE_URL = "http://127.0.0.1:5300/should-click"
    }

    private var running = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(2, buildNotification())

        Thread {
            while (running) {
                try {
                    // Берём последний запрос (если есть)
                    val request = FrameQueue.get()
                    if (request == null) {
                        Thread.sleep(200)
                        continue
                    }

                    // Преобразуем InputRequest → JSON
                    val json = JSONObject().apply {
                        put("image", request.image)
                        request.msSinceClick?.let { put("ms_since_click", it) }
                        request.timestamp?.let { put("timestamp", it) }
                    }

                    // Настраиваем соединение
                    val url = URL(MOBILE_URL)
                    val connection = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        doOutput = true
                        connectTimeout = 5000
                        readTimeout = 5000
                    }

                    // Отправляем JSON
                    OutputStreamWriter(connection.outputStream).use {
                        it.write(json.toString())
                    }

                    // Читаем ответ
                    val responseCode = connection.responseCode
                    val responseText = connection.inputStream.bufferedReader().readText()

                    Log.i("FrameUploadService", "HTTP Response ($responseCode): $responseText")

                    // Создаём объект ответа
                    val response = Gson().fromJson(responseText, InputResponse::class.java)

                    // Кладём ответ в ResponseQueue
                    ResponseQueue.set(response)

                    // Очищаем очередь запросов, чтобы не отправлять повторно
                    //FrameQueue.clear()

                } catch (e: Exception) {
                    Log.e("FrameUploadService", "Error sending frame", e)
                    Thread.sleep(1000)
                }
            }
        }.start()

        return START_STICKY
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

fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
    return android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.NO_WRAP)
}
