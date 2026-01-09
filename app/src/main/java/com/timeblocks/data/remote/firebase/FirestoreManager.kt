package com.timeblocks.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.timeblocks.data.local.entities.TimeBlockEntity
import com.timeblocks.data.local.entities.CategoryEntity
import com.timeblocks.data.local.entities.AchievementEntity
import com.timeblocks.data.remote.dto.UserDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Менеджер для работы с Firebase Firestore.
 * Обеспечивает синхронизацию данных между локальной БД и облаком.
 */
class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Получить коллекцию пользователя
     */
    private fun getUserCollection(userId: String) = firestore.collection("users").document(userId)

    /**
     * Сохранить данные пользователя
     */
    suspend fun saveUser(user: UserDTO): Result<Unit> {
        return try {
            getUserCollection(user.id).set(user, SetOptions.merge()).await()
            Timber.d("User data saved to Firestore: ${user.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save user data")
            Result.failure(e)
        }
    }

    /**
     * Получить данные пользователя
     */
    suspend fun getUser(userId: String): Result<UserDTO> {
        return try {
            val snapshot = getUserCollection(userId).get().await()
            val user = snapshot.toObject(UserDTO::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get user data")
            Result.failure(e)
        }
    }

    /**
     * Синхронизировать блоки времени
     */
    suspend fun syncTimeBlocks(userId: String, blocks: List<TimeBlockEntity>): Result<Unit> {
        return try {
            val collection = getUserCollection(userId).collection("timeBlocks")
            
            // Удалить старые данные и добавить новые
            val batch = firestore.batch()
            
            // Сначала получим существующие документы для удаления
            val existingDocs = collection.get().await()
            existingDocs.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            
            // Добавим новые блоки
            blocks.forEach { block ->
                val docRef = collection.document(block.id)
                batch.set(docRef, block)
            }
            
            batch.commit().await()
            Timber.d("Synced ${blocks.size} time blocks")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync time blocks")
            Result.failure(e)
        }
    }

    /**
     * Синхронизировать категории
     */
    suspend fun syncCategories(userId: String, categories: List<CategoryEntity>): Result<Unit> {
        return try {
            val collection = getUserCollection(userId).collection("categories")
            
            val batch = firestore.batch()
            
            val existingDocs = collection.get().await()
            existingDocs.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            
            categories.forEach { category ->
                val docRef = collection.document(category.id)
                batch.set(docRef, category)
            }
            
            batch.commit().await()
            Timber.d("Synced ${categories.size} categories")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync categories")
            Result.failure(e)
        }
    }

    /**
     * Синхронизировать достижения
     */
    suspend fun syncAchievements(userId: String, achievements: List<AchievementEntity>): Result<Unit> {
        return try {
            val collection = getUserCollection(userId).collection("achievements")
            
            val batch = firestore.batch()
            
            val existingDocs = collection.get().await()
            existingDocs.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            
            achievements.forEach { achievement ->
                val docRef = collection.document(achievement.id)
                batch.set(docRef, achievement)
            }
            
            batch.commit().await()
            Timber.d("Synced ${achievements.size} achievements")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync achievements")
            Result.failure(e)
        }
    }

    /**
     * Получить все данные для синхронизации
     */
    suspend fun getAllSyncData(userId: String): Result<SyncData> {
        return try {
            val userDoc = getUserCollection(userId).get().await()
            val user = userDoc.toObject(UserDTO::class.java)

            val timeBlocks = getUserCollection(userId).collection("timeBlocks")
                .get().await()
                .documents.mapNotNull { it.toObject(TimeBlockEntity::class.java) }

            val categories = getUserCollection(userId).collection("categories")
                .get().await()
                .documents.mapNotNull { it.toObject(CategoryEntity::class.java) }

            val achievements = getUserCollection(userId).collection("achievements")
                .get().await()
                .documents.mapNotNull { it.toObject(AchievementEntity::class.java) }

            Result.success(
                SyncData(
                    user = user,
                    timeBlocks = timeBlocks,
                    categories = categories,
                    achievements = achievements
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to get all sync data")
            Result.failure(e)
        }
    }

    /**
     * Слушать изменения в данных пользователя (для реальной синхронизации)
     */
    fun listenToUserChanges(userId: String): Flow<UserDTO?> = callbackFlow {
        val listener = getUserCollection(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.e(error, "Error listening to user changes")
                trySend(null)
                return@addSnapshotListener
            }
            
            val user = snapshot?.toObject(UserDTO::class.java)
            trySend(user)
        }
        
        awaitClose {
            listener.remove()
        }
    }
}

/**
 * Вспомогательный data class для синхронизации
 */
data class SyncData(
    val user: UserDTO?,
    val timeBlocks: List<TimeBlockEntity>,
    val categories: List<CategoryEntity>,
    val achievements: List<AchievementEntity>
)