package com.timeblocks

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Главный класс приложения TimeBlocks.
 * Настраивает Hilt для dependency injection
 * и Timber для логирования в debug режиме.
 */
@HiltAndroidApp
class TimeBlocksApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализация Timber для логирования в debug режиме
        Timber.plant(Timber.DebugTree())
    }
}