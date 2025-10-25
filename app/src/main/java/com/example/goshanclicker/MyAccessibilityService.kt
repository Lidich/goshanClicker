package com.example.goshanclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.goshanclicker.atomic.ResponseQueue

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
                    val response = ResponseQueue.get()

                    //if (x >= 0 && y >= 0) {
                    val x = 549F
                    val y = 930F
                    if (response != null) {
                        Log.i("AccessibilityService", "Taken response do click if needed $response")
                        for (i in 0 until response.status.toInt()) {
                            performClick(x, y, 5, 50)
                            Log.i("AccessibilityService", "Click performed at [$x, $y] with COUNT $i response $response")
                        }
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

    private fun performClick(x: Float, y: Float, duration: Int = 100, delayMs: Long = 0L) {
        if (delayMs != 0L) Thread.sleep(delayMs)
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration.toLong()))
            .build()
        dispatchGesture(gesture, null, null)
    }
}
