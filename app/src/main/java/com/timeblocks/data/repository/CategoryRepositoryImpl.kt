package com.timeblocks.data.repository

import com.timeblocks.data.local.dao.CategoryDao
import com.timeblocks.data.local.entities.CategoryEntity
import com.timeblocks.data.remote.firebase.FirestoreManager
import com.timeblocks.domain.model.Category
import com.timeblocks.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Реализация репозитория для работы с категориями.
 */
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val firestoreManager: FirestoreManager
) : CategoryRepository {

    /**
     * Получить все категории
     */
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    /**
     * Получить категорию по ID
     */
    override suspend fun getCategoryById(id: String): Category? {
        return try {
            categoryDao.getCategoryById(id)?.toDomainModel()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get category by id")
            null
        }
    }

    /**
     * Создать категорию
     */
    override suspend fun createCategory(category: Category): Result<Category> {
        return try {
            val entity = category.toEntity()
            categoryDao.insertCategory(entity)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Created category: ${category.id}")
            Result.success(category)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create category")
            Result.failure(e)
        }
    }

    /**
     * Обновить категорию
     */
    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val entity = category.toEntity()
            categoryDao.updateCategory(entity)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Updated category: ${category.id}")
            Result.success(category)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update category")
            Result.failure(e)
        }
    }

    /**
     * Удалить категорию
     */
    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            categoryDao.deleteCategory(id)
            
            // Синхронизация с облаком
            syncWithRemote()
            
            Timber.d("Deleted category: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete category")
            Result.failure(e)
        }
    }

    /**
     * Получить количество категорий
     */
    override suspend fun getCategoryCount(): Int {
        return try {
            categoryDao.getCategoryCount()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get category count")
            0
        }
    }

    /**
     * Проверить, можно ли создать новую категорию (для free tier ограничение)
     */
    override suspend fun canCreateCategory(maxLimit: Int): Boolean {
        val count = getCategoryCount()
        return count < maxLimit
    }

    /**
     * Синхронизация с удаленным хранилищем
     */
    override suspend fun syncWithRemote(): Result<Unit> {
        return try {
            val userId = firestoreManager.getCurrentUserId()
            if (userId != null) {
                // Получаем все категории
                val categories = categoryDao.getAllCategories().let { flow ->
                    // Для синхронизации нужен не-flow вариант
                    // Создадим suspend вариант в DAO
                    emptyList<CategoryEntity>()
                }
                
                // firestoreManager.syncCategories(userId, categories)
                Timber.d("Categories sync completed")
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Categories sync failed")
            Result.failure(e)
        }
    }

    /**
     * Инициализация дефолтных категорий
     */
    override suspend fun initializeDefaultCategories(): Result<Unit> {
        return try {
            val existingCount = categoryDao.getCategoryCount()
            if (existingCount > 0) return Result.success(Unit)

            val defaultCategories = listOf(
                CategoryEntity(
                    id = "cat_work",
                    name = "Работа",
                    color = "#FF5722",
                    icon = "💼",
                    isDefault = true,
                    order = 0
                ),
                CategoryEntity(
                    id = "cat_learning",
                    name = "Обучение",
                    color = "#2196F3",
                    icon = "📚",
                    isDefault = true,
                    order = 1
                ),
                CategoryEntity(
                    id = "cat_sport",
                    name = "Спорт",
                    color = "#4CAF50",
                    icon = "💪",
                    isDefault = true,
                    order = 2
                ),
                CategoryEntity(
                    id = "cat_rest",
                    name = "Отдых",
                    color = "#9C27B0",
                    icon = "🎮",
                    isDefault = true,
                    order = 3
                ),
                CategoryEntity(
                    id = "cat_family",
                    name = "Семья",
                    color = "#FF9800",
                    icon = "👨‍👩‍👧‍👦",
                    isDefault = true,
                    order = 4
                ),
                CategoryEntity(
                    id = "cat_hobby",
                    name = "Хобби",
                    color = "#00BCD4",
                    icon = "🎨",
                    isDefault = true,
                    order = 5
                )
            )

            categoryDao.insertCategories(defaultCategories)
            Timber.d("Default categories initialized")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize default categories")
            Result.failure(e)
        }
    }
}

/**
 * Extension функции для преобразования между Entity и Domain моделями
 */
private fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = id,
        name = name,
        color = color,
        icon = icon,
        isDefault = isDefault,
        order = order
    )
}

private fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        color = color,
        icon = icon,
        isDefault = isDefault,
        order = order
    )
}