package com.timeblocks.domain.usecase.timeblock

import com.timeblocks.domain.model.TimeBlock
import com.timeblocks.domain.repository.TimeBlockRepository
import com.timeblocks.domain.repository.CategoryRepository
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Use case для создания блока времени.
 * Содержит бизнес-логику валидации и создания.
 */
class CreateTimeBlockUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository,
    private val categoryRepository: CategoryRepository
) {

    /**
     * Создать блок времени с валидацией
     */
    suspend operator fun invoke(
        title: String,
        description: String?,
        startTime: LocalTime,
        endTime: LocalTime,
        categoryId: String,
        date: LocalDate
    ): Result<TimeBlock> {
        // Валидация
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Название блока не может быть пустым"))
        }

        if (startTime >= endTime) {
            return Result.failure(IllegalArgumentException("Время начала должно быть раньше времени окончания"))
        }

        // Проверка существования категории
        val category = categoryRepository.getCategoryById(categoryId)
        if (category == null) {
            return Result.failure(IllegalArgumentException("Категория не найдена"))
        }

        // Создание блока
        val timeBlock = TimeBlock(
            title = title.trim(),
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            startTime = startTime,
            endTime = endTime,
            categoryId = categoryId,
            date = date
        )

        return timeBlockRepository.createBlock(timeBlock)
    }
}