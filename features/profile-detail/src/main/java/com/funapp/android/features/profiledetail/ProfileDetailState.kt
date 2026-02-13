package com.funapp.android.features.profiledetail

import com.funapp.android.model.UserProfile

data class ProfileDetailState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null
)
