package com.timeblocks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeblocks.domain.repository.UserRepository
import com.timeblocks.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экранов аутентификации (Login и SignUp).
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    /**
     * Проверить авторизацию при запуске
     */
    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (userRepository.isUserLoggedIn()) {
                _events.emit(AuthEvent.LoginSuccess)
            }
        }
    }

    /**
     * Вход по Email/Password
     */
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            if (!validateEmail(email)) return@launch
            if (!validatePassword(password)) return@launch

            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = userRepository.signInWithEmail(email, password)

            result.onSuccess {
                _events.emit(AuthEvent.LoginSuccess)
            }.onFailure {
                _state.value = _state.value.copy(
                    error = it.message ?: "Authentication failed"
                )
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * Регистрация по Email/Password
     */
    fun signUpWithEmail(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (!validateEmail(email)) return@launch
            if (!validatePassword(password)) return@launch
            if (!validateConfirmPassword(password, confirmPassword)) return@launch

            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = userRepository.signUpWithEmail(email, password)

            result.onSuccess {
                _events.emit(AuthEvent.RegistrationSuccess)
            }.onFailure {
                _state.value = _state.value.copy(
                    error = it.message ?: "Registration failed"
                )
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * Вход через Google
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = userRepository.signInWithGoogle(idToken)

            result.onSuccess {
                _events.emit(AuthEvent.LoginSuccess)
            }.onFailure {
                _state.value = _state.value.copy(
                    error = it.message ?: "Google sign-in failed"
                )
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * Валидация Email
     */
    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            _state.value = _state.value.copy(error = "Email не может быть пустым")
            return false
        }
        if (!email.isValidEmail()) {
            _state.value = _state.value.copy(error = "Неверный формат Email")
            return false
        }
        return true
    }

    /**
     * Валидация пароля
     */
    private fun validatePassword(password: String): Boolean {
        if (password.isBlank()) {
            _state.value = _state.value.copy(error = "Пароль не может быть пустым")
            return false
        }
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "Пароль должен содержать минимум 6 символов")
            return false
        }
        return true
    }

    /**
     * Валидация подтверждения пароля
     */
    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        if (password != confirmPassword) {
            _state.value = _state.value.copy(error = "Пароли не совпадают")
            return false
        }
        return true
    }

    /**
     * Сброс ошибки
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    /**
     * Установить режим экрана
     */
    fun setScreenMode(mode: AuthScreenMode) {
        _state.value = _state.value.copy(screenMode = mode, error = null)
    }
}

/**
 * Состояние экрана аутентификации
 */
data class AuthState(
    val screenMode: AuthScreenMode = AuthScreenMode.LOGIN,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Режим экрана аутентификации
 */
enum class AuthScreenMode {
    LOGIN,
    SIGN_UP
}

/**
 * События аутентификации
 */
sealed class AuthEvent {
    object LoginSuccess : AuthEvent()
    object RegistrationSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}