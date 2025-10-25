package com.example.goshanclicker.atomic

import java.util.concurrent.atomic.AtomicLong

class ClickTimeStorage {
    private val lastClickTime = AtomicLong(0L)

    // Установить время последнего клика
    fun setLastClickTime(timeMillis: Long) {
        lastClickTime.set(timeMillis)
    }

    // Получить время последнего клика
    fun getLastClickTime(): Long {
        return lastClickTime.get()
    }

    // Проверить, прошло ли нужное время (например, cooldown)
    fun isCooldownOver(cooldownMillis: Long): Boolean {
        return System.currentTimeMillis() - lastClickTime.get() >= cooldownMillis
    }

    // Метод: количество миллисекунд с последнего клика
    fun millisSinceLastClick(): Long {
        val lastClickTemp = lastClickTime.get()
        if (lastClickTemp == 0L) return 50L
        return System.currentTimeMillis() - lastClickTime.get()
    }
}
