package com.timeblocks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность достижения для локального хранения в Room Database.
 * Соответствует доменной модели Achievement.
 */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val type: String, // AchievementType.name
    val title: String,
    val description: String,
    val iconRes: Int, // ID ресурса
    val rarity: String, // Rarity.name
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 100,
    val unlockedAt: Long? = null
)