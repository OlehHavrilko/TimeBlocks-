package com.timeblocks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeblocks.domain.model.TimeBlock
import com.timeblocks.domain.usecase.timeblock.GetDailyBlocksUseCase
import com.timeblocks.domain.usecase.timeblock.CreateTimeBlockUseCase
import com.timeblocks.domain.usecase.timeblock.DeleteTimeBlockUseCase
import com.timeblocks.domain.usecase.timeblock.UpdateBlockStatusUseCase
import com.timeblocks.domain.usecase.statistics.GetStatisticsUseCase
import com.timeblocks.domain.usecase.statistics.CalculateStreakUseCase
import com.timeblocks.domain.usecase.achievement.CheckAchievementsUseCase
import com.timeblocks.domain.repository.UserRepository
import com.timeblocks.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel для HomeScreen.
 * Управляет состоянием главного экрана и бизнес-логикой.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyBlocksUseCase: GetDailyBlocksUseCase,
    private val createTimeBlockUseCase: CreateTimeBlockUseCase,
    private val deleteTimeBlockUseCase: DeleteTimeBlockUseCase,
    private val updateBlockStatusUseCase: UpdateBlockStatusUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase,
    private val userRepository: UserRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        loadCurrentUser()
        loadTodayBlocks()
        loadTodayStats()
        checkAchievements()
    }

    /**
     * Загрузить текущего пользователя
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                _state.value = _state.value.copy(userId = userId)
                loadUserSettings(userId)
            } else {
                _events.emit(HomeEvent.NavigateToAuth)
            }
        }
    }

    /**
     * Загрузить настройки пользователя
     */
    private fun loadUserSettings(userId: String) {
        viewModelScope.launch {
            userRepository.getUserSettings(userId).collect { user ->
                user?.let {
                    _state.value = _state.value.copy(
                        isPremium = it.isPremium,
                        theme = it.theme,
                        notificationsEnabled = it.notificationsEnabled
                    )
                }
            }
        }
    }

    /**
     * Загрузить блоки на сегодня
     */
    private fun loadTodayBlocks() {
        viewModelScope.launch {
            Timber.d("loadTodayBlocks: Начало загрузки блоков для даты ${LocalDate.now()}")
            getDailyBlocksUseCase(LocalDate.now())
                .collect { blocks ->
                    Timber.d("loadTodayBlocks: Получено ${blocks.size} блоков")
                    blocks.forEach { block ->
                        Timber.d("  Блок: ${block.title} (${block.startTime} - ${block.endTime}), completed: ${block.isCompleted}")
                    }
                    _state.value = _state.value.copy(
                        todayBlocks = blocks,
                        isLoading = false
                    )
                    checkActiveBlocks(blocks)
                }
        }
    }

    /**
     * Проверить активные блоки и отправить уведомления
     */
    private fun checkActiveBlocks(blocks: List<TimeBlock>) {
        viewModelScope.launch {
            val now = LocalTime.now()
            val activeBlocks = blocks.filter { 
                it.startTime.isNowBetween(it.startTime, it.endTime) 
            }
            
            if (activeBlocks.isNotEmpty()) {
                val activeBlock = activeBlocks.first()
                if (_state.value.lastNotifiedBlockId != activeBlock.id) {
                    notificationHelper.showBlockStartNotification(activeBlock)
                    _state.value = _state.value.copy(lastNotifiedBlockId = activeBlock.id)
                }
            }
        }
    }

    /**
     * Загрузить статистику за сегодня
     */
    private fun loadTodayStats() {
        viewModelScope.launch {
            getStatisticsUseCase.getTodayStatsFlow()
                .collect { stats ->
                    _state.value = _state.value.copy(todayStats = stats)
                }
        }
    }

    /**
     * Проверить достижения
     */
    private fun checkAchievements() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                checkAchievementsUseCase(userId)
                    .onSuccess {
                        Timber.d("Achievements checked successfully")
                    }
                    .onFailure {
                        Timber.e(it, "Failed to check achievements")
                    }
            }
        }
    }

    /**
     * Создать новый блок времени
     */
    fun createBlock(
        title: String,
        description: String?,
        startTime: LocalTime,
        endTime: LocalTime,
        categoryId: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = createTimeBlockUseCase(
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                categoryId = categoryId,
                date = LocalDate.now()
            )

            result.onSuccess {
                _events.emit(HomeEvent.BlockCreated(it))
                loadTodayBlocks()
                loadTodayStats()
            }.onFailure {
                _events.emit(HomeEvent.Error(it.message ?: "Failed to create block"))
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * Удалить блок времени
     */
    fun deleteBlock(blockId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = deleteTimeBlockUseCase(blockId, LocalDate.now())

            result.onSuccess {
                _events.emit(HomeEvent.BlockDeleted)
                loadTodayBlocks()
                loadTodayStats()
            }.onFailure {
                _events.emit(HomeEvent.Error(it.message ?: "Failed to delete block"))
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * Начать выполнение блока
     */
    fun startBlock(blockId: String) {
        viewModelScope.launch {
            val result = updateBlockStatusUseCase(blockId, LocalDate.now().toString())
            
            result.onSuccess {
                _events.emit(HomeEvent.BlockStarted)
                loadTodayBlocks()
            }.onFailure {
                _events.emit(HomeEvent.Error(it.message ?: "Failed to start block"))
            }
        }
    }

    /**
     * Обновить дату
     */
    fun updateDate(date: LocalDate) {
        _state.value = _state.value.copy(currentDate = date)
        viewModelScope.launch {
            getDailyBlocksUseCase(date)
                .collect { blocks ->
                    _state.value = _state.value.copy(todayBlocks = blocks)
                }
        }
    }

    /**
     * Обновить тему
     */
    fun updateTheme(theme: String) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                userRepository.updateTheme(userId, theme)
            }
        }
    }

    /**
     * Выйти из аккаунта
     */
    fun signOut() {
        viewModelScope.launch {
            val result = userRepository.signOut()
            result.onSuccess {
                _events.emit(HomeEvent.NavigateToAuth)
            }.onFailure {
                _events.emit(HomeEvent.Error(it.message ?: "Failed to sign out"))
            }
        }
    }

    /**
     * Проверить статус Premium
     */
    fun checkPremiumStatus() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                val isPremium = userRepository.isPremium(userId)
                _state.value = _state.value.copy(isPremium = isPremium)
            }
        }
    }
}

/**
 * Состояние HomeScreen
 */
data class HomeState(
    val userId: String? = null,
    val currentDate: LocalDate = LocalDate.now(),
    val todayBlocks: List<TimeBlock> = emptyList(),
    val todayStats: com.timeblocks.domain.usecase.statistics.TodayStats? = null,
    val isPremium: Boolean = false,
    val theme: String = "system",
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val lastNotifiedBlockId: String? = null
)

/**
 * События HomeScreen
 */
sealed class HomeEvent {
    data class BlockCreated(val block: TimeBlock) : HomeEvent()
    object BlockDeleted : HomeEvent()
    object BlockStarted : HomeEvent()
    object NavigateToAuth : HomeEvent()
    data class Error(val message: String) : HomeEvent()
}