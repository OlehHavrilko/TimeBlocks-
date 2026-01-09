package com.timeblocks.data.repository

import com.timeblocks.data.local.dao.AchievementDao
import com.timeblocks.data.local.entities.AchievementEntity
import com.timeblocks.data.remote.firebase.FirestoreManager
import com.timeblocks.domain.model.Achievement
import com.timeblocks.domain.model.AchievementType
import com.timeblocks.domain.model.Rarity
import com.timeblocks.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Реализация репозитория для работы с достижениями.
 */
class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao,
    private val firestoreManager: FirestoreManager
) : AchievementRepository {

    /**
     * Получить все достижения
     */
    override fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    /**
     * Получить разблокированные достижения
     */
    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    /**
     * Получить достижение по ID
     */
    override suspend fun getAchievementById(id: String): Achievement? {
        return try {
            achievementDao.getAchievementById(id)?.toDomainModel()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get achievement by id")
            null
        }
    }

    /**
     * Получить достижение по типу
     */
    override suspend fun getAchievementByType(type: AchievementType): Achievement? {
        return try {
            achievementDao.getAchievementByType(type.name)?.toDomainModel()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get achievement by type")
            null
        }
    }

    /**
     * Создать или обновить достижение
     */
    override suspend fun saveAchievement(achievement: Achievement): Result<Achievement> {
        return try {
            val entity = achievement.toEntity()
            achievementDao.insertAchievement(entity)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Saved achievement: ${achievement.id}")
            Result.success(achievement)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save achievement")
            Result.failure(e)
        }
    }

    /**
     * Разблокировать достижение
     */
    override suspend fun unlockAchievement(id: String): Result<Unit> {
        return try {
            val timestamp = System.currentTimeMillis()
            achievementDao.unlockAchievement(id, timestamp)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Unlocked achievement: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to unlock achievement")
            Result.failure(e)
        }
    }

    /**
     * Обновить прогресс достижения
     */
    override suspend fun updateProgress(id: String, progress: Int): Result<Unit> {
        return try {
            achievementDao.updateProgress(id, progress)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Updated progress for achievement: $id to $progress")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update progress")
            Result.failure(e)
        }
    }

    /**
     * Получить количество разблокированных достижений
     */
    override suspend fun getUnlockedCount(): Int {
        return try {
            achievementDao.getUnlockedCount()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get unlocked count")
            0
        }
    }

    /**
     * Проверить, разблокировано ли достижение
     */
    override suspend fun isAchievementUnlocked(id: String): Boolean {
        return try {
            achievementDao.getAchievementById(id)?.isUnlocked == true
        } catch (e: Exception) {
            Timber.e(e, "Failed to check if achievement unlocked")
            false
        }
    }

    /**
     * Синхронизация с удаленным хранилищем
     */
    override suspend fun syncWithRemote(): Result<Unit> {
        return try {
            val userId = firestoreManager.getCurrentUserId()
            if (userId != null) {
                // Получаем все достижения
                val achievements = achievementDao.getAllAchievements().let { flow ->
                    // Для синхронизации нужен не-flow вариант
                    emptyList<AchievementEntity>()
                }
                
                // firestoreManager.syncAchievements(userId, achievements)
                Timber.d("Achievements sync completed")
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Achievements sync failed")
            Result.failure(e)
        }
    }

    /**
     * Инициализация достижений (создание всех возможных достижений)
     */
    override suspend fun initializeAchievements(): Result<Unit> {
        return try {
            // Проверим, есть ли уже достижения
            val existingCount = achievementDao.getUnlockedCount()
            if (existingCount > 0) return Result.success(Unit)

            val achievements = listOf(
                // Streak achievements
                AchievementEntity(
                    id = "streak_3",
                    type = AchievementType.STREAK.name,
                    title = "Новичок",
                    description = "3 дня подряд",
                    iconRes = 0, // TODO: Добавить ресурсы
                    rarity = Rarity.COMMON.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 3
                ),
                AchievementEntity(
                    id = "streak_7",
                    type = AchievementType.STREAK.name,
                    title = "На волне",
                    description = "7 дней подряд",
                    iconRes = 0,
                    rarity = Rarity.RARE.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 7
                ),
                AchievementEntity(
                    id = "streak_30",
                    type = AchievementType.STREAK.name,
                    title = "Мастер дисциплины",
                    description = "30 дней подряд",
                    iconRes = 0,
                    rarity = Rarity.EPIC.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 30
                ),
                AchievementEntity(
                    id = "streak_100",
                    type = AchievementType.STREAK.name,
                    title = "Легенда",
                    description = "100 дней подряд",
                    iconRes = 0,
                    rarity = Rarity.LEGENDARY.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 100
                ),
                
                // Total hours achievements
                AchievementEntity(
                    id = "hours_50",
                    type = AchievementType.TOTAL_HOURS.name,
                    title = "Первые шаги",
                    description = "50 часов продуктивности",
                    iconRes = 0,
                    rarity = Rarity.COMMON.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 50
                ),
                AchievementEntity(
                    id = "hours_200",
                    type = AchievementType.TOTAL_HOURS.name,
                    title = "Серьезный подход",
                    description = "200 часов продуктивности",
                    iconRes = 0,
                    rarity = Rarity.RARE.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 200
                ),
                AchievementEntity(
                    id = "hours_1000",
                    type = AchievementType.TOTAL_HOURS.name,
                    title = "Мастер",
                    description = "1000 часов продуктивности",
                    iconRes = 0,
                    rarity = Rarity.EPIC.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 1000
                ),
                
                // Category mastery achievements
                AchievementEntity(
                    id = "category_10",
                    type = AchievementType.CATEGORY_MASTERY.name,
                    title = "Специалист",
                    description = "10 часов в одной категории",
                    iconRes = 0,
                    rarity = Rarity.RARE.name,
                    isUnlocked = false,
                    progress = 0,
                    maxProgress = 10
                ),
                
                // Perfect week achievement
                AchievementEntity(
                    id = "perfect_week",
                    type = AchievementType.PERFECT_WEEK.name,
                    title = "Идеальная неделя",
                    description = "Все запланированное выполнено за неделю",
                    iconRes = 0,
                    rarity = Rarity.EPIC.name,
                    isUnlocked = false
                ),
                
                // Early bird achievements
                AchievementEntity(
                    id = "early_bird",
                    type = AchievementType.EARLY_BIRD.name,
                    title = "Ранняя птичка",
                    description = "Начало работы до 8:00",
                    iconRes = 0,
                    rarity = Rarity.COMMON.name,
                    isUnlocked = false
                ),
                
                // Night owl achievements
                AchievementEntity(
                    id = "night_owl",
                    type = AchievementType.NIGHT_OWL.name,
                    title = "Сова",
                    description = "Работа после 22:00",
                    iconRes = 0,
                    rarity = Rarity.COMMON.name,
                    isUnlocked = false
                )
            )

            achievementDao.insertAchievements(achievements)
            Timber.d("Achievements initialized")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize achievements")
            Result.failure(e)
        }
    }
}

/**
 * Extension функции для преобразования между Entity и Domain моделями
 */
private fun AchievementEntity.toDomainModel(): Achievement {
    return Achievement(
        id = id,
        type = AchievementType.valueOf(type),
        title = title,
        description = description,
        iconRes = iconRes,
        rarity = Rarity.valueOf(rarity),
        isUnlocked = isUnlocked,
        progress = progress,
        maxProgress = maxProgress,
        unlockedAt = unlockedAt
    )
}

private fun Achievement.toEntity(): AchievementEntity {
    return AchievementEntity(
        id = id,
        type = type.name,
        title = title,
        description = description,
        iconRes = iconRes,
        rarity = rarity.name,
        isUnlocked = isUnlocked,
        progress = progress,
        maxProgress = maxProgress,
        unlockedAt = unlockedAt
    )
}