package com.timeblocks.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Утилиты для работы с датой и временем.
 */
object DateTimeUtils {

    private val russianLocale = Locale("ru", "RU")

    /**
     * Форматтеры
     */
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(russianLocale)

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(russianLocale)

    val fullDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(russianLocale)

    /**
     * Форматировать дату в строку
     */
    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    /**
     * Форматировать время в строку
     */
    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    /**
     * Форматировать диапазон времени
     */
    fun formatTimeRange(start: LocalTime, end: LocalTime): String {
        return "${formatTime(start)} - ${formatTime(end)}"
    }

    /**
     * Получить русское название дня недели
     */
    fun getDayOfWeekName(date: LocalDate): String {
        return when (date.dayOfWeek.value) {
            1 -> "Понедельник"
            2 -> "Вторник"
            3 -> "Среда"
            4 -> "Четверг"
            5 -> "Пятница"
            6 -> "Суббота"
            7 -> "Воскресенье"
            else -> "Неизвестно"
        }
    }

    /**
     * Получить русское название месяца
     */
    fun getMonthName(date: LocalDate): String {
        return when (date.monthValue) {
            1 -> "Январь"
            2 -> "Февраль"
            3 -> "Март"
            4 -> "Апрель"
            5 -> "Май"
            6 -> "Июнь"
            7 -> "Июль"
            8 -> "Август"
            9 -> "Сентябрь"
            10 -> "Октябрь"
            11 -> "Ноябрь"
            12 -> "Декабрь"
            else -> "Неизвестно"
        }
    }

    /**
     * Проверить, является ли дата сегодняшней
     */
    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }

    /**
     * Проверить, является ли дата вчерашней
     */
    fun isYesterday(date: LocalDate): Boolean {
        return date == LocalDate.now().minusDays(1)
    }

    /**
     * Получить форматированную дату с относительным указанием (Сегодня/Вчера/Дата)
     */
    fun formatDateRelative(date: LocalDate): String {
        return when {
            isToday(date) -> "Сегодня"
            isYesterday(date) -> "Вчера"
            else -> formatDate(date)
        }
    }

    /**
     * Преобразовать строку времени в LocalTime
     */
    fun parseTime(timeString: String): LocalTime? {
        return try {
            LocalTime.parse(timeString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Преобразовать строку даты в LocalDate
     */
    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Получить текущую дату в формате строки
     */
    fun getCurrentDateString(): String {
        return LocalDate.now().toString()
    }

    /**
     * Получить текущее время в формате строки
     */
    fun getCurrentTimeString(): String {
        return LocalTime.now().toString()
    }

    /**
     * Проверить, находится ли время в диапазоне
     */
    fun isTimeInRange(time: LocalTime, start: LocalTime, end: LocalTime): Boolean {
        return !time.isBefore(start) && !time.isAfter(end)
    }

    /**
     * Получить разницу во времени в минутах
     */
    fun getDurationInMinutes(start: LocalTime, end: LocalTime): Long {
        return java.time.Duration.between(start, end).toMinutes()
    }

    /**
     * Форматировать длительность в человекочитаемый вид
     */
    fun formatDuration(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        
        return when {
            hours > 0 && mins > 0 -> "${hours}ч ${mins}м"
            hours > 0 -> "${hours}ч"
            else -> "${mins}м"
        }
    }

    /**
     * Получить список дат за последние N дней
     */
    fun getLastNDays(n: Int): List<LocalDate> {
        return (0 until n).map { LocalDate.now().minusDays(it.toLong()) }.reversed()
    }

    /**
     * Получить список дат за последние N недель
     */
    fun getLastNWeeks(n: Int): List<LocalDate> {
        return (0 until n).map { LocalDate.now().minusWeeks(it.toLong()) }.reversed()
    }

    /**
     * Получить список дат за последние N месяцев
     */
    fun getLastNMonths(n: Int): List<LocalDate> {
        return (0 until n).map { LocalDate.now().minusMonths(it.toLong()) }.reversed()
    }
}