package com.funapp.android.features.settings

import androidx.lifecycle.ViewModel
import com.funapp.android.model.FeatureFlag
import com.funapp.android.platform.ui.theme.AppearanceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onAppearanceModeChanged(mode: AppearanceMode) {
        _state.value = _state.value.copy(appearanceMode = mode)
    }

    fun onFeatureFlagToggle(flag: FeatureFlag, enabled: Boolean) {
        when (flag) {
            FeatureFlag.FEATURED_CAROUSEL -> {
                _state.value = _state.value.copy(featuredCarouselEnabled = enabled)
            }
            FeatureFlag.SIMULATE_ERRORS -> {
                _state.value = _state.value.copy(simulateErrorsEnabled = enabled)
            }
        }
    }

    fun resetAppearance() {
        _state.value = _state.value.copy(appearanceMode = AppearanceMode.SYSTEM)
    }

    fun resetFeatureToggles() {
        _state.value = _state.value.copy(
            featuredCarouselEnabled = true,
            simulateErrorsEnabled = false
        )
    }
}
