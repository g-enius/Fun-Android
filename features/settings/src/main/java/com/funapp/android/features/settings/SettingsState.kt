package com.funapp.android.features.settings

data class SettingsState(
    val searchEnabled: Boolean = true,
    val favoritesEnabled: Boolean = true,
    val profileEditingEnabled: Boolean = false
)
