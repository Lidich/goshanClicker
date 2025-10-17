package com.example.goshanclicker

import android.app.*
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainService : Service() {

    private var captureManager: ScreenCaptureManager? = null

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Проверка данных MediaProjection
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED)
        val data = intent?.getParcelableExtra<Intent>("data")
        if (resultCode == null || data == null) {
            Log.e("MainService", "No MediaProjection data, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(1, buildNotification())

        // Запуск MediaProjection
        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = mgr.getMediaProjection(resultCode, data)
        captureManager = ScreenCaptureManager(projection!!)
        captureManager?.start()

        // Запуск сервиса отправки кадров на сервер
        val uploadIntent = Intent(this, FrameUploadService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(uploadIntent)
        } else {
            startService(uploadIntent)
        }

        // AccessibilityService — Android активирует автоматически
        Log.i("MainService", "All services started")
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
        Log.i("MainService", "MainService stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
