package com.timeblocks.utils

/**
 * Константы приложения TimeBlocks.
 */
object Constants {

    /**
     * Preferences keys
     */
    object Preferences {
        const val APP_PREFERENCES = "timeblocks_preferences"
        const val KEY_USER_ID = "user_id"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_THEME = "theme"
        const val KEY_LANGUAGE = "language"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_LAST_SYNC = "last_sync"
    }

    /**
     * Firebase collections
     */
    object Firebase {
        const val USERS_COLLECTION = "users"
        const val TIME_BLOCKS_COLLECTION = "timeBlocks"
        const val CATEGORIES_COLLECTION = "categories"
        const val ACHIEVEMENTS_COLLECTION = "achievements"
    }

    /**
     * Limits and constraints
     */
    object Limits {
        const val MAX_CATEGORIES_FREE = 3
        const val MAX_CATEGORIES_PREMIUM = 999
        const val MIN_BLOCK_DURATION_MINUTES = 15
        const val MAX_BLOCKS_PER_DAY = 96 // 24 hours * 4 (15 min blocks)
    }

    /**
     * Notification channels
     */
    object Notifications {
        const val CHANNEL_ID_BLOCK_START = "block_start"
        const val CHANNEL_ID_BLOCK_END = "block_end"
        const val CHANNEL_ID_REMINDER = "reminder"
        const val CHANNEL_ID_ACHIEVEMENT = "achievement"
        
        const val NOTIFICATION_ID_BLOCK_START = 1001
        const val NOTIFICATION_ID_BLOCK_END = 1002
        const val NOTIFICATION_ID_REMINDER = 1003
        const val NOTIFICATION_ID_ACHIEVEMENT = 1004
    }

    /**
     * Billing products
     */
    object Billing {
        const val PRODUCT_MONTHLY = "timeblocks_premium_monthly"
        const val PRODUCT_YEARLY = "timeblocks_premium_yearly"
        
        const val PRICE_MONTHLY = 2.99
        const val PRICE_YEARLY = 24.99
    }

    /**
     * Analytics events
     */
    object Analytics {
        const val EVENT_BLOCK_CREATED = "block_created"
        const val EVENT_BLOCK_COMPLETED = "block_completed"
        const val EVENT_ACHIEVEMENT_UNLOCKED = "achievement_unlocked"
        const val EVENT_SIGN_IN = "sign_in"
        const val EVENT_SIGN_UP = "sign_up"
        const val EVENT_PREMIUM_PURCHASED = "premium_purchased"
    }

    /**
     * Time constants
     */
    object Time {
        const val MILLIS_IN_SECOND = 1000L
        const val SECONDS_IN_MINUTE = 60L
        const val MINUTES_IN_HOUR = 60L
        const val HOURS_IN_DAY = 24L
        
        const val MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE
        const val MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR
        const val MILLIS_IN_DAY = MILLIS_IN_HOUR * HOURS_IN_DAY
    }
}