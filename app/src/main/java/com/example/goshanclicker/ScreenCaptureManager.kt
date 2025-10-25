package com.example.goshanclicker

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.example.goshanclicker.atomic.FrameQueue
import com.example.goshanclicker.model.InputRequest

class ScreenCaptureManager(private val projection: MediaProjection) {

    private var reader: ImageReader? = null
    private var display: VirtualDisplay? = null
    private var thread: HandlerThread? = null
    private var handler: Handler? = null

    fun start() {
        Thread.sleep(10000)

        // Регистрация колбэка для MediaProjection
        projection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Log.i("ScreenCapture", "MediaProjection stopped")
                stop()
            }
        }, Handler(Looper.getMainLooper()))

        // Создаём отдельный поток для чтения кадров
        thread = HandlerThread("ScreenCaptureThread").apply { start() }
        handler = Handler(thread!!.looper)

        val width = 720
        val height = 1280
        val density = Resources.getSystem().displayMetrics.densityDpi

        reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        display = projection.createVirtualDisplay(
            "Capture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            reader!!.surface,
            null,
            handler
        )

        // Постинг рендер-цикла
        handler?.post(object : Runnable {
            override fun run() {

                val image = reader?.acquireLatestImage()
                if (image != null) {
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * width

                    // создаём полный bitmap
                    val fullBitmap = Bitmap.createBitmap(
                        width + rowPadding / pixelStride, height,
                        Bitmap.Config.ARGB_8888
                    )
                    fullBitmap.copyPixelsFromBuffer(buffer)
                    image.close()

                    // Обрезка сверху и снизу
                    val topCrop = 250     // пикселей сверху
                    val bottomCrop = 1000  // пикселей снизу
                    val croppedHeight = fullBitmap.height - topCrop - bottomCrop
                    val bitmap = Bitmap.createBitmap(
                        fullBitmap,
                        80, 150,
                        550,
                        550
                    )

                    val request = InputRequest(
                        image = bitmap.toBase64(),
                        msSinceClick = 1,
                        timestamp = System.currentTimeMillis()
                    )

                    Log.i("ScreenCaptureManager", "Кладём кадр ${bitmap.toBase64()}")
                    FrameQueue.set(request)
                }
                handler?.postDelayed(this, 200) // ~10 FPS
            }
        })
    }

    fun stop() {
        display?.release()
        display = null
        reader?.close()
        reader = null
        projection.stop()
        thread?.quitSafely()
        thread = null
        handler = null
    }
}
