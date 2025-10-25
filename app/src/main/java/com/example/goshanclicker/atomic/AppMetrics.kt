package com.example.goshanclicker.atomic

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

object AppMetrics {

    private val totalRequestTime = AtomicLong()
    private val requestCount = AtomicInteger()
    private val lastRequestTimeMs = AtomicLong()

    fun recordRequestTime(timeMillis: Long) {
        totalRequestTime.addAndGet(timeMillis)
        requestCount.incrementAndGet()
        lastRequestTimeMs.set(timeMillis)
    }

    fun getAverageRequestTime(): Double {
        val count = requestCount.get()
        return if (count == 0) 0.0 else totalRequestTime.get().toDouble() / count
    }

    fun getLastRequestTime(): Long = lastRequestTimeMs.get()

    fun reset() {
        totalRequestTime.set(0)
        requestCount.set(0)
        lastRequestTimeMs.set(0)
    }
}
