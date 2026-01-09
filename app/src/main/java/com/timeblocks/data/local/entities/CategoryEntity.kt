package com.timeblocks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность категории для локального хранения в Room Database.
 * Соответствует доменной модели Category.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String, // HEX цвет в формате "#RRGGBB"
    val icon: String,  // emoji или название иконки
    val isDefault: Boolean = false,
    val order: Int = 0
)