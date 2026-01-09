package com.timeblocks.domain.usecase.timeblock

import com.timeblocks.domain.model.TimeBlock
import com.timeblocks.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case для получения блоков времени за день.
 */
class GetDailyBlocksUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {

    /**
     * Получить блоки для конкретной даты
     */
    operator fun invoke(date: LocalDate): Flow<List<TimeBlock>> {
        return timeBlockRepository.getBlocksForDate(date)
            .map { blocks -> 
                // Сортируем по времени начала
                blocks.sortedBy { it.startTime } 
            }
    }

    /**
     * Получить завершенные блоки для даты
     */
    fun getCompleted(date: LocalDate): Flow<List<TimeBlock>> {
        return timeBlockRepository.getCompletedBlocksForDate(date)
            .map { blocks -> 
                blocks.sortedBy { it.startTime } 
            }
    }
}