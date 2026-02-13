package com.funapp.android.features.settings

import androidx.lifecycle.ViewModel
import com.funapp.android.model.FeatureFlag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onFeatureFlagToggle(flag: FeatureFlag, enabled: Boolean) {
        when (flag) {
            FeatureFlag.ENABLE_SEARCH -> {
                _state.value = _state.value.copy(searchEnabled = enabled)
            }
            FeatureFlag.ENABLE_FAVORITES -> {
                _state.value = _state.value.copy(favoritesEnabled = enabled)
            }
            FeatureFlag.ENABLE_PROFILE_EDITING -> {
                _state.value = _state.value.copy(profileEditingEnabled = enabled)
            }
        }
    }
}
