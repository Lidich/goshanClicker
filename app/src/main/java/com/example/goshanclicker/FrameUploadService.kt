package com.example.goshanclicker

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
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
                    val bitmap: Bitmap = FrameQueue.queue.take()
                    val imageBase64 = bitmap.toBase64()

                    val json = JSONObject().apply {
                        put("image", imageBase64)
                    }

                    val url = URL(MOBILE_URL)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    OutputStreamWriter(connection.outputStream).use { it.write(json.toString()) }

                    val responseCode = connection.responseCode
                    val responseText = connection.inputStream.bufferedReader().readText()
                    Log.i("FrameUploadService", "HTTP Response ($responseCode): $responseText")

                    // Кладём ответ в очередь
                    val responseJson = JSONObject().apply {
                        put("response", responseText)
                        put("code", responseCode)
                    }
                    if (responseJson.toString().contains("1")) ResponseQueue.queue.offer(responseJson)

                } catch (e: Exception) {
                    Log.e("FrameUploadService", "Error sending frame", e)
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
