package com.timeblocks.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Экран настроек (заглушка для MVP)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Тема
            item {
                SettingsSection(
                    title = "Внешний вид",
                    icon = Icons.Default.Palette
                ) {
                    ThemeSettings()
                }
            }

            // Уведомления
            item {
                SettingsSection(
                    title = "Уведомления",
                    icon = Icons.Default.Notifications
                ) {
                    NotificationSettings()
                }
            }

            // Приватность
            item {
                SettingsSection(
                    title = "Приватность",
                    icon = Icons.Default.PrivacyTip
                ) {
                    PrivacySettings()
                }
            }
        }
    }
}

/**
 * Секция настроек
 */
@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        content()
    }
}

/**
 * Настройки темы
 */
@Composable
fun ThemeSettings() {
    var selectedTheme by remember { mutableStateOf("Системная") }
    val themes = listOf("Светлая", "Темная", "Системная")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        themes.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = theme)
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = { selectedTheme = theme }
                )
            }
        }
    }
}

/**
 * Настройки уведомлений
 */
@Composable
fun NotificationSettings() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingToggle(
            label = "Включить уведомления",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )
        SettingToggle(
            label = "Звук",
            checked = soundEnabled,
            onCheckedChange = { soundEnabled = it },
            enabled = notificationsEnabled
        )
        SettingToggle(
            label = "Вибрация",
            checked = vibrationEnabled,
            onCheckedChange = { vibrationEnabled = it },
            enabled = notificationsEnabled
        )
    }
}

/**
 * Настройки приватности
 */
@Composable
fun PrivacySettings() {
    var analyticsEnabled by remember { mutableStateOf(true) }
    var crashReportsEnabled by remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        SettingToggle(
            label = "Сбор аналитики",
            checked = analyticsEnabled,
            onCheckedChange = { analyticsEnabled = it }
        )
        SettingToggle(
            label = "Отправка отчетов об ошибках",
            checked = crashReportsEnabled,
            onCheckedChange = { crashReportsEnabled = it }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Мы собираем анонимную статистику для улучшения приложения. Ваши данные никогда не будут переданы третьим лицам.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Переключатель настроек
 */
@Composable
fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}