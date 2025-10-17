package com.example.goshanclicker

import android.graphics.Bitmap
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object FrameQueue {
val queue: BlockingQueue<Bitmap> = LinkedBlockingQueue(10) // максимум 10 кадров
}
