package com.timeblocks.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

/**
 * Доменная модель категории.
 * Используется для цветового кодирования блоков времени.
 */
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String, // HEX цвет в формате "#RRGGBB"
    val icon: String,  // emoji или название иконки
    val isDefault: Boolean = false,
    val order: Int = 0
) {
    /**
     * Преобразовать цвет в Compose Color
     */
    fun toComposeColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(color))
        } catch (e: Exception) {
            Color.Gray
        }
    }
    
    /**
     * Проверить, является ли цветом по умолчанию
     */
    fun isDefaultCategory(): Boolean {
        return isDefault
    }
}