package com.timeblocks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность настроек пользователя для локального хранения в Room Database.
 * Хранит пользовательские предпочтения и настройки приложения.
 */
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val userId: String,
    val theme: String = "system", // "light", "dark", "system"
    val notificationsEnabled: Boolean = true,
    val language: String = "ru",
    val isPremium: Boolean = false,
    val lastSyncTime: Long? = null,
    val maxCategories: Int = 3 // Ограничение для free tier
)