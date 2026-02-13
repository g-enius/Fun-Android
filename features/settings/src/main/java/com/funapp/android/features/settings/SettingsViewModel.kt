package com.funapp.android.features.settings

import androidx.lifecycle.ViewModel
import com.funapp.android.model.FeatureFlag
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.platform.ui.theme.AppearanceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsState(
            appearanceMode = appSettings.appearanceMode.value,
            featuredCarouselEnabled = appSettings.featuredCarouselEnabled.value,
            simulateErrorsEnabled = appSettings.simulateErrorsEnabled.value
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onAppearanceModeChanged(mode: AppearanceMode) {
        _state.value = _state.value.copy(appearanceMode = mode)
        appSettings.setAppearanceMode(mode)
    }

    fun onFeatureFlagToggle(flag: FeatureFlag, enabled: Boolean) {
        when (flag) {
            FeatureFlag.FEATURED_CAROUSEL -> {
                _state.value = _state.value.copy(featuredCarouselEnabled = enabled)
                appSettings.setFeaturedCarouselEnabled(enabled)
            }
            FeatureFlag.SIMULATE_ERRORS -> {
                _state.value = _state.value.copy(simulateErrorsEnabled = enabled)
                appSettings.setSimulateErrorsEnabled(enabled)
            }
        }
    }

    fun resetAppearance() {
        _state.value = _state.value.copy(appearanceMode = AppearanceMode.SYSTEM)
        appSettings.resetAppearance()
    }

    fun resetFeatureToggles() {
        _state.value = _state.value.copy(
            featuredCarouselEnabled = true,
            simulateErrorsEnabled = false
        )
        appSettings.resetFeatureToggles()
    }
}
