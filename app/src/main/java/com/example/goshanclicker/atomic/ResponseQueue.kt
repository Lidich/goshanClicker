package com.example.goshanclicker.atomic


import com.example.goshanclicker.model.InputResponse
import java.util.concurrent.ArrayBlockingQueue

object ResponseQueue {
    // Очередь с ёмкостью 1
    private val queue = ArrayBlockingQueue<InputResponse>(1)

    // Блокируется, если в очереди уже есть элемент
    fun set(response: InputResponse) {
        queue.put(response)
    }

    // Блокируется, если очередь пуста, пока не появится элемент
    fun get(): InputResponse {
        return queue.take()
    }

    fun clear() {
        queue.clear()
    }
}