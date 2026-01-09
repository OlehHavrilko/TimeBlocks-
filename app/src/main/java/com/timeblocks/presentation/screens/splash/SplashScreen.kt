package com.timeblocks.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timeblocks.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для Splash Screen
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : androidx.lifecycle.ViewModel() {

    fun checkAuthStatus(onComplete: (Boolean) -> Unit) {
        val isLoggedIn = userRepository.isUserLoggedIn()
        onComplete(isLoggedIn)
    }
}

/**
 * Splash Screen - Первый экран приложения
 */
@Composable
fun SplashScreen(
    onAuthCheck: (Boolean) -> Unit
) {
    // Анимация
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(1500) // Задержка для анимации
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6750A4),
                        Color(0xFF42A5F5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Логотип/Название
            Text(
                text = "TimeBlocks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 42.sp,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Подзаголовок
            Text(
                text = "Управляй своим временем",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Индикатор загрузки
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
        }

        // Проверка авторизации после анимации
        if (showContent) {
            LaunchedEffect(Unit) {
                launch {
                    // Имитация проверки (в реальном приложении можно сразу)
                    delay(500)
                    // В реальном приложении здесь будет проверка через ViewModel
                    // Для MVP сразу вызываем колбэк
                    onAuthCheck(false) // false - показываем экран входа
                }
            }
        }
    }
}