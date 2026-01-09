package com.timeblocks.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.timeblocks.domain.model.TimeBlock
import com.timeblocks.presentation.viewmodel.HomeState
import com.timeblocks.utils.DateTimeUtils
import com.timeblocks.utils.toColor
import com.timeblocks.utils.toFormattedString
import com.timeblocks.utils.toRelativeString
import java.time.LocalDate
import java.time.LocalTime

/**
 * HomeScreen - Главный экран приложения
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onCreateBlock: (String, String?, LocalTime, LocalTime, String) -> Unit,
    onDeleteBlock: (String) -> Unit,
    onStartBlock: (String) -> Unit,
    onUpdateDate: (LocalDate) -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedBlock by remember { mutableStateOf<TimeBlock?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = state.currentDate.toRelativeString(),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, "Профиль")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Добавить блок")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Домой") },
                    label = { Text("Домой") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, "Планнер") },
                    label = { Text("Планнер") },
                    selected = false,
                    onClick = onNavigateToPlanner
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, "Статистика") },
                    label = { Text("Статистика") },
                    selected = false,
                    onClick = onNavigateToStatistics
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, "Достижения") },
                    label = { Text("Достижения") },
                    selected = false,
                    onClick = onNavigateToAchievements
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Статистика за сегодня
            if (state.todayStats != null) {
                TodayStatsCard(stats = state.todayStats!!)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Информация о Premium
            if (!state.isPremium) {
                PremiumBanner(onNavigateToPremium = { 
                    // Navigate to paywall
                })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Список блоков
            if (state.todayBlocks.isEmpty()) {
                EmptyState()
            } else {
                TimeBlocksList(
                    blocks = state.todayBlocks,
                    onDelete = onDeleteBlock,
                    onStart = onStartBlock
                )
            }
        }
    }

    // Диалог создания блока
    if (showCreateDialog) {
        CreateBlockDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, description, start, end, category ->
                onCreateBlock(title, description, start, end, category)
                showCreateDialog = false
            }
        )
    }
}

/**
 * Карточка статистики за сегодня
 */
@Composable
fun TodayStatsCard(stats: com.timeblocks.domain.usecase.statistics.TodayStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Сегодня",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Всего блоков",
                    value = stats.totalBlocks.toString()
                )
                StatItem(
                    label = "Выполнено",
                    value = stats.completedBlocks.toString()
                )
                StatItem(
                    label = "Минут",
                    value = stats.totalMinutes.toString()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (stats.completionRate / 100f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${stats.completionRate}% завершено",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Элемент статистики
 */
@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Баннер Premium
 */
@Composable
fun PremiumBanner(onNavigateToPremium: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToPremium() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Получи Premium",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Неограниченные категории и расширенная статистика",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

/**
 * Список блоков времени
 */
@Composable
fun TimeBlocksList(
    blocks: List<TimeBlock>,
    onDelete: (String) -> Unit,
    onStart: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(blocks, key = { it.id }) { block ->
            TimeBlockItem(
                block = block,
                onDelete = { onDelete(block.id) },
                onStart = { onStart(block.id) }
            )
        }
    }
}

/**
 * Элемент блока времени
 */
@Composable
fun TimeBlockItem(
    block: TimeBlock,
    onDelete: () -> Unit,
    onStart: () -> Unit
) {
    val categoryColor = block.categoryId.toColor()
    val isCompleted = block.isCompleted
    val isNowActive = LocalTime.now().isNowBetween(block.startTime, block.endTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else if (isNowActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isNowActive) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок и категория
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(categoryColor)
                )
            }

            // Описание
            block.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Время
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${block.startTime.toFormattedString()} - ${block.endTime.toFormattedString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Выполнено",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Действия
            if (!isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isNowActive) {
                        Button(
                            onClick = onStart,
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Начать")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Delete, "Удалить")
                    }
                }
            }
        }
    }
}

/**
 * Пустое состояние
 */
@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет запланированных блоков",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Нажмите + чтобы добавить первый блок",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

/**
 * Диалог создания блока
 */
@Composable
fun CreateBlockDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, LocalTime, LocalTime, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(LocalTime.now()) }
    var endTime by remember { mutableStateOf(LocalTime.now().plusHours(1)) }
    var selectedCategory by remember { mutableStateOf("cat_work") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый блок") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание (опционально)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimePickerField(
                        label = "Начало",
                        time = startTime,
                        onTimeSelected = { startTime = it },
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        label = "Конец",
                        time = endTime,
                        onTimeSelected = { endTime = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                CategorySelector(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, description.takeIf { it.isNotBlank() }, startTime, endTime, selectedCategory)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

/**
 * Поле выбора времени
 */
@Composable
fun TimePickerField(
    label: String,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = time.toFormattedString(),
        onValueChange = { },
        label = { Text(label) },
        modifier = modifier.clickable { showPicker = true },
        readOnly = true,
        trailingIcon = {
            Icon(Icons.Default.Schedule, null)
        }
    )

    if (showPicker) {
        // В реальном приложении здесь должен быть Material Time Picker
        // Для MVP используем упрощенный вариант
        showPicker = false
    }
}

/**
 * Выбор категории
 */
@Composable
fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "cat_work" to "💼 Работа",
        "cat_learning" to "📚 Обучение",
        "cat_sport" to "💪 Спорт",
        "cat_rest" to "🎮 Отдых",
        "cat_family" to "👨‍👩‍👧‍👦 Семья",
        "cat_hobby" to "🎨 Хобби"
    )

    Column {
        Text(
            text = "Категория",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (id, name) ->
                CategoryChip(
                    name = name,
                    isSelected = selectedCategory == id,
                    onClick = { onCategorySelected(id) }
                )
            }
        }
    }
}

/**
 * Чип категории
 */
@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = name,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = contentColor,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
}

/**
 * Вспомогательный компонент для FlowRow (упрощенная версия)
 */
@Composable
fun FlowRow(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.Start
    ) {
        // В реальном приложении нужен кастомный Layout для FlowRow
        // Для MVP используем Column с переносом
        content()
    }
}