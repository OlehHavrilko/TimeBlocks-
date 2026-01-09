package com.timeblocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timeblocks.presentation.theme.TimeBlocksTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Главная активность приложения TimeBlocks.
 * Использует Jetpack Compose для UI и Hilt для DI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeBlocksTheme {
                // TODO: Navigation setup будет добавлен позже
            }
        }
    }
}