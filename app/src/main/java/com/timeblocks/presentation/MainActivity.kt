package com.timeblocks.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.timeblocks.presentation.navigation.TimeBlocksNavGraph
import com.timeblocks.presentation.theme.TimeBlocksTheme
import com.timeblocks.utils.getWorkerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Main Activity - Entry point of the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize WorkManager tasks
        initializeBackgroundTasks()

        setContent {
            TimeBlocksApp()
        }
    }

    /**
     * Initialize background tasks with WorkManager
     */
    private fun initializeBackgroundTasks() {
        lifecycleScope.launch {
            try {
                val workerManager = getWorkerManager()
                
                // Schedule periodic tasks
                workerManager.scheduleAchievementCheck()
                workerManager.scheduleStatisticsCalculation()
                workerManager.scheduleSync()
            } catch (e: Exception) {
                // Log error but don't crash
                e.printStackTrace()
            }
        }
    }
}

/**
 * Main app composable with theme
 */
@Composable
fun TimeBlocksApp() {
    val isDarkTheme = isSystemInDarkTheme()

    TimeBlocksTheme(
        darkTheme = isDarkTheme,
        dynamicColor = true
    ) {
        Surface(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            TimeBlocksNavGraph()
        }
    }
}