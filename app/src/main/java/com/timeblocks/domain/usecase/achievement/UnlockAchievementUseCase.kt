package com.timeblocks.domain.usecase.achievement

import com.timeblocks.domain.repository.AchievementRepository
import javax.inject.Inject

/**
 * Use case для разблокировки достижения.
 */
class UnlockAchievementUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository
) {

    /**
     * Разблокировать достижение по ID
     */
    suspend operator fun invoke(achievementId: String): Result<Unit> {
        if (achievementId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID достижения не может быть пустым"))
        }

        return achievementRepository.unlockAchievement(achievementId)
    }

    /**
     * Обновить прогресс достижения
     */
    suspend fun updateProgress(achievementId: String, progress: Int): Result<Unit> {
        if (achievementId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID достижения не может быть пустым"))
        }

        if (progress < 0) {
            return Result.failure(IllegalArgumentException("Прогресс не может быть отрицательным"))
        }

        return achievementRepository.updateProgress(achievementId, progress)
    }
}