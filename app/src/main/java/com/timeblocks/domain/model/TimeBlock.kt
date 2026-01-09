package com.timeblocks.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.util.UUID

/**
 * Доменная модель блока времени.
 * Содержит бизнес-логику и не зависит от Android фреймворка.
 */
data class TimeBlock(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val categoryId: String,
    val date: LocalDate,
    val isCompleted: Boolean = false,
    val actualStartTime: LocalTime? = null,
    val actualEndTime: LocalTime? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Расчетная длительность блока (плановая)
     */
    val duration: Duration
        get() = Duration.between(startTime, endTime)
    
    /**
     * Расчетная фактическая длительность
     */
    val actualDuration: Duration?
        get() = if (actualStartTime != null && actualEndTime != null) {
            Duration.between(actualStartTime, actualEndTime)
        } else null
    
    /**
     * Проверка, активен ли блок в указанное время
     */
    fun isActiveAt(time: LocalTime): Boolean {
        return time >= startTime && time <= endTime
    }
    
    /**
     * Проверка, завершен ли блок
     */
    fun isFinished(): Boolean {
        return isCompleted && actualEndTime != null
    }
    
    /**
     * Получить процент выполнения (0-100)
     */
    fun getCompletionPercent(): Int {
        if (!isCompleted) return 0
        val actual = actualDuration ?: return 0
        val planned = duration.toMinutes().toDouble()
        val actualMinutes = actual.toMinutes().toDouble()
        return ((actualMinutes / planned) * 100).toInt().coerceIn(0, 100)
    }
}