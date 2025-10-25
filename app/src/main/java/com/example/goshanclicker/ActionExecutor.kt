package com.example.goshanclicker

import android.util.Log
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ActionExecutor {

    // TODO remove class
    public var status = false

    companion object {
        const val EMULATOR_URL = "http://10.0.2.16:5300/should-click"
        const val MOBILE_URL = "http://127.0.0.1:5300/should-click"
    }

    fun perform(x: Float, y: Float, duration: Int): Boolean {
        Thread {
            try {
                val url = URL(MOBILE_URL)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val json = JSONObject().apply {
                    put("x", x)
                    put("y", y)
                    put("duration", duration)
                }

                OutputStreamWriter(connection.outputStream).use {
                    it.write(json.toString())
                }

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().readText()

                if (response.contains("0")) status = true

                Log.i("ActionExecutor", "HTTP Response ($responseCode): $response")

            } catch (e: Exception) {
                Log.e("ActionExecutor", "Ошибка HTTP запроса: ${e.message}", e)
            }
        }.start()

        return true
    }
}

