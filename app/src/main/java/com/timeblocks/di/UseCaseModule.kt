package com.timeblocks.di

import com.timeblocks.domain.repository.*
import com.timeblocks.domain.usecase.timeblock.*
import com.timeblocks.domain.usecase.achievement.*
import com.timeblocks.domain.usecase.statistics.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для предоставления Use Cases.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * TimeBlock Use Cases
     */
    @Provides
    @Singleton
    fun provideCreateTimeBlockUseCase(
        timeBlockRepository: TimeBlockRepository,
        categoryRepository: CategoryRepository
    ): CreateTimeBlockUseCase {
        return CreateTimeBlockUseCase(timeBlockRepository, categoryRepository)
    }

    @Provides
    @Singleton
    fun provideGetDailyBlocksUseCase(
        timeBlockRepository: TimeBlockRepository
    ): GetDailyBlocksUseCase {
        return GetDailyBlocksUseCase(timeBlockRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateBlockStatusUseCase(
        timeBlockRepository: TimeBlockRepository
    ): UpdateBlockStatusUseCase {
        return UpdateBlockStatusUseCase(timeBlockRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteTimeBlockUseCase(
        timeBlockRepository: TimeBlockRepository
    ): DeleteTimeBlockUseCase {
        return DeleteTimeBlockUseCase(timeBlockRepository)
    }

    /**
     * Achievement Use Cases
     */
    @Provides
    @Singleton
    fun provideCheckAchievementsUseCase(
        achievementRepository: AchievementRepository,
        timeBlockRepository: TimeBlockRepository,
        userRepository: UserRepository
    ): CheckAchievementsUseCase {
        return CheckAchievementsUseCase(achievementRepository, timeBlockRepository, userRepository)
    }

    @Provides
    @Singleton
    fun provideUnlockAchievementUseCase(
        achievementRepository: AchievementRepository
    ): UnlockAchievementUseCase {
        return UnlockAchievementUseCase(achievementRepository)
    }

    /**
     * Statistics Use Cases
     */
    @Provides
    @Singleton
    fun provideCalculateStreakUseCase(
        timeBlockRepository: TimeBlockRepository
    ): CalculateStreakUseCase {
        return CalculateStreakUseCase(timeBlockRepository)
    }

    @Provides
    @Singleton
    fun provideGetStatisticsUseCase(
        timeBlockRepository: TimeBlockRepository
    ): GetStatisticsUseCase {
        return GetStatisticsUseCase(timeBlockRepository)
    }
}