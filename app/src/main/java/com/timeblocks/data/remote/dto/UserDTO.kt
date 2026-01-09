package com.timeblocks.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Data Transfer Object для пользователя в Firebase Firestore.
 * Содержит данные, которые синхронизируются в облако.
 */
data class UserDTO(
    @DocumentId
    val id: String = "",
    
    @PropertyName("email")
    val email: String = "",
    
    @PropertyName("displayName")
    val displayName: String? = null,
    
    @PropertyName("photoUrl")
    val photoUrl: String? = null,
    
    @PropertyName("isPremium")
    val isPremium: Boolean = false,
    
    @PropertyName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @PropertyName("lastLogin")
    val lastLogin: Long = System.currentTimeMillis(),
    
    @PropertyName("syncData")
    val syncData: Boolean = true,
    
    @PropertyName("version")
    val version: Int = 1
) {
    // Пустой конструктор для Firebase
    constructor() : this("", "", null, null, false, 0, 0, true, 1)
}