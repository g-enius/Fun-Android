package com.funapp.android.features.profile

import com.funapp.android.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null
)
