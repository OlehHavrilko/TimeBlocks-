package com.timeblocks.data.local.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneId

/**
 * Конвертеры для Room Database.
 * Преобразуют сложные типы в простые для хранения в БД.
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromTimeString(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun fromLocalDateTime(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun localDateTimeToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}