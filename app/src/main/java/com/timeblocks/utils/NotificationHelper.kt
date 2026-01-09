package com.timeblocks.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.timeblocks.MainActivity
import com.timeblocks.R
import com.timeblocks.domain.model.TimeBlock
import javax.inject.Inject

/**
 * Хелпер для работы с уведомлениями.
 */
class NotificationHelper @Inject constructor(
    private val context: Context
) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Создать каналы уведомлений (для Android 8.0+)
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    Constants.Notifications.CHANNEL_ID_BLOCK_START,
                    "Начало блока",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Уведомление о начале нового блока времени"
                },
                NotificationChannel(
                    Constants.Notifications.CHANNEL_ID_BLOCK_END,
                    "Завершение блока",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Уведомление о завершении блока времени"
                },
                NotificationChannel(
                    Constants.Notifications.CHANNEL_ID_REMINDER,
                    "Напоминания",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Напоминания о предстоящих блоках"
                },
                NotificationChannel(
                    Constants.Notifications.CHANNEL_ID_ACHIEVEMENT,
                    "Достижения",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Уведомления о разблокировке достижений"
                }
            )

            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    /**
     * Показать уведомление о начале блока
     */
    fun showBlockStartNotification(block: TimeBlock) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_ID_BLOCK_START,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.Notifications.CHANNEL_ID_BLOCK_START)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Начался блок: ${block.title}")
            .setContentText("${DateTimeUtils.formatTime(block.startTime)} - ${DateTimeUtils.formatTime(block.endTime)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_ID_BLOCK_START, notification)
    }

    /**
     * Показать уведомление о завершении блока
     */
    fun showBlockEndNotification(block: TimeBlock) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_ID_BLOCK_END,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.Notifications.CHANNEL_ID_BLOCK_END)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Завершен блок: ${block.title}")
            .setContentText("Время завершить и перейти к следующему блоку")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_ID_BLOCK_END, notification)
    }

    /**
     * Показать уведомление о разблокировке достижения
     */
    fun showAchievementNotification(title: String, description: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_screen", "achievements")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_ID_ACHIEVEMENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.Notifications.CHANNEL_ID_ACHIEVEMENT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🏆 Достижение разблокировано!")
            .setContentText("$title: $description")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_ID_ACHIEVEMENT, notification)
    }

    /**
     * Показать напоминание о предстоящем блоке
     */
    fun showReminderNotification(block: TimeBlock, minutesBefore: Int = 5) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_ID_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.Notifications.CHANNEL_ID_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Напоминание")
            .setContentText("Через $minutesBefore минут начнется блок: ${block.title}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_ID_REMINDER, notification)
    }

    /**
     * Отменить уведомление по ID
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Отменить все уведомления
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}