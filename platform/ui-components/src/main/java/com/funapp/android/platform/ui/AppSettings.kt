package com.funapp.android.platform.ui

import com.funapp.android.platform.ui.theme.AppearanceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettings {
    private val _appearanceMode = MutableStateFlow(AppearanceMode.SYSTEM)
    val appearanceMode: StateFlow<AppearanceMode> = _appearanceMode.asStateFlow()

    private val _featuredCarouselEnabled = MutableStateFlow(true)
    val featuredCarouselEnabled: StateFlow<Boolean> = _featuredCarouselEnabled.asStateFlow()

    private val _simulateErrorsEnabled = MutableStateFlow(false)
    val simulateErrorsEnabled: StateFlow<Boolean> = _simulateErrorsEnabled.asStateFlow()

    fun setAppearanceMode(mode: AppearanceMode) {
        _appearanceMode.value = mode
    }

    fun setFeaturedCarouselEnabled(enabled: Boolean) {
        _featuredCarouselEnabled.value = enabled
    }

    fun setSimulateErrorsEnabled(enabled: Boolean) {
        _simulateErrorsEnabled.value = enabled
    }

    fun resetAppearance() {
        _appearanceMode.value = AppearanceMode.SYSTEM
    }

    fun resetFeatureToggles() {
        _featuredCarouselEnabled.value = true
        _simulateErrorsEnabled.value = false
    }
}
