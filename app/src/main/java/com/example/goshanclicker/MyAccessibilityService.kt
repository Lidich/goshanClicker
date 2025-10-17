package com.example.goshanclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    private var executor: ActionExecutor? = null

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        }

        Log.i("AccessibilityService", "Service connected and configured")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Можно реагировать на события, если нужно
    }

    override fun onInterrupt() {}

    fun performClick(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        executor = ActionExecutor()
        intent?.let {
            val x = it.getFloatExtra("x", -1f)
            val y = it.getFloatExtra("y", -1f)
            val duration = it.getIntExtra("duration", 100)
            if (x >= 0 && y >= 0) {
                if (executor?.perform(x, y, duration) == true) {
                    Log.i("MyAccessibilityService", "got respone do clicking")
                    performClickRepeated(x, y)
                } else {
                    Log.i("MyAccessibilityService", "havent got response sosal")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun performClickRepeated(x: Float,
                             y: Float,
                             repeat: Int = 10,
                             delayMillis: Long = 200L,
                             startDelay: Long = 10000L) {
        Thread {
            Thread.sleep(startDelay)
            for (i in 1..repeat) {
                if (executor?.status == true) {
                    performClick(x, y)
                    Log.i("ActionExecutor", "Клик #$i выполнен на [$x, $y]")
                } else {
                    Log.i("ActionExecutor", "Клик #$i не выполнен на [$x, $y]")
                }
                Thread.sleep(delayMillis)
            }
        }.start()
    }
}
