package com.funapp.android.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.platform.ui.utils.viewModelFactory

@Composable
fun SettingsScreen(
    appSettings: AppSettings
) {
    val viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory { SettingsViewModel(appSettings) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onAppearanceModeChanged = viewModel::onAppearanceModeChanged,
        onFeatureFlagToggle = viewModel::onFeatureFlagToggle,
        onResetAppearance = viewModel::resetAppearance,
        onResetFeatureToggles = viewModel::resetFeatureToggles
    )
}
