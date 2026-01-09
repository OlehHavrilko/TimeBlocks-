package com.timeblocks.domain.repository

import com.timeblocks.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с категориями.
 */
interface CategoryRepository {

    /**
     * Получить все категории
     */
    fun getAllCategories(): Flow<List<Category>>

    /**
     * Получить категорию по ID
     */
    suspend fun getCategoryById(id: String): Category?

    /**
     * Создать категорию
     */
    suspend fun createCategory(category: Category): Result<Category>

    /**
     * Обновить категорию
     */
    suspend fun updateCategory(category: Category): Result<Category>

    /**
     * Удалить категорию
     */
    suspend fun deleteCategory(id: String): Result<Unit>

    /**
     * Получить количество категорий
     */
    suspend fun getCategoryCount(): Int

    /**
     * Проверить, можно ли создать новую категорию
     */
    suspend fun canCreateCategory(maxLimit: Int): Boolean

    /**
     * Синхронизация с удаленным хранилищем
     */
    suspend fun syncWithRemote(): Result<Unit>

    /**
     * Инициализация дефолтных категорий
     */
    suspend fun initializeDefaultCategories(): Result<Unit>
}