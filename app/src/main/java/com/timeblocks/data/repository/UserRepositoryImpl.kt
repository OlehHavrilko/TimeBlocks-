package com.timeblocks.data.repository

import com.timeblocks.data.local.dao.UserSettingsDao
import com.timeblocks.data.local.entities.UserSettingsEntity
import com.timeblocks.data.remote.dto.UserDTO
import com.timeblocks.data.remote.firebase.FirebaseAuthManager
import com.timeblocks.data.remote.firebase.FirestoreManager
import com.timeblocks.domain.model.User
import com.timeblocks.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Реализация репозитория для работы с пользователем и настройками.
 */
class UserRepositoryImpl @Inject constructor(
    private val userSettingsDao: UserSettingsDao,
    private val authManager: FirebaseAuthManager,
    private val firestoreManager: FirestoreManager
) : UserRepository {

    /**
     * Получить настройки пользователя
     */
    override fun getUserSettings(userId: String): Flow<User?> {
        return userSettingsDao.getUserSettings(userId)
            .map { entity -> entity?.toDomainModel() }
    }

    /**
     * Получить настройки синхронно (для быстрого доступа)
     */
    override suspend fun getUserSettingsSync(userId: String): User? {
        return try {
            userSettingsDao.getUserSettingsSync(userId)?.toDomainModel()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get user settings sync")
            null
        }
    }

    /**
     * Сохранить или обновить настройки
     */
    override suspend fun saveUserSettings(user: User): Result<User> {
        return try {
            val entity = user.toEntity()
            userSettingsDao.insertUserSettings(entity)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Saved user settings: ${user.userId}")
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save user settings")
            Result.failure(e)
        }
    }

    /**
     * Обновить тему
     */
    override suspend fun updateTheme(userId: String, theme: String): Result<Unit> {
        return try {
            userSettingsDao.updateTheme(userId, theme)
            syncWithRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update theme")
            Result.failure(e)
        }
    }

    /**
     * Обновить статус уведомлений
     */
    override suspend fun updateNotifications(userId: String, enabled: Boolean): Result<Unit> {
        return try {
            userSettingsDao.updateNotifications(userId, enabled)
            syncWithRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update notifications")
            Result.failure(e)
        }
    }

    /**
     * Обновить язык
     */
    override suspend fun updateLanguage(userId: String, language: String): Result<Unit> {
        return try {
            userSettingsDao.updateLanguage(userId, language)
            syncWithRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update language")
            Result.failure(e)
        }
    }

    /**
     * Обновить статус Premium
     */
    override suspend fun updatePremiumStatus(userId: String, isPremium: Boolean): Result<Unit> {
        return try {
            userSettingsDao.updatePremiumStatus(userId, isPremium)
            syncWithRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update premium status")
            Result.failure(e)
        }
    }

    /**
     * Обновить время последней синхронизации
     */
    override suspend fun updateLastSyncTime(userId: String, timestamp: Long): Result<Unit> {
        return try {
            userSettingsDao.updateLastSyncTime(userId, timestamp)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update last sync time")
            Result.failure(e)
        }
    }

    /**
     * Обновить максимальное количество категорий
     */
    override suspend fun updateMaxCategories(userId: String, maxCategories: Int): Result<Unit> {
        return try {
            userSettingsDao.updateMaxCategories(userId, maxCategories)
            syncWithRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update max categories")
            Result.failure(e)
        }
    }

    /**
     * Проверить, существует ли пользователь
     */
    override suspend fun exists(userId: String): Boolean {
        return try {
            userSettingsDao.exists(userId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to check if user exists")
            false
        }
    }

    /**
     * Удалить настройки пользователя
     */
    override suspend fun deleteUserSettings(userId: String): Result<Unit> {
        return try {
            userSettingsDao.deleteUserSettings(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete user settings")
            Result.failure(e)
        }
    }

    /**
     * Синхронизация с удаленным хранилищем
     */
    override suspend fun syncWithRemote(): Result<Unit> {
        return try {
            val userId = authManager.currentUserId
            if (userId != null) {
                val settings = userSettingsDao.getUserSettingsSync(userId)
                if (settings != null) {
                    val userDTO = UserDTO(
                        id = userId,
                        email = settings.userId, // В реальном приложении нужно отдельное поле email
                        isPremium = settings.isPremium,
                        lastLogin = System.currentTimeMillis()
                    )
                    firestoreManager.saveUser(userDTO)
                    Timber.d("User sync completed")
                }
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "User sync failed")
            Result.failure(e)
        }
    }

    /**
     * Инициализация настроек пользователя
     */
    override suspend fun initializeUserSettings(userId: String): Result<Unit> {
        return try {
            val exists = userSettingsDao.exists(userId)
            if (exists) return Result.success(Unit)

            val defaultSettings = UserSettingsEntity(
                userId = userId,
                theme = "system",
                notificationsEnabled = true,
                language = "ru",
                isPremium = false,
                lastSyncTime = null,
                maxCategories = 3
            )

            userSettingsDao.insertUserSettings(defaultSettings)
            Timber.d("User settings initialized for: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize user settings")
            Result.failure(e)
        }
    }

    /**
     * Проверить, является ли пользователь Premium
     */
    override suspend fun isPremium(userId: String): Boolean {
        return try {
            userSettingsDao.getUserSettingsSync(userId)?.isPremium == true
        } catch (e: Exception) {
            Timber.e(e, "Failed to check premium status")
            false
        }
    }

    /**
     * Получить ID текущего пользователя
     */
    override fun getCurrentUserId(): String? {
        return authManager.currentUserId
    }

    /**
     * Проверить, авторизован ли пользователь
     */
    override fun isUserLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }

    /**
     * Получить Flow состояния авторизации
     */
    override fun getAuthState(): Flow<Boolean> {
        return authManager.getAuthState()
    }

    /**
     * Вход через Email/Password
     */
    override suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = authManager.signInWithEmail(email, password)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                initializeUserSettings(userId)
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Sign in failed")
            Result.failure(e)
        }
    }

    /**
     * Регистрация через Email/Password
     */
    override suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = authManager.signUpWithEmail(email, password)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                initializeUserSettings(userId)
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Sign up failed")
            Result.failure(e)
        }
    }

    /**
     * Вход через Google
     */
    override suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val result = authManager.signInWithGoogle(idToken)
            if (result.isSuccess) {
                val userId = result.getOrNull() ?: ""
                initializeUserSettings(userId)
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "Google sign in failed")
            Result.failure(e)
        }
    }

    /**
     * Выход из аккаунта
     */
    override suspend fun signOut(): Result<Unit> {
        return try {
            authManager.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Sign out failed")
            Result.failure(e)
        }
    }

    /**
     * Удаление аккаунта
     */
    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val userId = authManager.currentUserId
            if (userId != null) {
                deleteUserSettings(userId)
            }
            authManager.deleteAccount()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Account deletion failed")
            Result.failure(e)
        }
    }

    /**
     * Получить данные текущего пользователя
     */
    override fun getCurrentUser(): UserDTO? {
        return authManager.getCurrentUser()
    }
}

/**
 * Extension функции для преобразования между Entity и Domain моделями
 */
private fun UserSettingsEntity.toDomainModel(): User {
    return User(
        userId = userId,
        theme = theme,
        notificationsEnabled = notificationsEnabled,
        language = language,
        isPremium = isPremium,
        lastSyncTime = lastSyncTime,
        maxCategories = maxCategories
    )
}

private fun User.toEntity(): UserSettingsEntity {
    return UserSettingsEntity(
        userId = userId,
        theme = theme,
        notificationsEnabled = notificationsEnabled,
        language = language,
        isPremium = isPremium,
        lastSyncTime = lastSyncTime,
        maxCategories = maxCategories
    )
}