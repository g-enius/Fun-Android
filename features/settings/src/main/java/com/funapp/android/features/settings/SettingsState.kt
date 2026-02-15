package com.funapp.android.features.settings

import com.funapp.android.platform.ui.theme.AppearanceMode

data class SettingsState(
    val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
    val featuredCarouselEnabled: Boolean = true,
    val simulateErrorsEnabled: Boolean = false,
    val aiSummaryEnabled: Boolean = true
)
