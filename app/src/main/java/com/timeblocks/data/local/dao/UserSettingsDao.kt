package com.timeblocks.data.local.dao

import androidx.room.*
import com.timeblocks.data.local.entities.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с настройками пользователя в Room Database.
 */
@Dao
interface UserSettingsDao {

    /**
     * Получить настройки пользователя по ID
     */
    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    fun getUserSettings(userId: String): Flow<UserSettingsEntity?>

    /**
     * Получить настройки синхронизации (без Flow для быстрого доступа)
     */
    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    suspend fun getUserSettingsSync(userId: String): UserSettingsEntity?

    /**
     * Вставить или обновить настройки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettingsEntity)

    /**
     * Обновить тему
     */
    @Query("UPDATE user_settings SET theme = :theme WHERE userId = :userId")
    suspend fun updateTheme(userId: String, theme: String)

    /**
     * Обновить статус уведомлений
     */
    @Query("UPDATE user_settings SET notificationsEnabled = :enabled WHERE userId = :userId")
    suspend fun updateNotifications(userId: String, enabled: Boolean)

    /**
     * Обновить язык
     */
    @Query("UPDATE user_settings SET language = :language WHERE userId = :userId")
    suspend fun updateLanguage(userId: String, language: String)

    /**
     * Обновить статус Premium
     */
    @Query("UPDATE user_settings SET isPremium = :isPremium WHERE userId = :userId")
    suspend fun updatePremiumStatus(userId: String, isPremium: Boolean)

    /**
     * Обновить время последней синхронизации
     */
    @Query("UPDATE user_settings SET lastSyncTime = :timestamp WHERE userId = :userId")
    suspend fun updateLastSyncTime(userId: String, timestamp: Long)

    /**
     * Обновить максимальное количество категорий
     */
    @Query("UPDATE user_settings SET maxCategories = :maxCategories WHERE userId = :userId")
    suspend fun updateMaxCategories(userId: String, maxCategories: Int)

    /**
     * Проверить, существует ли запись настроек
     */
    @Query("SELECT EXISTS(SELECT 1 FROM user_settings WHERE userId = :userId)")
    suspend fun exists(userId: String): Boolean

    /**
     * Удалить настройки пользователя
     */
    @Query("DELETE FROM user_settings WHERE userId = :userId")
    suspend fun deleteUserSettings(userId: String)
}