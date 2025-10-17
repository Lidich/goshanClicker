package com.example.goshanclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import org.json.JSONObject

class MyAccessibilityService : AccessibilityService() {

    private var running = true

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        }

        Log.i("AccessibilityService", "Service connected and configured")

        // Запускаем поток обработки очереди
        Thread {
            while (running) {
                try {
                    // Берём ответ из очереди (ждём, если пусто)
                    val response: JSONObject = ResponseQueue.queue.take()

                    // Пример: сервер возвращает координаты x, y и duration
                    //val x = response.optDouble("x", -1.0).toFloat()
                    //val y = response.optDouble("y", -1.0).toFloat()
                    val duration = response.optInt("duration", 100)

                    //if (x >= 0 && y >= 0) {
                    val x = 549F
                    val y = 930F
                    if (response != null) {
                        Log.i("AccessibilityService", "Taken response do click $response")
                        performClick(x, y, duration)
                        Log.i("AccessibilityService", "Click performed at [$x, $y] with duration $duration")
                    }

                } catch (e: Exception) {
                    Log.e("AccessibilityService", "Error processing response", e)
                }
            }
        }.start()
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // Реагировать на события не нужно
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }

    private fun performClick(x: Float, y: Float, duration: Int = 100) {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration.toLong()))
            .build()
        dispatchGesture(gesture, null, null)
    }
}
