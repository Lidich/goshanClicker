package com.example.goshanclicker

import org.json.JSONObject
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object ResponseQueue {
    val queue: BlockingQueue<JSONObject> = LinkedBlockingQueue(20) // максимум 20 ответов
}
