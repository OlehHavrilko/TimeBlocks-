package com.timeblocks.data.local.dao

import androidx.room.*
import com.timeblocks.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с категориями в Room Database.
 */
@Dao
interface CategoryDao {

    /**
     * Получить все категории (отсортированные по order)
     */
    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Получить категорию по ID
     */
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    /**
     * Получить категории по именам
     */
    @Query("SELECT * FROM categories WHERE name IN (:names)")
    suspend fun getCategoriesByNames(names: List<String>): List<CategoryEntity>

    /**
     * Получить дефолтные категории
     */
    @Query("SELECT * FROM categories WHERE isDefault = 1 ORDER BY `order` ASC")
    suspend fun getDefaultCategories(): List<CategoryEntity>

    /**
     * Вставить категорию
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    /**
     * Вставить несколько категорий
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    /**
     * Обновить категорию
     */
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * Удалить категорию по ID
     */
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)

    /**
     * Получить количество категорий
     */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    /**
     * Проверить, существует ли категория с таким именем
     */
    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE name = :name)")
    suspend fun existsByName(name: String): Boolean
}