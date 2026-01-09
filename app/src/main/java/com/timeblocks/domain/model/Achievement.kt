package com.timeblocks.domain.model

import java.util.UUID

/**
 * Доменная модель достижения.
 * Описывает систему геймификации и мотивации.
 */
data class Achievement(
    val id: String = UUID.randomUUID().toString(),
    val type: AchievementType,
    val title: String,
    val description: String,
    val iconRes: Int, // ID ресурса изображения
    val rarity: Rarity,
    val isUnlocked: Boolean = false,
    val progress: Int = 0, // 0-100
    val maxProgress: Int = 100,
    val unlockedAt: Long? = null
) {
    /**
     * Проверить, полностью ли разблокировано
     */
    fun isComplete(): Boolean {
        return isUnlocked && progress >= maxProgress
    }
    
    /**
     * Получить прогресс в процентах
     */
    fun getProgressPercent(): Int {
        return ((progress.toDouble() / maxProgress) * 100).toInt().coerceIn(0, 100)
    }
    
    /**
     * Проверить, можно ли разблокировать
     */
    fun canUnlock(currentProgress: Int): Boolean {
        return currentProgress >= maxProgress && !isUnlocked
    }
}

/**
 * Типы достижений
 */
enum class AchievementType {
    STREAK,           // Серия дней подряд
    TOTAL_HOURS,      // Общее количество часов
    CATEGORY_MASTERY, // Мастерство в категории
    PERFECT_WEEK,     // Идеальная неделя
    EARLY_BIRD,       // Ранняя птичка (начало до 8:00)
    NIGHT_OWL         // Сова (работа после 22:00)
}

/**
 * Редкость достижений
 */
enum class Rarity(val colorHex: String) {
    COMMON("#808080"),    // Серый
    RARE("#4169E1"),      // Королевский синий
    EPIC("#9370DB"),      // Средне-фиолетовый
    LEGENDARY("#FFD700")  // Золотой
}