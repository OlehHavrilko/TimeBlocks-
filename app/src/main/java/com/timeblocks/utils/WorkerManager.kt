package com.timeblocks.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.timeblocks.domain.usecase.achievement.CheckAchievementsUseCase
import com.timeblocks.domain.usecase.statistics.CalculateStreakUseCase
import com.timeblocks.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * WorkManager для фоновых задач TimeBlocks
 */

/**
 * Worker для проверки достижений
 */
@HiltWorker
class AchievementCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val checkAchievementsUseCase: CheckAchievementsUseCase,
    private val userRepository: UserRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                checkAchievementsUseCase(userId)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

/**
 * Worker для расчета статистики
 */
@HiltWorker
class StatisticsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val userRepository: UserRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                calculateStreakUseCase(userId, LocalDate.now())
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

/**
 * Worker для синхронизации с Firebase
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userRepository: UserRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                // Синхронизация данных
                userRepository.syncUserData(userId)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

/**
 * Управление WorkManager
 */
class WorkerManager(private val context: Context) {

    /**
     * Запланировать периодическую проверку достижений
     */
    fun scheduleAchievementCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AchievementCheckWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "achievement_check",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Запланировать периодический расчет статистики
     */
    fun scheduleStatisticsCalculation() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<StatisticsWorker>(
            repeatInterval = 12,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(30, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "statistics_calculation",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Запланировать периодическую синхронизацию
     */
    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "sync_data",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Запланировать отложенную проверку достижений (после создания блока)
     */
    fun scheduleDelayedAchievementCheck(delayMinutes: Long = 15) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AchievementCheckWorker>()
            .setConstraints(constraints)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "delayed_achievement_check",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Отменить все работы
     */
    fun cancelAllWork() {
        WorkManager.getInstance(context).cancelAllWork()
    }

    /**
     * Отменить конкретную работу
     */
    fun cancelWork(tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }
}

/**
 * Расширение для Context для удобного получения WorkerManager
 */
fun Context.getWorkerManager(): WorkerManager {
    return WorkerManager(this)
}