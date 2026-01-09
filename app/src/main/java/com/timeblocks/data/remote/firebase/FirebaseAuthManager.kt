package com.timeblocks.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.timeblocks.data.remote.dto.UserDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Менеджер для работы с Firebase Authentication.
 * Обрабатывает вход/регистрацию через Email/Password и Google Sign-In.
 */
class FirebaseAuthManager @Inject constructor(
    private val auth: FirebaseAuth
) {

    /**
     * Текущий ID пользователя или null
     */
    val currentUserId: String?
        get() = auth.currentUser?.uid

    /**
     * Проверка авторизации пользователя
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Получить Flow состояния авторизации
     */
    fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        
        auth.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Регистрация через Email/Password
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Timber.d("User registered: ${result.user?.uid}")
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Timber.e(e, "Sign up failed")
            Result.failure(e)
        }
    }

    /**
     * Вход через Email/Password
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Timber.d("User signed in: ${result.user?.uid}")
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Timber.e(e, "Sign in failed")
            Result.failure(e)
        }
    }

    /**
     * Вход через Google
     */
    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Timber.d("Google sign in successful: ${result.user?.uid}")
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Timber.e(e, "Google sign in failed")
            Result.failure(e)
        }
    }

    /**
     * Выход из аккаунта
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Timber.d("User signed out")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Sign out failed")
            Result.failure(e)
        }
    }

    /**
     * Удаление аккаунта
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            auth.currentUser?.delete()?.await()
            Timber.d("Account deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Account deletion failed")
            Result.failure(e)
        }
    }

    /**
     * Получить данные текущего пользователя
     */
    fun getCurrentUser(): UserDTO? {
        val user = auth.currentUser ?: return null
        
        return UserDTO(
            id = user.uid,
            email = user.email ?: "",
            displayName = user.displayName,
            photoUrl = user.photoUrl?.toString()
        )
    }
}