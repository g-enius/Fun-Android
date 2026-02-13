package com.funapp.android.features.settings

import com.funapp.android.model.FeatureFlag
import com.funapp.android.platform.ui.theme.AppearanceMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

    @Test
    fun `initial state has correct defaults`() {
        val viewModel = SettingsViewModel()
        val state = viewModel.state.value

        assertEquals(AppearanceMode.SYSTEM, state.appearanceMode)
        assertTrue(state.featuredCarouselEnabled)
        assertFalse(state.simulateErrorsEnabled)
    }

    @Test
    fun `toggle featured carousel flag updates state`() {
        val viewModel = SettingsViewModel()

        viewModel.onFeatureFlagToggle(FeatureFlag.FEATURED_CAROUSEL, false)

        assertFalse(viewModel.state.value.featuredCarouselEnabled)
    }

    @Test
    fun `toggle simulate errors flag updates state`() {
        val viewModel = SettingsViewModel()

        viewModel.onFeatureFlagToggle(FeatureFlag.SIMULATE_ERRORS, true)

        assertTrue(viewModel.state.value.simulateErrorsEnabled)
    }

    @Test
    fun `change appearance mode updates state`() {
        val viewModel = SettingsViewModel()

        viewModel.onAppearanceModeChanged(AppearanceMode.DARK)

        assertEquals(AppearanceMode.DARK, viewModel.state.value.appearanceMode)
    }

    @Test
    fun `reset appearance restores system mode`() {
        val viewModel = SettingsViewModel()
        viewModel.onAppearanceModeChanged(AppearanceMode.DARK)

        viewModel.resetAppearance()

        assertEquals(AppearanceMode.SYSTEM, viewModel.state.value.appearanceMode)
    }

    @Test
    fun `reset feature toggles restores defaults`() {
        val viewModel = SettingsViewModel()
        viewModel.onFeatureFlagToggle(FeatureFlag.FEATURED_CAROUSEL, false)
        viewModel.onFeatureFlagToggle(FeatureFlag.SIMULATE_ERRORS, true)

        viewModel.resetFeatureToggles()

        assertTrue(viewModel.state.value.featuredCarouselEnabled)
        assertFalse(viewModel.state.value.simulateErrorsEnabled)
    }
}
