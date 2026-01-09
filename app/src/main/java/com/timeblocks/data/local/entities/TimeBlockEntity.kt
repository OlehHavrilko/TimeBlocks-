package com.timeblocks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * Сущность блока времени для локального хранения в Room Database.
 * Соответствует доменной модели TimeBlock.
 */
@Entity(tableName = "time_blocks")
data class TimeBlockEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val startTime: String, // LocalTime в строковом формате
    val endTime: String,   // LocalTime в строковом формате
    val categoryId: String,
    val date: String,      // LocalDate в строковом формате
    val isCompleted: Boolean = false,
    val actualStartTime: String? = null,
    val actualEndTime: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)