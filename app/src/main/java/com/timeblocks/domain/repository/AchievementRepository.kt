package com.timeblocks.domain.repository

import com.timeblocks.domain.model.Achievement
import com.timeblocks.domain.model.AchievementType
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с достижениями.
 */
interface AchievementRepository {

    /**
     * Получить все достижения
     */
    fun getAllAchievements(): Flow<List<Achievement>>

    /**
     * Получить разблокированные достижения
     */
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    /**
     * Получить достижение по ID
     */
    suspend fun getAchievementById(id: String): Achievement?

    /**
     * Получить достижение по типу
     */
    suspend fun getAchievementByType(type: AchievementType): Achievement?

    /**
     * Создать или обновить достижение
     */
    suspend fun saveAchievement(achievement: Achievement): Result<Achievement>

    /**
     * Разблокировать достижение
     */
    suspend fun unlockAchievement(id: String): Result<Unit>

    /**
     * Обновить прогресс достижения
     */
    suspend fun updateProgress(id: String, progress: Int): Result<Unit>

    /**
     * Получить количество разблокированных достижений
     */
    suspend fun getUnlockedCount(): Int

    /**
     * Проверить, разблокировано ли достижение
     */
    suspend fun isAchievementUnlocked(id: String): Boolean

    /**
     * Синхронизация с удаленным хранилищем
     */
    suspend fun syncWithRemote(): Result<Unit>

    /**
     * Инициализация достижений
     */
    suspend fun initializeAchievements(): Result<Unit>
}