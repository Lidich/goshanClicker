package com.example.goshanclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    private var executor: ActionExecutor? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
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
                executor?.perform(x, y, duration)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}
