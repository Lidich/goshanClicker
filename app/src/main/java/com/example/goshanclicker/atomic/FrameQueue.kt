package com.example.goshanclicker.atomic

import com.example.goshanclicker.model.InputRequest
import java.util.concurrent.ArrayBlockingQueue

object FrameQueue {
    private val queue = ArrayBlockingQueue<InputRequest>(1)

    fun set(request: InputRequest) {
        queue.put(request)
    }

    fun get(): InputRequest {
        return queue.take()
    }

    fun clear() {
        queue.clear()
    }
}