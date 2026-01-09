package com.timeblocks.domain.repository

import com.timeblocks.domain.model.User
import com.timeblocks.data.remote.dto.UserDTO
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с пользователем и настройками.
 */
interface UserRepository {

    /**
     * Получить настройки пользователя
     */
    fun getUserSettings(userId: String): Flow<User?>

    /**
     * Получить настройки синхронно (для быстрого доступа)
     */
    suspend fun getUserSettingsSync(userId: String): User?

    /**
     * Сохранить или обновить настройки
     */
    suspend fun saveUserSettings(user: User): Result<User>

    /**
     * Обновить тему
     */
    suspend fun updateTheme(userId: String, theme: String): Result<Unit>

    /**
     * Обновить статус уведомлений
     */
    suspend fun updateNotifications(userId: String, enabled: Boolean): Result<Unit>

    /**
     * Обновить язык
     */
    suspend fun updateLanguage(userId: String, language: String): Result<Unit>

    /**
     * Обновить статус Premium
     */
    suspend fun updatePremiumStatus(userId: String, isPremium: Boolean): Result<Unit>

    /**
     * Обновить время последней синхронизации
     */
    suspend fun updateLastSyncTime(userId: String, timestamp: Long): Result<Unit>

    /**
     * Обновить максимальное количество категорий
     */
    suspend fun updateMaxCategories(userId: String, maxCategories: Int): Result<Unit>

    /**
     * Проверить, существует ли пользователь
     */
    suspend fun exists(userId: String): Boolean

    /**
     * Удалить настройки пользователя
     */
    suspend fun deleteUserSettings(userId: String): Result<Unit>

    /**
     * Синхронизация с удаленным хранилищем
     */
    suspend fun syncWithRemote(): Result<Unit>

    /**
     * Инициализация настроек пользователя
     */
    suspend fun initializeUserSettings(userId: String): Result<Unit>

    /**
     * Проверить, является ли пользователь Premium
     */
    suspend fun isPremium(userId: String): Boolean

    /**
     * Получить ID текущего пользователя
     */
    fun getCurrentUserId(): String?

    /**
     * Проверить, авторизован ли пользователь
     */
    fun isUserLoggedIn(): Boolean

    /**
     * Получить Flow состояния авторизации
     */
    fun getAuthState(): Flow<Boolean>

    /**
     * Вход через Email/Password
     */
    suspend fun signInWithEmail(email: String, password: String): Result<String>

    /**
     * Регистрация через Email/Password
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<String>

    /**
     * Вход через Google
     */
    suspend fun signInWithGoogle(idToken: String): Result<String>

    /**
     * Выход из аккаунта
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Удаление аккаунта
     */
    suspend fun deleteAccount(): Result<Unit>

    /**
     * Получить данные текущего пользователя
     */
    fun getCurrentUser(): UserDTO?
}