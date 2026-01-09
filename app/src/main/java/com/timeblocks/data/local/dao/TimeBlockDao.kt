package com.timeblocks.data.local.dao

import androidx.room.*
import com.timeblocks.data.local.entities.TimeBlockEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с блоками времени в Room Database.
 * Содержит базовые операции CRUD и специфичные запросы.
 */
@Dao
interface TimeBlockDao {

    /**
     * Получить все блоки для конкретной даты
     */
    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY startTime ASC")
    fun getBlocksForDate(date: String): Flow<List<TimeBlockEntity>>

    /**
     * Получить блок по ID
     */
    @Query("SELECT * FROM time_blocks WHERE id = :id LIMIT 1")
    suspend fun getBlockById(id: String): TimeBlockEntity?

    /**
     * Получить активные блоки (которые должны идти сейчас)
     */
    @Query("SELECT * FROM time_blocks WHERE date = :date AND :currentTime BETWEEN startTime AND endTime")
    suspend fun getActiveBlocks(date: String, currentTime: String): List<TimeBlockEntity>

    /**
     * Получить завершенные блоки для даты
     */
    @Query("SELECT * FROM time_blocks WHERE date = :date AND isCompleted = 1 ORDER BY startTime ASC")
    fun getCompletedBlocksForDate(date: String): Flow<List<TimeBlockEntity>>

    /**
     * Вставить блок
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: TimeBlockEntity)

    /**
     * Вставить несколько блоков
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<TimeBlockEntity>)

    /**
     * Обновить блок
     */
    @Update
    suspend fun updateBlock(block: TimeBlockEntity)

    /**
     * Удалить блок по ID
     */
    @Query("DELETE FROM time_blocks WHERE id = :id")
    suspend fun deleteBlock(id: String)

    /**
     * Удалить все блоки для даты
     */
    @Query("DELETE FROM time_blocks WHERE date = :date")
    suspend fun deleteBlocksForDate(date: String)

    /**
     * Получить все блоки в диапазоне дат
     */
    @Query("SELECT * FROM time_blocks WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, startTime ASC")
    fun getBlocksInDateRange(startDate: String, endDate: String): Flow<List<TimeBlockEntity>>

    /**
     * Получить статистику по категориям за период
     */
    @Query("""
        SELECT categoryId, COUNT(*) as count, SUM(
            CASE 
                WHEN actualStartTime IS NOT NULL AND actualEndTime IS NOT NULL 
                THEN (julianday(actualEndTime) - julianday(actualStartTime)) * 24 * 60 
                ELSE 0 
            END
        ) as totalMinutes
        FROM time_blocks 
        WHERE date BETWEEN :startDate AND :endDate 
        GROUP BY categoryId
    """)
    suspend fun getCategoryStats(startDate: String, endDate: String): List<CategoryStats>

    /**
     * Получить количество выполненных блоков за период
     */
    @Query("SELECT COUNT(*) FROM time_blocks WHERE date BETWEEN :startDate AND :endDate AND isCompleted = 1")
    suspend fun getCompletedCount(startDate: String, endDate: String): Int

    /**
     * Получить общее количество часов за период
     */
    @Query("""
        SELECT SUM(
            CASE 
                WHEN actualStartTime IS NOT NULL AND actualEndTime IS NOT NULL 
                THEN (julianday(actualEndTime) - julianday(actualStartTime)) * 24 
                ELSE 0 
            END
        ) 
        FROM time_blocks 
        WHERE date BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalHours(startDate: String, endDate: String): Double?
}

/**
 * Вспомогательный data class для статистики по категориям
 */
data class CategoryStats(
    val categoryId: String,
    val count: Int,
    val totalMinutes: Double?
)