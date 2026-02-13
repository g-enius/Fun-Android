package com.funapp.android.features.settings

import com.funapp.android.model.FeatureFlag
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

    @Test
    fun `initial state has correct defaults`() {
        val viewModel = SettingsViewModel()
        val state = viewModel.state.value

        assertTrue(state.searchEnabled)
        assertTrue(state.favoritesEnabled)
        assertFalse(state.profileEditingEnabled)
    }

    @Test
    fun `toggle search flag updates state`() {
        val viewModel = SettingsViewModel()

        viewModel.onFeatureFlagToggle(FeatureFlag.ENABLE_SEARCH, false)

        assertFalse(viewModel.state.value.searchEnabled)
    }

    @Test
    fun `toggle favorites flag updates state`() {
        val viewModel = SettingsViewModel()

        viewModel.onFeatureFlagToggle(FeatureFlag.ENABLE_FAVORITES, false)

        assertFalse(viewModel.state.value.favoritesEnabled)
    }

    @Test
    fun `toggle profile editing flag updates state`() {
        val viewModel = SettingsViewModel()

        viewModel.onFeatureFlagToggle(FeatureFlag.ENABLE_PROFILE_EDITING, true)

        assertTrue(viewModel.state.value.profileEditingEnabled)
    }
}
