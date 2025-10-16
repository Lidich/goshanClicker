package com.example.goshanclicker

import android.app.*
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainService : Service() {

    private var captureManager: ScreenCaptureManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: return START_NOT_STICKY
        val data = intent.getParcelableExtra<Intent>("data") ?: return START_NOT_STICKY

        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = mgr.getMediaProjection(resultCode, data)

        captureManager = ScreenCaptureManager(projection!!)
        captureManager?.start()

        // Однократный тестовый Action
        val intentAction = Intent(this, MyAccessibilityService::class.java).apply {
            putExtra("x", 411f)
            putExtra("y", 778f)
            putExtra("duration", 100)
        }
        startService(intentAction)

        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val chanId = "goshan_clicker"
        val channel = NotificationChannel(chanId, "GoshanClicker", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        return Notification.Builder(this, chanId)
            .setContentTitle("GoshanClicker работает")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager?.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
}
