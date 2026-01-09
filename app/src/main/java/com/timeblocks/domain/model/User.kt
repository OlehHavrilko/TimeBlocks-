package com.timeblocks.domain.model

/**
 * Доменная модель пользователя и настроек.
 */
data class User(
    val userId: String,
    val theme: String = "system", // "light", "dark", "system"
    val notificationsEnabled: Boolean = true,
    val language: String = "ru",
    val isPremium: Boolean = false,
    val lastSyncTime: Long? = null,
    val maxCategories: Int = 3 // Ограничение для free tier
) {
    /**
     * Проверить, можно ли создать новую категорию
     */
    fun canCreateCategory(currentCount: Int): Boolean {
        return currentCount < maxCategories
    }
    
    /**
     * Проверить, доступна ли Premium функция
     */
    fun isPremiumFeatureAvailable(): Boolean {
        return isPremium
    }
    
    /**
     * Получить лимит категорий для отображения
     */
    fun getCategoryLimitText(): String {
        return if (isPremium) "∞" else maxCategories.toString()
    }
}