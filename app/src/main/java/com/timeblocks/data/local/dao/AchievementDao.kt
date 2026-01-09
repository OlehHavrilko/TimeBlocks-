package com.timeblocks.data.local.dao

import androidx.room.*
import com.timeblocks.data.local.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с достижениями в Room Database.
 */
@Dao
interface AchievementDao {

    /**
     * Получить все достижения
     */
    @Query("SELECT * FROM achievements ORDER BY rarity DESC, type ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    /**
     * Получить разблокированные достижения
     */
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    /**
     * Получить достижение по ID
     */
    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getAchievementById(id: String): AchievementEntity?

    /**
     * Получить достижение по типу
     */
    @Query("SELECT * FROM achievements WHERE type = :type LIMIT 1")
    suspend fun getAchievementByType(type: String): AchievementEntity?

    /**
     * Вставить достижение
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    /**
     * Вставить несколько достижений
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    /**
     * Обновить достижение
     */
    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    /**
     * Разблокировать достижение
     */
    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockAchievement(id: String, unlockedAt: Long)

    /**
     * Обновить прогресс достижения
     */
    @Query("UPDATE achievements SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int)

    /**
     * Получить количество разблокированных достижений
     */
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedCount(): Int

    /**
     * Проверить, существует ли достижение с таким ID
     */
    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE id = :id)")
    suspend fun existsById(id: String): Boolean
}