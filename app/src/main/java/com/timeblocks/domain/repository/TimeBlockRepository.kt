package com.timeblocks.domain.repository

import com.timeblocks.domain.model.TimeBlock
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

/**
 * Интерфейс репозитория для работы с блоками времени.
 * Определяет контракт для Data Layer.
 */
interface TimeBlockRepository {

    /**
     * Получить блоки для конкретной даты
     */
    fun getBlocksForDate(date: LocalDate): Flow<List<TimeBlock>>

    /**
     * Получить блоки в диапазоне дат
     */
    fun getBlocksInDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<TimeBlock>>

    /**
     * Создать блок времени
     */
    suspend fun createBlock(block: TimeBlock): Result<TimeBlock>

    /**
     * Обновить блок времени
     */
    suspend fun updateBlock(block: TimeBlock): Result<TimeBlock>

    /**
     * Удалить блок времени
     */
    suspend fun deleteBlock(id: String, date: LocalDate): Result<Unit>

    /**
     * Получить активные блоки (которые должны идти сейчас)
     */
    suspend fun getActiveBlocks(
        date: LocalDate,
        currentTime: LocalTime
    ): List<TimeBlock>

    /**
     * Получить завершенные блоки для даты
     */
    fun getCompletedBlocksForDate(date: LocalDate): Flow<List<TimeBlock>>

    /**
     * Синхронизация с удаленным хранилищем
     */
    suspend fun syncWithRemote(date: LocalDate): Result<Unit>

    /**
     * Получить статистику по категориям за период
     */
    suspend fun getCategoryStats(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<String, Pair<Int, Double>>

    /**
     * Получить количество выполненных блоков за период
     */
    suspend fun getCompletedCount(
        startDate: LocalDate,
        endDate: LocalDate
    ): Int

    /**
     * Получить общее количество часов за период
     */
    suspend fun getTotalHours(
        startDate: LocalDate,
        endDate: LocalDate
    ): Double
}