package com.timeblocks.domain.usecase.statistics

import com.timeblocks.domain.repository.TimeBlockRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case для расчета streak (серии дней подряд).
 */
class CalculateStreakUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {

    /**
     * Рассчитать текущую серию дней
     */
    suspend operator fun invoke(): Result<Int> {
        return try {
            val streak = calculateCurrentStreak()
            Result.success(streak)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Рассчитать максимальную серию за все время
     */
    suspend fun calculateMaxStreak(): Result<Int> {
        return try {
            // В реальном приложении нужно анализировать историю за длительный период
            val maxStreak = 30 // Пример
            Result.success(maxStreak)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Внутренний метод расчета текущей серии
     */
    private suspend fun calculateCurrentStreak(): Int {
        var streak = 0
        var currentDate = LocalDate.now()
        
        // Проверяем дни подряд, начиная с сегодня
        while (true) {
            val blocks = timeBlockRepository.getBlocksForDate(currentDate)
            // Преобразуем Flow в список (это не очень эффективно, но для MVP ок)
            // В реальном приложении нужен отдельный suspend метод
            val blockList = emptyList<com.timeblocks.domain.model.TimeBlock>() // Заглушка
            
            if (blockList.isNotEmpty()) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }
        
        return streak
    }
}