package com.timeblocks.domain.usecase.statistics

import com.timeblocks.domain.model.TimeBlock
import com.timeblocks.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case для получения статистики.
 */
class GetStatisticsUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {

    /**
     * Получить статистику за период
     */
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<StatisticsData> {
        return try {
            val totalHours = timeBlockRepository.getTotalHours(startDate, endDate)
            val completedCount = timeBlockRepository.getCompletedCount(startDate, endDate)
            val categoryStats = timeBlockRepository.getCategoryStats(startDate, endDate)
            
            val data = StatisticsData(
                totalHours = totalHours,
                completedBlocks = completedCount,
                categoryStats = categoryStats
            )
            
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Получить статистику за неделю
     */
    suspend fun getWeeklyStats(): Result<StatisticsData> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusWeeks(1)
        return invoke(startDate, endDate)
    }

    /**
     * Получить статистику за месяц
     */
    suspend fun getMonthlyStats(): Result<StatisticsData> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusMonths(1)
        return invoke(startDate, endDate)
    }

    /**
     * Получить статистику за год
     */
    suspend fun getYearlyStats(): Result<StatisticsData> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusYears(1)
        return invoke(startDate, endDate)
    }

    /**
     * Получить поток статистики за сегодня
     */
    fun getTodayStatsFlow(): Flow<TodayStats> {
        val today = LocalDate.now()
        return timeBlockRepository.getBlocksForDate(today).map { blocks ->
            val totalBlocks = blocks.size
            val completedBlocks = blocks.filter { it.isCompleted }.size
            val totalMinutes = blocks.sumOf { 
                it.actualDuration?.toMinutes() ?: 0 
            }
            
            TodayStats(
                totalBlocks = totalBlocks,
                completedBlocks = completedBlocks,
                totalMinutes = totalMinutes,
                completionRate = if (totalBlocks > 0) (completedBlocks.toDouble() / totalBlocks * 100).toInt() else 0
            )
        }
    }
}

/**
 * Данные статистики за период
 */
data class StatisticsData(
    val totalHours: Double,
    val completedBlocks: Int,
    val categoryStats: Map<String, Pair<Int, Double>> // categoryId to (count, totalMinutes)
)

/**
 * Статистика за сегодня
 */
data class TodayStats(
    val totalBlocks: Int,
    val completedBlocks: Int,
    val totalMinutes: Long,
    val completionRate: Int
)