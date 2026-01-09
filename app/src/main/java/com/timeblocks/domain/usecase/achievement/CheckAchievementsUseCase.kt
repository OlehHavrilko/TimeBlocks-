package com.timeblocks.domain.usecase.achievement

import com.timeblocks.domain.model.AchievementType
import com.timeblocks.domain.repository.AchievementRepository
import com.timeblocks.domain.repository.TimeBlockRepository
import com.timeblocks.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case для проверки и обновления достижений.
 * Анализирует активность пользователя и разблокирует достижения.
 */
class CheckAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val timeBlockRepository: TimeBlockRepository,
    private val userRepository: UserRepository
) {

    /**
     * Проверить все достижения для пользователя
     */
    suspend operator fun invoke(userId: String): Result<Unit> {
        return try {
            // Проверить streak (серия дней)
            checkStreakAchievements(userId)
            
            // Проверить общее количество часов
            checkTotalHoursAchievements(userId)
            
            // Проверить достижения по категориям
            checkCategoryMasteryAchievements(userId)
            
            // Проверить идеальную неделю
            checkPerfectWeekAchievements(userId)
            
            // Проверить раннюю пташку/сову
            checkTimeBasedAchievements(userId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Проверка достижений за серию дней
     */
    private suspend fun checkStreakAchievements(userId: String) {
        // В реальном приложении нужно получать данные о streak из БД
        // Пока заглушка
        val currentStreak = 3 // Пример
        
        if (currentStreak >= 3) {
            achievementRepository.unlockAchievement("streak_3")
        }
        if (currentStreak >= 7) {
            achievementRepository.unlockAchievement("streak_7")
        }
        if (currentStreak >= 30) {
            achievementRepository.unlockAchievement("streak_30")
        }
        if (currentStreak >= 100) {
            achievementRepository.unlockAchievement("streak_100")
        }
    }

    /**
     * Проверка достижений за общее количество часов
     */
    private suspend fun checkTotalHoursAchievements(userId: String) {
        val totalHours = timeBlockRepository.getTotalHours(
            LocalDate.now().minusYears(1),
            LocalDate.now()
        )
        
        if (totalHours >= 50) {
            achievementRepository.unlockAchievement("hours_50")
        }
        if (totalHours >= 200) {
            achievementRepository.unlockAchievement("hours_200")
        }
        if (totalHours >= 1000) {
            achievementRepository.unlockAchievement("hours_1000")
        }
    }

    /**
     * Проверка достижений за мастерство в категориях
     */
    private suspend fun checkCategoryMasteryAchievements(userId: String) {
        val categoryStats = timeBlockRepository.getCategoryStats(
            LocalDate.now().minusMonths(1),
            LocalDate.now()
        )
        
        // Проверить, есть ли категория с 10+ часами
        val hasMastery = categoryStats.values.any { (_, totalMinutes) ->
            totalMinutes >= 600 // 10 часов
        }
        
        if (hasMastery) {
            achievementRepository.unlockAchievement("category_10")
        }
    }

    /**
     * Проверка достижения "Идеальная неделя"
     */
    private suspend fun checkPerfectWeekAchievements(userId: String) {
        // Проверить выполнение всех запланированных блоков за неделю
        val lastWeek = LocalDate.now().minusWeeks(1)
        val thisWeek = LocalDate.now()
        
        val weekBlocks = timeBlockRepository.getBlocksInDateRange(lastWeek, thisWeek)
        val completedBlocks = weekBlocks.first().filter { it.isCompleted }
        
        // Если выполнено 90%+ блоков за неделю
        if (completedBlocks.size >= weekBlocks.first().size * 0.9) {
            achievementRepository.unlockAchievement("perfect_week")
        }
    }

    /**
     * Проверка достижений по времени (ранняя пташка/сова)
     */
    private suspend fun checkTimeBasedAchievements(userId: String) {
        val today = LocalDate.now()
        val activeBlocks = timeBlockRepository.getActiveBlocks(today, java.time.LocalTime.now())
        
        // Проверить, есть ли блоки до 8:00
        val hasEarlyBlock = activeBlocks.any { 
            it.startTime.isBefore(java.time.LocalTime.of(8, 0)) 
        }
        
        // Проверить, есть ли блоки после 22:00
        val hasLateBlock = activeBlocks.any { 
            it.startTime.isAfter(java.time.LocalTime.of(22, 0)) 
        }
        
        if (hasEarlyBlock) {
            achievementRepository.unlockAchievement("early_bird")
        }
        
        if (hasLateBlock) {
            achievementRepository.unlockAchievement("night_owl")
        }
    }
}