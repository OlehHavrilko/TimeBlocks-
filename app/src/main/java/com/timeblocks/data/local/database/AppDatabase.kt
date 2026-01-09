package com.timeblocks.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timeblocks.data.local.dao.TimeBlockDao
import com.timeblocks.data.local.dao.CategoryDao
import com.timeblocks.data.local.dao.AchievementDao
import com.timeblocks.data.local.dao.UserSettingsDao
import com.timeblocks.data.local.entities.TimeBlockEntity
import com.timeblocks.data.local.entities.CategoryEntity
import com.timeblocks.data.local.entities.AchievementEntity
import com.timeblocks.data.local.entities.UserSettingsEntity

/**
 * Основная база данных приложения TimeBlocks.
 * Содержит таблицы для:
 * - TimeBlockEntity (блоки времени)
 * - CategoryEntity (категории)
 * - AchievementEntity (достижения)
 * - UserSettingsEntity (настройки пользователя)
 */
@Database(
    entities = [
        TimeBlockEntity::class,
        CategoryEntity::class,
        AchievementEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun categoryDao(): CategoryDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userSettingsDao(): UserSettingsDao
}