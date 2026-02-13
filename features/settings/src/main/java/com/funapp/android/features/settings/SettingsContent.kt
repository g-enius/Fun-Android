package com.funapp.android.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.funapp.android.model.FeatureFlag
import com.funapp.android.platform.ui.theme.AppearanceMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    state: SettingsState,
    onAppearanceModeChanged: (AppearanceMode) -> Unit,
    onFeatureFlagToggle: (FeatureFlag, Boolean) -> Unit,
    onResetAppearance: () -> Unit,
    onResetFeatureToggles: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Appearance section
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppearanceMode.entries.forEach { mode ->
                            val selected = state.appearanceMode == mode
                            FilterChip(
                                selected = selected,
                                onClick = { onAppearanceModeChanged(mode) },
                                label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Feature Toggles section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Feature Toggles",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                SettingRow(
                    title = "Featured Carousel",
                    description = "Enable the featured carousel on Home",
                    checked = state.featuredCarouselEnabled,
                    onCheckedChange = { onFeatureFlagToggle(FeatureFlag.FEATURED_CAROUSEL, it) }
                )
            }
            item {
                SettingRow(
                    title = "Simulate Errors",
                    description = "Simulate network errors for testing",
                    checked = state.simulateErrorsEnabled,
                    onCheckedChange = { onFeatureFlagToggle(FeatureFlag.SIMULATE_ERRORS, it) }
                )
            }

            // Reset section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reset",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        TextButton(onClick = onResetAppearance) {
                            Text(
                                text = "Reset Appearance",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        TextButton(onClick = onResetFeatureToggles) {
                            Text(
                                text = "Reset Feature Toggles",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // System Info section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "System Info",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow(label = "Version", value = "1.0.0")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(label = "Build", value = "1")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
