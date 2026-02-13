package com.funapp.android.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onAppearanceModeChanged = viewModel::onAppearanceModeChanged,
        onFeatureFlagToggle = viewModel::onFeatureFlagToggle,
        onResetAppearance = viewModel::resetAppearance,
        onResetFeatureToggles = viewModel::resetFeatureToggles
    )
}
