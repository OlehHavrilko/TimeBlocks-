package com.timeblocks.domain.usecase.timeblock

import com.timeblocks.domain.repository.TimeBlockRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case для удаления блока времени.
 */
class DeleteTimeBlockUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {

    /**
     * Удалить блок времени
     */
    suspend operator fun invoke(blockId: String, date: LocalDate): Result<Unit> {
        if (blockId.isBlank()) {
            return Result.failure(IllegalArgumentException("ID блока не может быть пустым"))
        }

        return timeBlockRepository.deleteBlock(blockId, date)
    }
}