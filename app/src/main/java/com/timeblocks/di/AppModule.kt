package com.timeblocks.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.timeblocks.data.local.dao.*
import com.timeblocks.data.local.database.AppDatabase
import com.timeblocks.data.remote.firebase.FirebaseAuthManager
import com.timeblocks.data.remote.firebase.FirestoreManager
import com.timeblocks.data.repository.*
import com.timeblocks.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Hilt для предоставления зависимостей на уровне приложения.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Предоставление Room Database
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "timeblocks_database"
        ).build()
    }

    /**
     * Предоставление DAO
     */
    @Provides
    fun provideTimeBlockDao(database: AppDatabase): TimeBlockDao = database.timeBlockDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideAchievementDao(database: AppDatabase): AchievementDao = database.achievementDao()

    @Provides
    fun provideUserSettingsDao(database: AppDatabase): UserSettingsDao = database.userSettingsDao()

    /**
     * Предоставление Firebase
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Предоставление Firebase Managers
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthManager(auth: FirebaseAuth): FirebaseAuthManager {
        return FirebaseAuthManager(auth)
    }

    @Provides
    @Singleton
    fun provideFirestoreManager(firestore: FirebaseFirestore): FirestoreManager {
        return FirestoreManager(firestore)
    }

    /**
     * Предоставление Repository Implementations
     */
    @Provides
    @Singleton
    fun provideTimeBlockRepository(
        timeBlockDao: TimeBlockDao,
        firestoreManager: FirestoreManager
    ): TimeBlockRepository {
        return TimeBlockRepositoryImpl(timeBlockDao, firestoreManager)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        firestoreManager: FirestoreManager
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao, firestoreManager)
    }

    @Provides
    @Singleton
    fun provideAchievementRepository(
        achievementDao: AchievementDao,
        firestoreManager: FirestoreManager
    ): AchievementRepository {
        return AchievementRepositoryImpl(achievementDao, firestoreManager)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userSettingsDao: UserSettingsDao,
        authManager: FirebaseAuthManager,
        firestoreManager: FirestoreManager
    ): UserRepository {
        return UserRepositoryImpl(userSettingsDao, authManager, firestoreManager)
    }
}