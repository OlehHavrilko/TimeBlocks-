package com.timeblocks.presentation.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timeblocks.domain.model.Achievement
import com.timeblocks.domain.model.Rarity

/**
 * Экран достижений (заглушка для MVP)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBack: () -> Unit
) {
    // Пример достижений
    val achievements = listOf(
        Achievement(
            id = "streak_3",
            type = com.timeblocks.domain.model.AchievementType.STREAK,
            title = "Новичок",
            description = "3 дня подряд",
            iconRes = 0,
            rarity = Rarity.COMMON,
            isUnlocked = true,
            progress = 3,
            maxProgress = 3
        ),
        Achievement(
            id = "streak_7",
            type = com.timeblocks.domain.model.AchievementType.STREAK,
            title = "На волне",
            description = "7 дней подряд",
            iconRes = 0,
            rarity = Rarity.RARE,
            isUnlocked = false,
            progress = 5,
            maxProgress = 7
        ),
        Achievement(
            id = "hours_50",
            type = com.timeblocks.domain.model.AchievementType.TOTAL_HOURS,
            title = "Первые шаги",
            description = "50 часов продуктивности",
            iconRes = 0,
            rarity = Rarity.COMMON,
            isUnlocked = true,
            progress = 50,
            maxProgress = 50
        ),
        Achievement(
            id = "early_bird",
            type = com.timeblocks.domain.model.AchievementType.EARLY_BIRD,
            title = "Ранняя птичка",
            description = "Начало работы до 8:00",
            iconRes = 0,
            rarity = Rarity.COMMON,
            isUnlocked = false,
            progress = 0,
            maxProgress = 1
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Достижения") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Streak display
            StreakDisplay(currentStreak = 3, maxStreak = 10)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Список достижений
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementItem(achievement = achievement)
                }
            }
        }
    }
}

/**
 * Отображение серии дней
 */
@Composable
fun StreakDisplay(
    currentStreak: Int,
    maxStreak: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔥 Текущая серия",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$currentStreak дней",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Лучший результат: $maxStreak дней",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Элемент достижения
 */
@Composable
fun AchievementItem(achievement: Achievement) {
    val rarityColor = when (achievement.rarity) {
        Rarity.COMMON -> Color(0xFF808080)
        Rarity.RARE -> Color(0xFF4169E1)
        Rarity.EPIC -> Color(0xFF9370DB)
        Rarity.LEGENDARY -> Color(0xFFFFD700)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = if (achievement.isUnlocked) {
            null
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Верхняя часть: иконка, название, редкость
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                    // Иконка (заглушка)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(rarityColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = rarityColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = achievement.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = achievement.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Статус
                    if (achievement.isUnlocked) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✓",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text(
                            text = achievement.rarity.name,
                            color = rarityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

            // Прогресс
            if (!achievement.isUnlocked && achievement.maxProgress > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = (achievement.progress.toFloat() / achievement.maxProgress),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = rarityColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${achievement.progress} / ${achievement.maxProgress}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}