package com.timeblocks.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.timeblocks.domain.model.Category
import java.time.LocalDate
import java.time.LocalTime

/**
 * Extension функции для упрощения кода.
 */

/**
 * Преобразовать HEX цвет в Compose Color
 */
fun String.toColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color.Gray
    }
}

/**
 * Проверить, является ли дата сегодняшней
 */
fun LocalDate.isToday(): Boolean {
    return this == LocalDate.now()
}

/**
 * Проверить, является ли дата вчерашней
 */
fun LocalDate.isYesterday(): Boolean {
    return this == LocalDate.now().minusDays(1)
}

/**
 * Получить форматированную дату с относительным указанием
 */
@Composable
fun LocalDate.toRelativeString(): String {
    return when {
        isToday() -> "Сегодня"
        isYesterday() -> "Вчера"
        else -> DateTimeUtils.formatDate(this)
    }
}

/**
 * Проверить, активен ли блок сейчас
 */
fun LocalTime.isNowBetween(start: LocalTime, end: LocalTime): Boolean {
    return !this.isBefore(start) && !this.isAfter(end)
}

/**
 * Получить цвет категории как Compose Color
 */
fun Category.toComposeColor(): Color {
    return this.color.toColor()
}

/**
 * Форматировать время как строку "HH:mm"
 */
fun LocalTime.toFormattedString(): String {
    return DateTimeUtils.formatTime(this)
}

/**
 * Форматировать дату как строку
 */
fun LocalDate.toFormattedString(): String {
    return DateTimeUtils.formatDate(this)
}

/**
 * Получить разницу во времени в минутах
 */
fun LocalTime.differenceInMinutes(other: LocalTime): Long {
    return java.time.Duration.between(this, other).toMinutes()
}

/**
 * Проверить, является ли строка валидным email
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Обрезать строку до максимальной длины с добавлением многоточия
 */
fun String.truncate(maxLength: Int): String {
    return if (length > maxLength) {
        substring(0, maxLength - 1) + "…"
    } else {
        this
    }
}

/**
 * Получить прогресс в процентах
 */
fun Int.toPercent(total: Int): Int {
    if (total == 0) return 0
    return ((this.toDouble() / total) * 100).toInt().coerceIn(0, 100)
}

/**
 * Проверить, является ли число четным
 */
fun Int.isEven(): Boolean = this % 2 == 0

/**
 * Проверить, является ли число нечетным
 */
fun Int.isOdd(): Boolean = this % 2 != 0

/**
 * Получить цвет редкости как Compose Color
 */
fun com.timeblocks.domain.model.Rarity.toColor(): Color {
    return when (this) {
        com.timeblocks.domain.model.Rarity.COMMON -> Color(0xFF808080)
        com.timeblocks.domain.model.Rarity.RARE -> Color(0xFF4169E1)
        com.timeblocks.domain.model.Rarity.EPIC -> Color(0xFF9370DB)
        com.timeblocks.domain.model.Rarity.LEGENDARY -> Color(0xFFFFD700)
    }
}