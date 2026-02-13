package com.funapp.android.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val viewsCount: Int = 0,
    val favoritesCount: Int = 0,
    val daysCount: Int = 0
)
