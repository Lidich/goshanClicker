package com.example.goshanclicker.atomic

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

object AppMetrics {

    private val totalRequestTime = AtomicLong()
    private val requestCount = AtomicInteger()

    fun recordRequestTime(timeMillis: Long) {
        totalRequestTime.addAndGet(timeMillis)
        requestCount.incrementAndGet()
    }

    fun getAverageRequestTime(): Double {
        val count = requestCount.get()
        return if (count == 0) 0.0 else totalRequestTime.get().toDouble() / count
    }

    fun reset() {
        totalRequestTime.set(0)
        requestCount.set(0)
    }
}
